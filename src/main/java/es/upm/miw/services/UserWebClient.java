package es.upm.miw.services;

import es.upm.miw.data.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserWebClient {
    User readUserByIdentity( String identity);
}
