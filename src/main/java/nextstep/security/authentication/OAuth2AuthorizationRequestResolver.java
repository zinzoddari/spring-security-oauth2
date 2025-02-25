package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.ClientRegistration;
import nextstep.security.authentication.domain.OAuth2AuthorizationRequest;

public class OAuth2AuthorizationRequestResolver {

    private static final String OAUTH2_LOGIN_REDIRECT_METHOD = "GET";
    private static final String OAUTH2_LOGIN_REDIRECT_URI = "/oauth2/authorization/";

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestResolver(Oauth2LoginProperties oauth2LoginProperties) {
        this.clientRegistrationRepository = new ClientRegistrationRepository(oauth2LoginProperties);
    }

    public OAuth2AuthorizationRequest resolve(final HttpServletRequest request) {
        final String httpMethod = request.getMethod();
        final String requestUri = request.getRequestURI();

        if (!isOauth2Login(httpMethod, requestUri)) {
            return null;
        }

        final ClientRegistration clientRegistration = getClientRegistration(requestUri);

        if (clientRegistration == null) {
            return null;
        }

        return OAuth2AuthorizationRequest.created(clientRegistration);
    }

    private boolean isOauth2Login(final String httpMethod, final String requestUri) {
        return OAUTH2_LOGIN_REDIRECT_METHOD.equals(httpMethod) && requestUri.startsWith(OAUTH2_LOGIN_REDIRECT_URI);
    }

    private ClientRegistration getClientRegistration(final String requestUri) {
        final String provider = requestUri.replace(OAUTH2_LOGIN_REDIRECT_URI, "");

        return clientRegistrationRepository.getClientRegistration(provider);
    }
}
