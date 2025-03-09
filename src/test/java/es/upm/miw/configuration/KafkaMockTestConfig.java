package es.upm.miw.configuration;

import org.apache.kafka.clients.producer.Producer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class KafkaMockTestConfig {

    @Bean
    @Profile("test")
    public Producer<String, String> kafkaProducerMock() {
        return Mockito.mock(Producer.class);
    }
}

