package nextstep.security.authentication;

import java.util.Collections;
import java.util.Set;

public class UsernamePasswordAuthentication implements Authentication {
    private final String username;
    private final String password;
    private final Set<String> authorities;
    private boolean authenticated = false;

    private UsernamePasswordAuthentication(String username, String password, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UsernamePasswordAuthentication ofRequest(String username, String password) {
        return new UsernamePasswordAuthentication(username, password, Collections.emptySet());
    }

    public static Authentication ofAuthenticated(String username, String password, Set<String> authorities) {
        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(username, password, authorities);
        authentication.authenticated = true;
        return authentication;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

}
