package nextstep.security.authentication;

import nextstep.app.security.Oauth2LoginProperties;
import nextstep.security.authentication.domain.ClientRegistration;

import java.util.Map;
import java.util.stream.Collectors;

public class ClientRegistrationRepository {

    private Map<String, ClientRegistration> providers;

    public ClientRegistrationRepository(Oauth2LoginProperties oauth2LoginProperties) {
        this.providers = generateProviders(oauth2LoginProperties);
    }

    public ClientRegistration getClientRegistration(final String provider) {
        return providers.get(provider);
    }

    private Map<String, ClientRegistration> generateProviders(Oauth2LoginProperties oauth2LoginProperties) {
        return oauth2LoginProperties.getProviders().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ClientRegistration.created(entry.getValue())
                ));
    }
}
