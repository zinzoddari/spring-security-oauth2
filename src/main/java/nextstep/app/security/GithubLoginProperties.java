package nextstep.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubLoginProperties {

    private final String secretKey;

    public GithubLoginProperties(@Value("${oauth2.github.secret-key}") final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
