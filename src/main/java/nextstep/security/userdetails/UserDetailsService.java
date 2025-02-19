package nextstep.security.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);

    UserDetails singup(String username);
}
