package nextstep.app.security;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.security.authentication.OAuthAuthenticationFilter;
import nextstep.security.authentication.OAuthLoginRedirectFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authorization.*;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EnableAspectJAutoProxy
@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final Oauth2LoginProperties oauth2LoginProperties;

    public SecurityConfig(MemberRepository memberRepository, Oauth2LoginProperties oauth2LoginProperties) {
        this.memberRepository = memberRepository;
        this.oauth2LoginProperties = oauth2LoginProperties;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain())));
    }

    @Bean
    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(
                List.of(
                        new SecurityContextHolderFilter(),
                        new UsernamePasswordAuthenticationFilter(userDetailsService()),
                        new BasicAuthenticationFilter(userDetailsService()),
                        new OAuth2AuthorizationRequestRedirectFilter(oauth2LoginProperties),
                        new OAuthAuthenticationFilter(oauth2LoginProperties, userDetailsService()),
                        new AuthorizationFilter(requestAuthorizationManager())
                )
        );
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public RequestAuthorizationManager requestAuthorizationManager() {
        List<RequestMatcherEntry<AuthorizationManager>> mappings = new ArrayList<>();
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members"), new AuthorityAuthorizationManager(roleHierarchy(), "ADMIN")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/members/me"), new AuthorityAuthorizationManager(roleHierarchy(), "USER")));
        mappings.add(new RequestMatcherEntry<>(new MvcRequestMatcher(HttpMethod.GET, "/search"), new PermitAllAuthorizationManager()));
        mappings.add(new RequestMatcherEntry<>(AnyRequestMatcher.INSTANCE, new PermitAllAuthorizationManager()));
        return new RequestAuthorizationManager(mappings);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                Member member = memberRepository.findByEmail(username)
                    .orElseThrow(() -> new AuthenticationException("존재하지 않는 사용자입니다."));
                return new UserDetails() {
                    @Override
                    public String getUsername() {
                        return member.getEmail();
                    }

                    @Override
                    public String getPassword() {
                        return member.getPassword();
                    }

                    @Override
                    public Set<String> getAuthorities() {
                        return member.getRoles();
                    }
                };
            }

            @Override
            public UserDetails singup(String username) {
                Member member = memberRepository.save(new Member(username, null, null, null, Set.of("USER")));
                return new UserDetails() {
                    @Override
                    public String getUsername() {
                        return member.getEmail();
                    }

                    @Override
                    public String getPassword() {
                        return member.getPassword();
                    }

                    @Override
                    public Set<String> getAuthorities() {
                        return member.getRoles();
                    }
                };
            }
        };
    }
}
