package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import nextstep.app.security.GithubLoginProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.mock;

class GithubLoginRedirectFilterTest {
    private GithubLoginRedirectFilter filter;
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private FilterChain chain;

    @BeforeEach
    void init() {
        final String clientId = "123456";
        final String secretKey = "123456";
        final GithubLoginProperties properties = new GithubLoginProperties(clientId, secretKey);

        filter = new GithubLoginRedirectFilter(properties);

        chain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Github 로그인에 대한 요청인 경우, 관련 리다이렉트 URI를 반환합니다.")
    void returnRedirect() throws ServletException, IOException {
        //given
        final String httpMethod = "GET";
        final String requestUri = "/oauth2/authorization/github";

        final String expectedRedirectUri = "https://github.com/login/oauth/authorize?client_id=123456&response_type=code&scope=read:user&redirect_uri=http://localhost:8080/login/oauth2/code/github";

        request.setMethod(httpMethod);
        request.setRequestURI(requestUri);

        //when
        filter.doFilter(request, response, chain);

        //then
        assertSoftly(it -> {
            it.assertThat(HttpStatus.resolve(response.getStatus()).is3xxRedirection()).isTrue();
            it.assertThat(response.getRedirectedUrl()).isEqualTo(expectedRedirectUri);
        });
    }

    @Test
    @DisplayName("Github 로그인 요청이 아닌 경우, 리다이렉트 URI을 반환하지 않습니다.")
    void notGithubLogin() throws ServletException, IOException {
        final String httpMethod = "GET";
        final String requestUri = "/test";

        request.setMethod(httpMethod);
        request.setRequestURI(requestUri);

        //when
        filter.doFilter(request, response, chain);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
