package nextstep.security.authentication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.GithubLoginAccessTokenRequest;
import nextstep.security.authentication.domain.GithubLoginAccessTokenResponse;
import nextstep.security.authentication.domain.OAuthLoginUserResponse;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class OAuthAuthenticationFilter implements Filter {

    private final Oauth2LoginProperties oauth2LoginProperties;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public OAuthAuthenticationFilter(Oauth2LoginProperties oauth2LoginProperties, UserDetailsService userDetailsService) {
        this.oauth2LoginProperties = oauth2LoginProperties;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        final String requestUri = servletRequest.getRequestURI();
        final String httpMethod = servletRequest.getMethod();

        if (requestUri.startsWith("/login/oauth2/code/github") && httpMethod.equals("GET")) {
            final String code = servletRequest.getParameterValues("code")[0];

            final GithubLoginAccessTokenRequest accessTokenRequest
                    = GithubLoginAccessTokenRequest.created(oauth2LoginProperties.getGithub().getClientId(), oauth2LoginProperties.getGithub().getSecretKey(), code);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<GithubLoginAccessTokenResponse> responseEntity = restTemplate.postForEntity("http://localhost:8089/login/oauth/access_token", accessTokenRequest, GithubLoginAccessTokenResponse.class);
            GithubLoginAccessTokenResponse response1 = responseEntity.getBody();

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                RequestEntity<Void> getRequest = RequestEntity.get("http://localhost:8089/user").header("Authorization", String.join(" ", response1.getTokenType(), response1.getAccessToken()))
                                .build();

                ResponseEntity<OAuthLoginUserResponse> userResponse = restTemplate.exchange(getRequest, OAuthLoginUserResponse.class);
                OAuthLoginUserResponse body = userResponse.getBody();

                if (userResponse.getStatusCode().is2xxSuccessful()) {
                    assert body != null;
                    UserDetails userDetails;
                    try {
                        userDetails = userDetailsService.loadUserByUsername(body.getEmail());
                    } catch (AuthenticationException e) {
                        userDetails = userDetailsService.singup(body.getEmail());
                    }

                    if (userDetails != null) {
                        Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(userDetails.getUsername(), userDetails.getPassword()));

                        if (authentication.isAuthenticated()) {
                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
                            SecurityContextHolder.setContext(context);
                            securityContextRepository.saveContext(context, servletRequest, servletResponse);
                            SecurityContextHolder.clearContext();
                            servletResponse.sendRedirect("/");
                            return;
                        }
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }
}
