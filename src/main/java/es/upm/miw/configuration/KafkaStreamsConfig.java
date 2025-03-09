package es.upm.miw.configuration;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Profile("!test")
@Log4j2
@Configuration
public class KafkaStreamsConfig {
    public static final String TOPIC_ORDER_IN = "order-in";
    public static final String TOPIC_ORDER_OUT = "order-out";
    public static final int PARTITIONS = 1;
    public static final short REPLICATION_FACTOR = 1;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    @Bean
    public Producer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Bean
    public KafkaStreams kafkaStreams() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        createTopicsIfNotExists(bootstrapServers, TOPIC_ORDER_IN);
        createTopicsIfNotExists(bootstrapServers, TOPIC_ORDER_OUT);
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(TOPIC_ORDER_IN);
        stream.to(TOPIC_ORDER_OUT);
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        return streams;
    }

    private void createTopicsIfNotExists(String bootstrapServers, String topicName) {
        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(adminProps)) {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                log.debug("El tópico '{}' ya existe.", topicName);
            } else {
                NewTopic newTopic = new NewTopic(topicName, PARTITIONS, REPLICATION_FACTOR);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                log.info("Tópico '{}' creado correctamente.", topicName);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("❌ Error al gestionar el tópico: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
