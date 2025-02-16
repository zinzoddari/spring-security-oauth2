package nextstep.security.authentication.domain;

import org.springframework.web.util.UriComponentsBuilder;

public class GithubLoginRedirectRequest {
    private final String responseType;

    private final String clientId;

    private final String scope;

    private final String redirectUri;

    protected GithubLoginRedirectRequest(String responseType, String clientId, String scope, String redirectUri) {
        this.responseType = responseType;
        this.clientId = clientId;
        this.scope = scope;
        this.redirectUri = redirectUri;
    }

    public static GithubLoginRedirectRequest created(final String responseType, final String clientId, final String scope, final String redirectUri) {
        return new GithubLoginRedirectRequest(responseType, clientId, scope, redirectUri);
    }

    public String getRedirectUri() {
        return UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", responseType)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();
    }
}
