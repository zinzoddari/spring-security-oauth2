package nextstep.security.savedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class HttpSessionRequestCache implements RequestCache {
    private static final String SAVED_REQUEST_KEY = "SPRING_SECURITY_SAVED_REQUEST";

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();
        SavedRequest savedRequest = new SavedRequest(request);
        httpSession.setAttribute(SAVED_REQUEST_KEY, savedRequest);
    }

    @Override
    public Optional<SavedRequest> getRequest(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        return Optional.of((SavedRequest) session.getAttribute(SAVED_REQUEST_KEY));
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute(SAVED_REQUEST_KEY);
    }
}
