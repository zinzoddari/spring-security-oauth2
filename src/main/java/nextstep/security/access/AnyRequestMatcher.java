package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;

public class AnyRequestMatcher implements RequestMatcher {
    public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

    private AnyRequestMatcher() {
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }
}
