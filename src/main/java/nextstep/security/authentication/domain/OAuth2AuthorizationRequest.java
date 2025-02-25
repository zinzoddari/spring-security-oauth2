package nextstep.security.authentication.domain;

import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthorizationRequest {

    private String responseType;

    private String clientId;

    private String scope;

    private String redirectUri;

    private String loginRequestUri;

    public static OAuth2AuthorizationRequest created(final ClientRegistration clientRegistration) {
        OAuth2AuthorizationRequest request = new OAuth2AuthorizationRequest();

        request.responseType = "code";
        request.clientId = clientRegistration.getClientId();
        request.scope = clientRegistration.getScope();
        request.redirectUri = clientRegistration.getLoginRedirectUri();
        request.loginRequestUri = clientRegistration.getLoginRequestUri();

        return request;
    }

    public String getRedirectUri() {
        return UriComponentsBuilder.fromHttpUrl(loginRequestUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();
    }
}
