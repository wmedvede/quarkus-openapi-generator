package io.quarkiverse.openapi.generator.deployment.faulttolerance;

public class FaultToleranceDescriptor {

    private boolean circuitBreaker;
    private boolean timeout;
    private boolean retry;

    public FaultToleranceDescriptor() {
    }

    public FaultToleranceDescriptor(boolean circuitBreaker, boolean timeout, boolean retry) {
        this.circuitBreaker = circuitBreaker;
        this.timeout = timeout;
        this.retry = retry;
    }

    public boolean isCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(boolean circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }
}
