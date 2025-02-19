package nextstep.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubLoginAccessTokenRequest {

    @JsonProperty("client-id")
    private String clientId;

    @JsonProperty("client-secret")
    private String clientSecret;

    private String code;

    private GithubLoginAccessTokenRequest() {
    }

    public static GithubLoginAccessTokenRequest created(final String clientId, final String clientSecret, final String code) {
        GithubLoginAccessTokenRequest request = new GithubLoginAccessTokenRequest();

        request.clientId = clientId;
        request.clientSecret = clientSecret;
        request.code = code;

        return request;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCode() {
        return code;
    }
}
