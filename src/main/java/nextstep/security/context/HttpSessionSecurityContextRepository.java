package nextstep.security.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class HttpSessionSecurityContextRepository implements SecurityContextRepository {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    @Override
    public Optional<SecurityContext> loadContext(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Object contextFromSession = httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        return Optional.ofNullable((SecurityContext) contextFromSession);
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
