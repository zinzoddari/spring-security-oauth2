package nextstep.app.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class Oauth2LoginProperties {

    private Map<String, OAuth2Provider> providers;

    public OAuth2Provider getProvider(final String provider) {
        return providers.get(provider);
    }

    public OAuth2Provider getGithub() {
        return providers.get("github");
    }

    public OAuth2Provider getGoogle() {
        return providers.get("google");
    }

    public Map<String, OAuth2Provider> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, OAuth2Provider> providers) {
        this.providers = providers;
    }

    public static class OAuth2Provider {
        private String clientId;

        private String secretKey;

        private String loginRequestUri;

        private String loginRedirectUri;

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setLoginRequestUri(String loginRequestUri) {
            this.loginRequestUri = loginRequestUri;
        }

        public void setLoginRedirectUri(String loginRedirectUri) {
            this.loginRedirectUri = loginRedirectUri;
        }

        public String getClientId() {
            return clientId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public String getLoginRequestUri() {
            return loginRequestUri;
        }

        public String getLoginRedirectUri() {
            return loginRedirectUri;
        }
    }
}
