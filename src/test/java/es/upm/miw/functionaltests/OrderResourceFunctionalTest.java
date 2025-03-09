package es.upm.miw.functionaltests;

import es.upm.miw.configuration.KafkaMockTestConfig;
import es.upm.miw.data.models.Order;
import es.upm.miw.data.models.User;
import es.upm.miw.resorces.OrderResource;
import es.upm.miw.services.UserWebClient;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(KafkaMockTestConfig.class)
@ActiveProfiles("test")
class OrderResourceFunctionalTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderResource orderResource;

    @MockitoBean
    private UserWebClient userWebClient;
    @Autowired
    private Producer<String, String> producer;

    @Test
    void testCreateOrder() {
        BDDMockito.given(this.userWebClient.readUserByIdentity(anyString()))
                .willAnswer(invocation ->
                        User.builder().identity(invocation.getArgument(0)).name("mock").email("mock@gmail.com").build());
        BDDMockito.given(producer.send(any())).willReturn(CompletableFuture.completedFuture(null));

        Order requestOrder = Order.builder()
                .productId("product-123")
                .user(User.builder().identity("user-identity-123").build())
                .build();

        ResponseEntity<Order> response = restTemplate.postForEntity(
                OrderResource.ORDERS,
                requestOrder,
                Order.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(order -> {
                    assertThat(order.getIdentity()).isNotNull();
                    assertThat(order.getProductId()).isEqualTo("product-123");
                    assertThat(order.getUser()).isNotNull();
                    assertThat(order.getCreatedAt()).isNotNull();
                });
    }

}
