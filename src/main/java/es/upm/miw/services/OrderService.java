package es.upm.miw.services;

import es.upm.miw.data.models.Order;
import es.upm.miw.data.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final UserClient userClient;


    @Autowired
    public OrderService(UserClient userClient) {

        this.userClient = userClient;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
    }

    public Order create(Order order) {
        User user = this.userClient.readUserByIdentity(order.getUser().getIdentity());
        return Order.builder()
                .identity("1")
                .productId(order.getProductId())
                .createdAt(LocalDateTime.now())
                .user(user).build();
    }
}
