package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.SpecConfig.RUNTIME_TIME_CONFIG_PREFIX;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import io.quarkiverse.openapi.generator.SpecConfig;
import io.quarkiverse.openapi.generator.SpecItemAuthConfig;
import io.quarkiverse.openapi.generator.SpecItemAuthsConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;

public abstract class AbstractAuthProvider implements AuthProvider {

    private static final String CANONICAL_AUTH_CONFIG_PROPERTY_NAME = "quarkus." + RUNTIME_TIME_CONFIG_PREFIX
            + ".%s.auth.%s.%s";

    private final String openApiSpecId;
    private final String name;
    private final SpecConfig specConfig;
    private SpecItemAuthConfig specItemAuthConfig;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

    protected AbstractAuthProvider(String openApiSpecId, String name, SpecConfig specConfig) {
        this.openApiSpecId = openApiSpecId;
        this.name = name;
        this.specConfig = specConfig;
        Optional<SpecItemConfig> specItemConfig = specConfig.getItemConfig(openApiSpecId);
        if (specItemConfig.isPresent()) {
            Optional<SpecItemAuthsConfig> authsConfig = specItemConfig.get().getAuth();
            authsConfig.ifPresent(
                    specItemAuthsConfig -> specItemAuthConfig = specItemAuthsConfig.getItemConfig(name).orElse(null));
        }
    }

    public String getOpenApiSpecId() {
        return openApiSpecId;
    }

    @Override
    public String getName() {
        return name;
    }

    public SpecConfig getSpecConfig() {
        return specConfig;
    }

    public boolean isTokenPropagation() {
        if (specItemAuthConfig != null) {
            return specItemAuthConfig.getTokenPropagation().orElse(false);
        }
        return false;
    }

    public String getTokenForPropagation(MultivaluedMap<String, Object> httpHeaders) {
        if (getHeaderName() != null) {
            return Objects.toString(httpHeaders.getFirst(getHeaderName()));
        } else {
            return Objects.toString(httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
        }
    }

    public String getHeaderName() {
        if (specItemAuthConfig != null) {
            return specItemAuthConfig.getHeaderName().orElse(null);
        }
        return null;
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    @Override
    public AuthProvider addOperation(OperationAuthInfo operationAuthInfo) {
        this.applyToOperations.add(operationAuthInfo);
        return this;
    }

    public String getAuthConfigParam(String paramName, String defaultValue) {
        if (specItemAuthConfig != null) {
            return specItemAuthConfig.getConfigParam(paramName).orElse(defaultValue);
        }
        return defaultValue;
    }

    protected String getCanonicalAuthConfigPropertyName(String authPropertyName) {
        return String.format(CANONICAL_AUTH_CONFIG_PROPERTY_NAME, getOpenApiSpecId(), getName(), authPropertyName);
    }
}
