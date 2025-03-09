package es.upm.miw.services;

import es.upm.miw.configuration.KafkaMockTestConfig;
import es.upm.miw.data.models.Order;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Import(KafkaMockTestConfig.class)
@ActiveProfiles("test")
class MessageProducerServiceTest {

    @Autowired
    private MessageProducerService messageProducerService;

    @Autowired
    private Producer<String, String> producer;
    @Captor
    private ArgumentCaptor<ProducerRecord<String, String>> captor;

    @Test
    void testSendOrder(){
        Order order = Order.builder()
                .identity("id")
                .productId("product-123")
                .build();
        BDDMockito.given(producer.send(captor.capture())).willReturn(CompletableFuture.completedFuture(null));
        messageProducerService.sendOrder(order);
        assertThat(captor.getValue().key()).isEqualTo("id");
    }
}
