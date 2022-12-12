package nextstep.security.authorization.manager;

import nextstep.security.access.AuthorizeRequestMatcherRegistry;
import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.AuthorizationManager;
import nextstep.security.exception.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

public class RequestAuthorizationManager implements AuthorizationManager {

    private final AuthorizeRequestMatcherRegistry requestMatcherRegistry;

    public RequestAuthorizationManager(AuthorizeRequestMatcherRegistry requestMatcherRegistry) {
        this.requestMatcherRegistry = requestMatcherRegistry;
    }

    @Override
    public boolean check(HttpServletRequest request, Authentication authentication) {
        AuthorizationManager authorizationManager = requestMatcherRegistry.getAttribute(request);

        if (authorizationManager == null) {
            return true;
        }

        if (authentication == null) {
            throw new AuthenticationException();
        }

        return authorizationManager.check(request, authentication);
    }

}
