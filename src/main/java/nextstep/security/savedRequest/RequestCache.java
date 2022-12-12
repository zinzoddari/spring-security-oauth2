package nextstep.security.savedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface RequestCache {

    void saveRequest(HttpServletRequest request, HttpServletResponse response);

    Optional<SavedRequest> getRequest(HttpServletRequest request, HttpServletResponse response);

    void removeRequest(HttpServletRequest request, HttpServletResponse response);

}