package nextstep.security.authentication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.GithubLoginRedirectRequest;

import java.io.IOException;

public class GithubLoginRedirectFilter implements Filter {

    private static final String OAUTH2_LOGIN_REDIRECT_METHOD = "GET";
    private static final String OAUTH2_LOGIN_REDIRECT_URI = "/oauth2/authorization/";

    private final Oauth2LoginProperties oauth2LoginProperties;

    public GithubLoginRedirectFilter(Oauth2LoginProperties oauth2LoginProperties) {
        this.oauth2LoginProperties = oauth2LoginProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        final String httpMethod = httpServletRequest.getMethod();
        final String requestUri = httpServletRequest.getRequestURI();

        if (isGithubLogin(httpMethod, requestUri)) {
            final String provider = requestUri.substring(requestUri.indexOf(OAUTH2_LOGIN_REDIRECT_URI));

            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            final GithubLoginRedirectRequest redirectRequest = createdRedirectRequest(provider);

            httpServletResponse.sendRedirect(redirectRequest.getRedirectUri());
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isGithubLogin(final String httpMethod, final String requestUri) {
        return OAUTH2_LOGIN_REDIRECT_METHOD.equals(httpMethod) && requestUri.startsWith(OAUTH2_LOGIN_REDIRECT_URI);
    }

    private GithubLoginRedirectRequest createdRedirectRequest(final String provider) {
        final String clientId = oauth2LoginProperties.getGithub().getSecretKey();
        final String responseType = "code";
        final String scope = "read:user";
        final String redirectUri = "http://localhost:8080/login/oauth2/code/github";

        return GithubLoginRedirectRequest.created(responseType, clientId, scope, redirectUri);
    }
}
