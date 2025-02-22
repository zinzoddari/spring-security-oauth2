package nextstep.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthLoginAccessTokenRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    private String code;

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    private OAuthLoginAccessTokenRequest() {
    }

    public static OAuthLoginAccessTokenRequest created(final String clientId, final String clientSecret, final String code, final String grantType, final String redirectUri) {
        OAuthLoginAccessTokenRequest request = new OAuthLoginAccessTokenRequest();

        request.clientId = clientId;
        request.clientSecret = clientSecret;
        request.code = code;
        request.grantType = grantType;
        request.redirectUri = redirectUri;

        return request;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setCode(String code) {
        this.code = code;
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
