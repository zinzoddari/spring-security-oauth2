package nextstep.security.authorization;

import nextstep.security.authentication.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthorizationManager {
    boolean check(HttpServletRequest request, Authentication authentication);
}
