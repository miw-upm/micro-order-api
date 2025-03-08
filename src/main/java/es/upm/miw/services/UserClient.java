package es.upm.miw.services;

import es.upm.miw.data.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = UserClient.API_USER)
public interface UserClient {

    String USERS_IDENTITY_ID = "/users/{identity}";
    String API_USER = "api-user";

    @GetMapping(USERS_IDENTITY_ID)
    User readUserByIdentity(@PathVariable String identity);
}
