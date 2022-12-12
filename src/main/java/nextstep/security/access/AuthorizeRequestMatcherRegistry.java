package nextstep.security.access;

import nextstep.security.access.matcher.RequestMatcher;
import nextstep.security.authorization.AuthorizationManager;
import nextstep.security.authorization.manager.AuthenticatedAuthorizationManager;
import nextstep.security.authorization.manager.AuthorityAuthorizationManager;
import nextstep.security.authorization.manager.DenyAllAuthorizationManager;
import nextstep.security.authorization.manager.PermitAllAuthorizationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class AuthorizeRequestMatcherRegistry {
    private final Map<RequestMatcher, AuthorizationManager> mappings = new HashMap<>();

    public AuthorizedUrl matcher(RequestMatcher requestMatcher) {
        return new AuthorizedUrl(requestMatcher);
    }

    AuthorizeRequestMatcherRegistry addMapping(RequestMatcher requestMatcher, AuthorizationManager authorizationManager) {
        mappings.put(requestMatcher, authorizationManager);
        return this;
    }

    public AuthorizationManager getAttribute(HttpServletRequest request) {
        for (Map.Entry<RequestMatcher, AuthorizationManager> entry : mappings.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public class AuthorizedUrl {
        private final RequestMatcher requestMatcher;

        public AuthorizedUrl(RequestMatcher requestMatcher) {
            this.requestMatcher = requestMatcher;
        }

        public AuthorizeRequestMatcherRegistry permitAll() {
            return access(PermitAllAuthorizationManager.permitAll());
        }

        public AuthorizeRequestMatcherRegistry denyAll() {
            return access(DenyAllAuthorizationManager.denyAll());
        }

        public AuthorizeRequestMatcherRegistry hasAuthority(String authority) {
            return access(AuthorityAuthorizationManager.hasAnyAuthority(authority));
        }

        public AuthorizeRequestMatcherRegistry authenticated() {
            return access(AuthenticatedAuthorizationManager.authenticated());
        }

        private AuthorizeRequestMatcherRegistry access(AuthorizationManager authorizationManager) {
            return addMapping(requestMatcher, authorizationManager);
        }
    }

}
