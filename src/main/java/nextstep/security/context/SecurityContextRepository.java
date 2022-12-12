package nextstep.security.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface SecurityContextRepository {

    Optional<SecurityContext> loadContext(HttpServletRequest request);

    void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response);

}
