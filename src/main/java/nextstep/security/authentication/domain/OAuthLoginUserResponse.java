package nextstep.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthLoginUserResponse {

    private String email;

    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private OAuthLoginUserResponse() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
