package nextstep.security.authentication;

import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UsernamePasswordAuthenticationFilter extends GenericFilterBean {
    private static final MvcRequestMatcher DEFAULT_MVC_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.POST, "/login");
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public UsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService) {
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication authenticationResult = attemptAuthentication(request);
            if (authenticationResult == null) {
                return;
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);
            this.securityContextRepository.saveContext(context, request, response);

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return DEFAULT_MVC_REQUEST_MATCHER.matches(request);
    }

    private Authentication attemptAuthentication(HttpServletRequest request) {
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.authenticationManager.authenticate(unauthenticated);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
    }
}
