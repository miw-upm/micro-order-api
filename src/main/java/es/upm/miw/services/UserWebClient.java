package es.upm.miw.services;

import es.upm.miw.data.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = UserWebClient.MICRO_USER_API)
public interface UserWebClient {

    String USERS_IDENTITY_ID = "/users/{identity}";
    String MICRO_USER_API = "micro-user-api";

    @GetMapping(USERS_IDENTITY_ID)
    User readUserByIdentity(@PathVariable String identity);
}
