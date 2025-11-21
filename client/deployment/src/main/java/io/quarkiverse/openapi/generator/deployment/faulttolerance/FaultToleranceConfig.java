package io.quarkiverse.openapi.generator.deployment.faulttolerance;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FaultToleranceConfig {
    public static final String NAME = "fault-tolerance";

    //TODO WM, ver si meto la generacion a nivel de clase...
    private Map<String, FaultToleranceDescriptor> classFaultTolerance = new HashMap<>();
    private Map<String, FaultToleranceDescriptor> methodFaultTolerance = new HashMap<>();

    public FaultToleranceConfig() {
    }

    public FaultToleranceConfig(Map<String, FaultToleranceDescriptor> classFaultTolerance,
            Map<String, FaultToleranceDescriptor> methodFaultTolerance) {
        this.classFaultTolerance = classFaultTolerance;
        this.methodFaultTolerance = methodFaultTolerance;
    }

    public boolean hasFaultTolerance(String packageName, String className, String method) {
        return hasCircuitBreaker(packageName, className, method) || hasTimeout(packageName, className, method)
                || hasRetry(packageName, className, method);
    }

    public boolean hasCircuitBreaker(String packageName, String className, String methodName) {
        return getMethodFaultTolerance(packageName, className, methodName,
                FaultToleranceDescriptor::isCircuitBreaker);
    }

    public boolean hasTimeout(String packageName, String className, String methodName) {
        return getMethodFaultTolerance(packageName, className, methodName, FaultToleranceDescriptor::isTimeout);
    }

    public boolean hasRetry(String packageName, String className, String methodName) {
        return getMethodFaultTolerance(packageName, className, methodName, FaultToleranceDescriptor::isRetry);
    }

    FaultToleranceDescriptor getMethodFaultTolerance(String packageName, String className, String methodName) {
        return methodFaultTolerance.get(fullyQualifiedName(packageName, className, methodName));
    }

    private static String fullyQualifiedName(String packageName, String className, String method) {
        return packageName + "." + className + "." + method;
    }

    private boolean getMethodFaultTolerance(String packageName, String className, String methodName,
            Function<FaultToleranceDescriptor, Boolean> accessor) {
        FaultToleranceDescriptor descriptor = methodFaultTolerance.get(fullyQualifiedName(packageName, className, methodName));
        return descriptor != null && accessor.apply(descriptor);
    }
}
