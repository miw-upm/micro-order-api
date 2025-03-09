package es.upm.miw.services;

import es.upm.miw.data.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public interface UserWebClient {
    User readUserByIdentity(String identity);
}

