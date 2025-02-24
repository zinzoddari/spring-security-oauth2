package nextstep.security.authentication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.OAuthLoginRedirectRequest;

import java.io.IOException;

public class OAuthLoginRedirectFilter implements Filter {

    private static final String OAUTH2_LOGIN_REDIRECT_METHOD = "GET";
    private static final String OAUTH2_LOGIN_REDIRECT_URI = "/oauth2/authorization/";

    private final Oauth2LoginProperties oauth2LoginProperties;

    public OAuthLoginRedirectFilter(Oauth2LoginProperties oauth2LoginProperties) {
        this.oauth2LoginProperties = oauth2LoginProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        final String httpMethod = httpServletRequest.getMethod();
        final String requestUri = httpServletRequest.getRequestURI();

        if (!isOauth2Login(httpMethod, requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        final String provider = requestUri.replace(OAUTH2_LOGIN_REDIRECT_URI, "");

        Oauth2LoginProperties.OAuth2Provider oAuth2Provider = oauth2LoginProperties.getProvider(provider);

        if (oAuth2Provider == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        final OAuthLoginRedirectRequest redirectRequest = createdRedirectRequest(oAuth2Provider);

        httpServletResponse.sendRedirect(redirectRequest.getRedirectUri(oAuth2Provider.getLoginRequestUri()));
    }

    private boolean isOauth2Login(final String httpMethod, final String requestUri) {
        return OAUTH2_LOGIN_REDIRECT_METHOD.equals(httpMethod) && requestUri.startsWith(OAUTH2_LOGIN_REDIRECT_URI);
    }

    private OAuthLoginRedirectRequest createdRedirectRequest(final Oauth2LoginProperties.OAuth2Provider provider) {
        final String clientId = provider.getClientId();
        final String responseType = "code";
        final String scope = provider.getScope();
        final String redirectUri = provider.getLoginRedirectUri();

        return OAuthLoginRedirectRequest.created(responseType, clientId, scope, redirectUri);
    }
}
