package nextstep.security.config;

import nextstep.security.access.matcher.AnyRequestMatcher;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authorization.AuthorizationManager;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

public class HttpSecurity {
    private AuthenticationManager authenticationManager;
    private AuthorizationManager authorizationManager;
    private List<Filter> filters = new ArrayList<>();

    public HttpSecurity authenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        return this;
    }

    public HttpSecurity authorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
        return this;
    }

    public HttpSecurity addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public HttpSecurity authorizeHttpRequests() {

        return this;
    }

    public SecurityFilterChain build() {
        return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, filters);
    }
}
