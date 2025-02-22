package nextstep.security.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.OAuthLoginAccessTokenRequest;
import nextstep.security.authentication.domain.OAuthLoginAccessTokenResponse;
import nextstep.security.authentication.domain.OAuthLoginUserResponse;
import nextstep.security.config.client.OAuthClient;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

public class OAuthAuthenticationFilter implements Filter {

    private static final String OAUTH2_LOGIN_TOKEN_REQUEST_METHOD = "GET";
    private static final String OAUTH2_LOGIN_TOKEN_REQUEST_URI = "/login/oauth2/code/";
    private final Oauth2LoginProperties oauth2LoginProperties;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final OAuthClient oAuthClient;

    public OAuthAuthenticationFilter(Oauth2LoginProperties oauth2LoginProperties, UserDetailsService userDetailsService) {
        this.oauth2LoginProperties = oauth2LoginProperties;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
        this.oAuthClient = new OAuthClient();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        final String requestUri = servletRequest.getRequestURI();
        final String httpMethod = servletRequest.getMethod();

        if (!isOauth2Login(httpMethod, requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        final String provider = requestUri.replace(OAUTH2_LOGIN_TOKEN_REQUEST_URI, "");
        final Oauth2LoginProperties.OAuth2Provider oAuth2Provider = oauth2LoginProperties.getProvider(provider);

        final String code = servletRequest.getParameterValues("code")[0];

        final OAuthLoginAccessTokenResponse tokenResponse = getTokenResponse(oAuth2Provider, code);

        final OAuthLoginUserResponse userInfoResponse = getUserResponse(oAuth2Provider.getUserRequestUri(), tokenResponse);

        final Authentication authentication = getAuthentication(userInfoResponse);

        if (authentication.isAuthenticated()) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, servletRequest, servletResponse);
            SecurityContextHolder.clearContext();
            servletResponse.sendRedirect("/");
        }
    }

    private boolean isOauth2Login(final String httpMethod, final String requestUri) {
        return OAUTH2_LOGIN_TOKEN_REQUEST_METHOD.equals(httpMethod) && requestUri.startsWith(OAUTH2_LOGIN_TOKEN_REQUEST_URI);
    }

    private OAuthLoginAccessTokenResponse getTokenResponse(final Oauth2LoginProperties.OAuth2Provider oAuth2Provider, final String code) {
        final OAuthLoginAccessTokenRequest accessTokenRequest
                = OAuthLoginAccessTokenRequest.created(oAuth2Provider.getClientId(), oAuth2Provider.getSecretKey(), code, oAuth2Provider.getGrantType(), oAuth2Provider.getLoginRedirectUri());

        return oAuthClient.post(oAuth2Provider.getTokenRequestUri(), accessTokenRequest, new TypeReference<>() { });
    }

    private OAuthLoginUserResponse getUserResponse(final String userRequestUri, final OAuthLoginAccessTokenResponse tokenResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of(String.join(" ", tokenResponse.getTokenType(), tokenResponse.getAccessToken())));

        return oAuthClient.get(userRequestUri, headers, new TypeReference<>() { });
    }

    private Authentication getAuthentication(final OAuthLoginUserResponse userInfoResponse) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(userInfoResponse.getEmail());
        } catch (AuthenticationException e) {
            userDetails = userDetailsService.singup(userInfoResponse.getEmail());
        }

        if (userDetails == null) {
            throw new RuntimeException();
        }

        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(userDetails.getUsername(), userDetails.getPassword()));
    }
}
