package es.upm.miw.services;

import es.upm.miw.models.Order;
import es.upm.miw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final UserClient userClient;
    private final MessageProducerService messageProducerService;

    @Autowired
    public OrderService(MessageProducerService messageProducerService, UserClient userClient) {
        this.messageProducerService = messageProducerService;
        this.userClient = userClient;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
    }

    public Order create(Order order) {
        User user = this.userClient.getUser(order.getUser().getIdentity());
        Order orderCreated = Order.builder()
                .identity("1")
                .productId(order.getProductId())
                .createdAt(LocalDateTime.now())
                .user(user).build();
        this.messageProducerService.sendOrder(orderCreated);
        return orderCreated;
    }
}
