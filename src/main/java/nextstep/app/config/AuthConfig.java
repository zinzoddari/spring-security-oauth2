package nextstep.app.config;

import nextstep.security.access.AuthorizeRequestMatcherRegistry;
import nextstep.security.access.matcher.AnyRequestMatcher;
import nextstep.security.access.matcher.MvcRequestMatcher;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationProvider;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.authorization.SecurityContextHolderFilter;
import nextstep.security.authorization.manager.RequestAuthorizationManager;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextRepository;
import nextstep.security.exception.ExceptionTranslateFilter;
import nextstep.security.savedRequest.HttpSessionRequestCache;
import nextstep.security.savedRequest.RequestCache;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final UserDetailsService userDetailsService;

    public AuthConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DelegatingFilterProxy securityFilterChainProxy() {
        return new DelegatingFilterProxy("filterChainProxy");
    }

    @Bean
    public FilterChainProxy filterChainProxy() {
        return new FilterChainProxy(List.of(securityFilterChainNew()));
    }

    @Bean
    public SecurityFilterChain securityFilterChainNew() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new SecurityContextHolderFilter(securityContextRepository()));
        filters.add(new UsernamePasswordAuthenticationFilter(authenticationManager(), securityContextRepository()));
        filters.add(new BasicAuthenticationFilter(authenticationManager()));
        filters.add(new ExceptionTranslateFilter(requestCache()));
        filters.add(new AuthorizationFilter(authorizationManager()));
        return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, filters);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

    @Bean
    public RequestAuthorizationManager authorizationManager() {
        AuthorizeRequestMatcherRegistry requestMatcherRegistry = new AuthorizeRequestMatcherRegistry();
        requestMatcherRegistry
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members")).hasAuthority("ADMIN")
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members/me")).authenticated()
                .matcher(new MvcRequestMatcher(HttpMethod.GET, "/members/authentication")).authenticated();
        return new RequestAuthorizationManager(requestMatcherRegistry);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager(new UsernamePasswordAuthenticationProvider(userDetailsService));
    }

}
