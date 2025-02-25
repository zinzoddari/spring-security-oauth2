package nextstep.security.authentication.domain;

import nextstep.app.security.Oauth2LoginProperties;

public class ClientRegistration {

    private String clientId;

    private String secretKey;

    private String scope;

    private String grantType;

    private String loginRequestUri;

    private String loginRedirectUri;

    private String tokenRequestUri;

    private String userRequestUri;

    public static ClientRegistration created(final Oauth2LoginProperties.OAuth2Provider properties) {
        ClientRegistration clientRegistration = new ClientRegistration();

        clientRegistration.clientId = properties.getClientId();
        clientRegistration.secretKey = properties.getSecretKey();
        clientRegistration.scope = properties.getScope();
        clientRegistration.grantType = properties.getGrantType();
        clientRegistration.loginRequestUri = properties.getLoginRequestUri();
        clientRegistration.loginRedirectUri = properties.getLoginRedirectUri();
        clientRegistration.loginRedirectUri = properties.getLoginRedirectUri();
        clientRegistration.tokenRequestUri = properties.getTokenRequestUri();
        clientRegistration.userRequestUri = properties.getUserRequestUri();

        return clientRegistration;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getScope() {
        return scope;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getLoginRequestUri() {
        return loginRequestUri;
    }

    public String getLoginRedirectUri() {
        return loginRedirectUri;
    }

    public String getTokenRequestUri() {
        return tokenRequestUri;
    }

    public String getUserRequestUri() {
        return userRequestUri;
    }
}
