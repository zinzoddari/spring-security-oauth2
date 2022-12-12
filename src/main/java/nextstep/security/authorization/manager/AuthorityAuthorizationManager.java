package nextstep.security.authorization.manager;

import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AuthorityAuthorizationManager implements AuthorizationManager {
    private final List<String> authorities;

    private AuthorityAuthorizationManager(String... authorities) {
        this.authorities = List.of(authorities);
    }

    public static AuthorityAuthorizationManager hasAnyAuthority(String... authorities) {
        return new AuthorityAuthorizationManager(authorities);
    }

    @Override
    public boolean check(HttpServletRequest request, Authentication authentication) {
        return authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(authorities::contains);
    }
}
