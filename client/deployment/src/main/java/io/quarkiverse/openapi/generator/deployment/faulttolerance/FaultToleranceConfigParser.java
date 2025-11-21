package io.quarkiverse.openapi.generator.deployment.faulttolerance;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.Config;

public final class FaultToleranceConfigParser {

    private static final String CIRCUIT_BREAKER = "CircuitBreaker";
    private static final String TIMEOUT = "Timeout";
    public static final String RETRY = "Retry";
    private static final String METHOD_FAULT_TOLERANCE_PROPERTY = ".+/.+/(" + CIRCUIT_BREAKER + "|" + TIMEOUT + "|" + RETRY
            + ")/enabled";

    private FaultToleranceConfigParser() {
    }

    /**
     * Parses the {@link Config} and returns a {@link FaultToleranceConfig} with the fault tolerance configurations to
     * apply during code generation.
     *
     * @return a {@link FaultToleranceConfig} with fault tolerance configurations.
     */
    public static FaultToleranceConfig parse(Config config) {
        Map<String, FaultToleranceDescriptor> methodFaultTolerance = new HashMap<>();
        for (String propertyName : config.getPropertyNames()) {
            if (propertyName.matches(METHOD_FAULT_TOLERANCE_PROPERTY)) {
                //TODO WM ver si estos spilits se pueden hacer romper
                String[] propertySplit = propertyName.split("/");
                String methodQualifiedName = propertySplit[0] + "." + propertySplit[1];
                String faultTolerance = propertySplit[2] != null ? propertySplit[2] : "";
                FaultToleranceDescriptor descriptor = methodFaultTolerance.computeIfAbsent(methodQualifiedName,
                        (key) -> new FaultToleranceDescriptor());
                boolean value = config.getOptionalValue(propertyName, Boolean.class).orElse(false);
                switch (faultTolerance) {
                    case CIRCUIT_BREAKER -> descriptor.setCircuitBreaker(value);
                    case TIMEOUT -> descriptor.setTimeout(value);
                    case RETRY -> descriptor.setRetry(value);
                }
            }
        }
        return new FaultToleranceConfig(new HashMap<>(), methodFaultTolerance);
    }
}
