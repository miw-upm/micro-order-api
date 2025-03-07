package es.upm.miw.services;

import es.upm.miw.models.Order;
import es.upm.miw.models.User;
import es.upm.miw.rest.exceptionshandler.BadGatewayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final String userApiUrl;

    private final RestTemplate restTemplate;
    private final MessageProducerService messageProducerService;

    @Autowired
    public OrderService(@Value("${miw.url.user-api}") String userApiUrl, MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
        this.restTemplate = new RestTemplate(requestFactory);
        this.userApiUrl = userApiUrl;
    }

    public Order create(Order order) {
        String url = userApiUrl + "/users/" + order.getUser().getIdentity();
        // ResourceAccessException: "Connection refused" or  "Read timed out" or HttpClientErrorException
        ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);

        if (response.getBody() == null) {
            throw new BadGatewayException("User a null");
        }

        Order orderCreated = Order.builder()
                .identity("1")
                .productId(order.getProductId())
                .createdAt(LocalDateTime.now())
                .user(response.getBody()).build();
        this.messageProducerService.sendOrder(orderCreated);
        return orderCreated;
    }
}
