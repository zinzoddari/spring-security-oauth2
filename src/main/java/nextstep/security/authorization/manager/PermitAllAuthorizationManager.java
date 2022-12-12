package nextstep.security.authorization.manager;

import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;

public class PermitAllAuthorizationManager implements AuthorizationManager {
    private PermitAllAuthorizationManager() {
    }

    public static PermitAllAuthorizationManager permitAll() {
        return new PermitAllAuthorizationManager();
    }

    @Override
    public boolean check(HttpServletRequest request, Authentication authentication) {
        return true;
    }
}
