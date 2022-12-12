package nextstep.security.authorization;

import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.manager.RequestAuthorizationManager;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.exception.AuthenticationException;
import nextstep.security.exception.AuthorizationException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthorizationFilter extends GenericFilterBean {

    private final RequestAuthorizationManager authorizationManager;

    public AuthorizationFilter(RequestAuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (!authorizationManager.check((HttpServletRequest) request, authentication)) {
                throw new AuthorizationException();
            }
        } catch (AuthenticationException exception) {
            throw exception;
        }

        chain.doFilter(request, response);
    }
}
