package nextstep.security.savedRequest;

import javax.servlet.http.HttpServletRequest;

public class SavedRequest {
    private final String method;
    private final String redirectUrl;

    public SavedRequest(HttpServletRequest request) {
        this.method = request.getMethod();
        this.redirectUrl = request.getRequestURL().toString();
    }

    public String getMethod() {
        return method;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
