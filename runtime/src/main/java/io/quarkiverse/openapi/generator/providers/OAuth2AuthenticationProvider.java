package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.SpecItemAuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.SpecConfig;
import io.quarkus.oidc.client.filter.OidcClientRequestFilter;
import io.quarkus.oidc.common.runtime.OidcConstants;

public class OAuth2AuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);

    private final OidcClientRequestFilter delegate;

    public OAuth2AuthenticationProvider(final String openApiSpecId, final String name, final SpecConfig specConfig) {
        super(openApiSpecId, name, specConfig);
        if (isTokenPropagation()) {
            LOGGER.warn("Token propagation was enabled for a the oauth2: {} securityScheme in the specification file: {}. " +
                    "This configuration can be done by using the property: {} and is not necessary a problem if the configuration is intentional.",
                    name, openApiSpecId, getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION));
        }
        this.delegate = new OidcClientRequestFilterDelegate(name);
        // it's fine calling it here since this class will be created on `init()` method of the generated CompositeAuthenticationProvider
        delegate.init();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (isTokenPropagation()) {
            String bearerToken = getTokenForPropagation(requestContext.getHeaders());
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, OidcConstants.BEARER_SCHEME + " " + bearerToken);
        } else {
            delegate.filter(requestContext);
        }
    }

    private static class OidcClientRequestFilterDelegate extends OidcClientRequestFilter {

        private final String clientId;

        public OidcClientRequestFilterDelegate(String clientId) {
            this.clientId = clientId;
        }

        @Override
        protected Optional<String> clientId() {
            return Optional.of(this.clientId);
        }
    }
}
