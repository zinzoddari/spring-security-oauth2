package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.OAuth2AuthorizationRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public OAuth2AuthorizationRequestRedirectFilter(Oauth2LoginProperties oauth2LoginProperties) {
        this.authorizationRequestResolver = new OAuth2AuthorizationRequestResolver(oauth2LoginProperties);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
        if (authorizationRequest != null) {
            sendRedirectForAuthorization(request, response, authorizationRequest);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        response.sendRedirect(authorizationRequest.getRedirectUri());
    }
}
