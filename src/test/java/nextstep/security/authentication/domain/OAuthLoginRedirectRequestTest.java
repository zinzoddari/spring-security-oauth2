package nextstep.security.authentication.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthLoginRedirectRequestTest {

    @Test
    @DisplayName("성공적으로, Github 로그인을 위한 리다이렉트 정보를 생성합니다.")
    void success() {
        //given
        final String responseType = "code";
        final String clientId = "123456";
        final String scope = "read:user";
        final String redirectUri = "http://localhost:8080/index";

        final String requestUri = "https://github.com/login/oauth/authorize";

        final String expectedResult =
                String.format("%s?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s"
                        , requestUri, clientId, responseType, scope, redirectUri);

        //when
        final String result = OAuthLoginRedirectRequest.created(responseType, clientId, scope, redirectUri)
                .getRedirectUri(requestUri);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }
}