package at.atypicalbacklog.management.external.igdb;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IgdbClient {

    private static final String BASE_URL = "https://api.igdb.com/v4";

    private final RestTemplate rest = new RestTemplate();
    private final IgdbAuthService auth;

    public IgdbClient(IgdbAuthService auth) {
        this.auth = auth;
    }

    public String post(String endpoint, String body) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", IgdbAuthService.CLIENT_ID);
        headers.setBearerAuth(auth.getAccessToken());
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return rest.postForObject(
            BASE_URL + endpoint,
            entity,
            String.class
        );
    }
}