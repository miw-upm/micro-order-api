package es.upm.miw.services;

import es.upm.miw.data.models.Order;
import es.upm.miw.data.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final UserWebClient userWebClient;
    private final MessageProducerService messageProducerService;

    @Autowired
    public OrderService(UserWebClient userWebClient, MessageProducerService messageProducerService) {
        this.userWebClient = userWebClient;
        this.messageProducerService = messageProducerService;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
    }

    public Order create(Order order) {
        User user = this.userWebClient.readUserByIdentity(order.getUser().getIdentity());
        Order orderCreated = Order.builder()
                .identity("1")
                .productId(order.getProductId())
                .createdAt(LocalDateTime.now())
                .user(user).build();
        this.messageProducerService.sendOrder(orderCreated);
        return orderCreated;
    }
}
