package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;

import io.quarkiverse.openapi.generator.SpecConfig;

/**
 * Provides bearer token authentication or any other valid scheme.
 * 
 * @see <a href="https://swagger.io/docs/specification/authentication/bearer-authentication/">Bearer Authentication</a>
 */
public class BearerAuthenticationProvider extends AbstractAuthProvider {

    private static final String BEARER_TOKEN_CONFIG_PROPERTY = "bearer-token";

    private final String scheme;

    public BearerAuthenticationProvider(final String openApiSpecId, final String name, final String scheme,
            final SpecConfig specConfig) {
        super(openApiSpecId, name, specConfig);
        this.scheme = scheme;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String bearerToken;
        if (isTokenPropagation()) {
            bearerToken = getTokenForPropagation(requestContext.getHeaders());
        } else {
            bearerToken = getBearerToken();
        }
        //TODO remove this prints.
        System.out.println("XXXXXX token-propagation: " + isTokenPropagation());
        System.out.println("XXXXXX header-name: " + getHeaderName());
        System.out.println("XXXXXX bearer-token: " + getBearerToken());

        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.authTokenOrBearer(this.scheme, bearerToken));
    }

    private String getBearerToken() {
        return getAuthConfigParam(BEARER_TOKEN_CONFIG_PROPERTY, "");
    }
}
