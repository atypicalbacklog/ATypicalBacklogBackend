package at.atypicalbacklog.management.external.igdb.service;

import at.atypicalbacklog.management.external.igdb.dto.IgdbTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class IgdbAuthService {

    static final String CLIENT_ID = "wr7lq3xkgeelq1raperxmhslv11h6l";
    private static final String CLIENT_SECRET = "5vs6i99bbmxhse76gx1356mikbtnml";

    private final RestTemplate rest = new RestTemplate();

    private String accessToken;
    private Instant expiresAt;

    public synchronized String getAccessToken() {

        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        String url =
            "https://id.twitch.tv/oauth2/token"
          + "?client_id=" + CLIENT_ID
          + "&client_secret=" + CLIENT_SECRET
          + "&grant_type=client_credentials";

        IgdbTokenResponse response =
            rest.postForObject(url, null, IgdbTokenResponse.class);

        this.accessToken = response.access_token();
        this.expiresAt = Instant.now().plusSeconds(response.expires_in() - 60);
    }
}