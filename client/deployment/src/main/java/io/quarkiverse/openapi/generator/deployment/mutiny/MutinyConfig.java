package io.quarkiverse.openapi.generator.deployment.mutiny;

import java.util.HashMap;
import java.util.Map;

public class MutinyConfig {
    private static final String MULTI = "Multi";
    private static final String UNI = "Uni";
    public static final String NAME = "mutiny";
    private boolean enabled;

    private boolean returnResponse;

    private boolean globalReturnResponse;

    private Map<String, String> mutinyOperationIds = new HashMap<>();

    public MutinyConfig() {
    }

    public MutinyConfig(boolean enabled, boolean returnResponse, Map<String, String> mutinyOperationIds,
            boolean globalReturnResponse) {
        this.enabled = enabled;
        this.returnResponse = returnResponse;
        this.mutinyOperationIds = mutinyOperationIds;
        this.globalReturnResponse = globalReturnResponse;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean returnResponse() {
        return returnResponse;
    }

    public boolean globalReturnResponse() {
        return globalReturnResponse;
    }

    public boolean returnMulti(String operationId) {
        return MULTI.equals(mutinyOperationIds.get(operationId));
    }

    public boolean returnUni(String operationId) {
        return UNI.equals(mutinyOperationIds.get(operationId));
    }

    public boolean emptyOperationIds() {
        return mutinyOperationIds.isEmpty();
    }

    public boolean hasOperationId(String operationId) {
        return mutinyOperationIds.containsKey(operationId) && !mutinyOperationIds.get(operationId).isEmpty();
    }
}
