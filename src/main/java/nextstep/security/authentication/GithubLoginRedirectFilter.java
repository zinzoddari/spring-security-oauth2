package nextstep.security.authentication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.GithubLoginProperties;
import nextstep.security.authentication.domain.GithubLoginRedirectRequest;

import java.io.IOException;

public class GithubLoginRedirectFilter implements Filter {

    private static final String GITHUB_LOGIN_REDIRECT_METHOD = "GET";
    private static final String GITHUB_LOGIN_REDIRECT_URI = "/oauth2/authorization/github";

    private final GithubLoginProperties githubLoginProperties;

    public GithubLoginRedirectFilter(GithubLoginProperties githubLoginProperties) {
        this.githubLoginProperties = githubLoginProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        final String httpMethod = httpServletRequest.getMethod();
        final String requestUri = httpServletRequest.getRequestURI();

        if (isGithubLogin(httpMethod, requestUri)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            final GithubLoginRedirectRequest redirectRequest = createdRedirectRequest();

            httpServletResponse.sendRedirect(redirectRequest.getRedirectUri());
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isGithubLogin(final String httpMethod, final String requestUri) {
        return GITHUB_LOGIN_REDIRECT_METHOD.equals(httpMethod) && GITHUB_LOGIN_REDIRECT_URI.startsWith(requestUri);
    }

    private GithubLoginRedirectRequest createdRedirectRequest() {
        final String clientId = githubLoginProperties.getSecretKey();
        final String responseType = "code";
        final String scope = "read:user";
        final String redirectUri = "http://localhost:8080/login/oauth2/code/github";

        return GithubLoginRedirectRequest.created(responseType, clientId, scope, redirectUri);
    }
}
