package es.upm.miw.webclients;

import es.upm.miw.data.models.User;
import es.upm.miw.resorces.exceptionshandler.BadGatewayException;
import es.upm.miw.services.UserWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service(value = "userWebClient")
public class UserWebClientImpl implements UserWebClient {
    public static final String USERS_IDENTITY_ID = "/users/{identity}";
    private final String microUserApiUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public UserWebClientImpl(@Value("${miw.url.micro-user-api}") String microUserApiUrl) {
        this.microUserApiUrl = microUserApiUrl;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public User readUserByIdentity(String identity) {
        String url = microUserApiUrl + USERS_IDENTITY_ID;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // ResourceAccessException: "Connection refused" or  "Read timed out" or HttpClientErrorException
        ResponseEntity<User> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                User.class,
                identity
        );

        if (response.getBody() == null) {
            throw new BadGatewayException("User a null");
        }
        return response.getBody();
    }
}
