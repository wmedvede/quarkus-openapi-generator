package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.SpecItemAuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;

import io.quarkiverse.openapi.generator.SpecConfig;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {

    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";

    public BasicAuthenticationProvider(final String openApiSpecId, String name, final SpecConfig specConfig) {
        super(openApiSpecId, name, specConfig);
    }

    private String getUsername() {
        return getAuthConfigParam(USER_NAME, "");
    }

    private String getPassword() {
        return getAuthConfigParam(PASSWORD, "");
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (isTokenPropagation()) {
            throw new IOException(
                    "Token propagation is not admitted for the OpenApi securitySchemes of \"type\": \"http\", \"scheme\": \"basic\"."
                            +
                            " A potential source of the problem might be that the configuration property "
                            + getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION) +
                            " was set with the value true in your application, please check your configuration.");
        }
        //TODO remove!
        System.out.println("getAuthProperty() username: " + getUsername());
        System.out.println("getAuthProperty() password: " + getPassword());
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.basicAuthAccessToken(getUsername(), getPassword()));
    }
}
