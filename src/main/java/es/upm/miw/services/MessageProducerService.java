package es.upm.miw.services;

import es.upm.miw.data.models.Order;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static es.upm.miw.configuration.KafkaStreamsConfig.TOPIC_ORDER_IN;

@Service
public class MessageProducerService {
    private final Producer<String, String> producer;

    @Autowired
    public MessageProducerService(Producer<String, String> producer) {
        this.producer = producer;
    }

    public void sendOrder(Order order) {
        producer.send(new ProducerRecord<>(TOPIC_ORDER_IN, order.getIdentity(), order.toString()));
    }
}
