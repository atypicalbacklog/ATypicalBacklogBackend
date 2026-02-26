package at.atypicalbacklog.management.external.igdb.service;

import at.atypicalbacklog.management.external.igdb.dto.IgdbTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class IgdbAuthService {

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate rest = new RestTemplate();

    public IgdbAuthService(
            @Value("${app.igdb.client-id}") String clientId,
            @Value("${app.igdb.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private String accessToken;
    private Instant expiresAt;

    public String getClientId() {
        return clientId;
    }

    public synchronized String getAccessToken() {

        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        String url =
            "https://id.twitch.tv/oauth2/token"
          + "?client_id=" + clientId
          + "&client_secret=" + clientSecret
          + "&grant_type=client_credentials";

        IgdbTokenResponse response =
            rest.postForObject(url, null, IgdbTokenResponse.class);

        this.accessToken = response.access_token();
        this.expiresAt = Instant.now().plusSeconds(response.expires_in() - 60);
    }
}