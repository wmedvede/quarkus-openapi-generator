package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.SpecItemAuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.UriBuilder;

import io.quarkiverse.openapi.generator.SpecConfig;

/**
 * Provider for API Key authentication.
 */
public class ApiKeyAuthenticationProvider extends AbstractAuthProvider {

    private static final String API_KEY = "api-key";

    private final ApiKeyIn apiKeyIn;
    private final String apiKeyName;

    public ApiKeyAuthenticationProvider(final String openApiSpecId, final String name, final ApiKeyIn apiKeyIn,
            final String apiKeyName,
            final SpecConfig specConfig) {
        super(openApiSpecId, name, specConfig);
        this.apiKeyIn = apiKeyIn;
        this.apiKeyName = apiKeyName;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (isTokenPropagation()) {
            throw new IOException("Token propagation is not admitted for the OpenApi securitySchemes of \"type\": \"apiKey\"." +
                    " A potential source of the problem might be that the configuration property "
                    + getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION) +
                    " was set with the value true in your application, please check your configuration.");
        }
        switch (apiKeyIn) {
            case query:
                requestContext.setUri(UriBuilder.fromUri(requestContext.getUri()).queryParam(apiKeyName, getApiKey()).build());
                break;
            case cookie:
                requestContext.getCookies().put(apiKeyName, new Cookie(apiKeyName, getApiKey()));
                break;
            case header:
                requestContext.getHeaders().add(apiKeyName, getApiKey());
                break;
        }
    }

    private String getApiKey() {
        //TODO remove
        System.out.println("XXXXXXXX api-key: " + getAuthConfigParam(API_KEY, ""));
        return getAuthConfigParam(API_KEY, "");
    }
}
