package es.upm.miw.services;

import es.upm.miw.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-user")
public interface UserClient {
    @GetMapping("/users/{identity}")
    User getUser(@PathVariable String identity);
}
