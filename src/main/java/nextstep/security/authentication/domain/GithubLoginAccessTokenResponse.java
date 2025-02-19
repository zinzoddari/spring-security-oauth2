package nextstep.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubLoginAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    private GithubLoginAccessTokenResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
