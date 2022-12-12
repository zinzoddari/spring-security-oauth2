package nextstep.security.authorization.manager;

import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;

public class AuthenticatedAuthorizationManager implements AuthorizationManager {
    private AuthenticatedAuthorizationManager() {
    }

    public static AuthenticatedAuthorizationManager authenticated() {
        return new AuthenticatedAuthorizationManager();
    }

    @Override
    public boolean check(HttpServletRequest request, Authentication authentication) {
        return authentication.isAuthenticated();
    }
}
