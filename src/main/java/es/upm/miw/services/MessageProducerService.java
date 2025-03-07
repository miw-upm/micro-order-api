package es.upm.miw.services;

import es.upm.miw.models.Order;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {
    private static final String TOPIC = "order-in";
    private final Producer<String, String> producer;

    @Autowired
    public MessageProducerService(Producer<String, String> producer) {
        this.producer = producer;
    }

    public void sendOrder(Order order) {
        producer.send(new ProducerRecord<>(TOPIC, order.getIdentity(), order.toString()));
    }
}
