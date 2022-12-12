package nextstep.security.authorization.manager;

import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;

public class DenyAllAuthorizationManager implements AuthorizationManager {
    private DenyAllAuthorizationManager() {
    }

    public static DenyAllAuthorizationManager denyAll() {
        return new DenyAllAuthorizationManager();
    }
    
    @Override
    public boolean check(HttpServletRequest request, Authentication authentication) {
        return false;
    }
}
