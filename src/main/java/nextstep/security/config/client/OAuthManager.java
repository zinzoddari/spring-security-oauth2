package nextstep.security.config.client;

import com.fasterxml.jackson.core.type.TypeReference;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.OAuthLoginAccessTokenRequest;
import nextstep.security.authentication.domain.OAuthLoginAccessTokenResponse;
import nextstep.security.authentication.domain.OAuthLoginUserResponse;
import org.springframework.http.HttpHeaders;

import java.util.List;

public class OAuthManager {

    private final Oauth2LoginProperties oauth2LoginProperties;
    private final OAuthClient oAuthClient;

    public OAuthManager(Oauth2LoginProperties oauth2LoginProperties) {
        this.oauth2LoginProperties = oauth2LoginProperties;
        this.oAuthClient = new OAuthClient();
    }

    public OAuthLoginAccessTokenResponse requestAccessToken(final String provider, final String code) {
        final Oauth2LoginProperties.OAuth2Provider oAuth2Provider = oauth2LoginProperties.getProvider(provider);

        final OAuthLoginAccessTokenRequest accessTokenRequest
                = OAuthLoginAccessTokenRequest.created(oAuth2Provider.getClientId(), oAuth2Provider.getSecretKey(), code, oAuth2Provider.getGrantType(), oAuth2Provider.getLoginRedirectUri());

        return oAuthClient.post(oAuth2Provider.getTokenRequestUri(), accessTokenRequest, new TypeReference<>() { });
    }

    public OAuthLoginUserResponse getUserInfo(final String userRequestUri, final OAuthLoginAccessTokenResponse tokenResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of(String.join(" ", tokenResponse.getTokenType(), tokenResponse.getAccessToken())));

        return oAuthClient.get(userRequestUri, headers, new TypeReference<>() { });
    }
}
