package io.quarkiverse.openapi.generator.deployment.mutiny;

import static io.quarkiverse.openapi.generator.deployment.codegen.OpenApiFileConfigUtils.getValues;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.smallrye.config.SmallRyeConfig;

public class MutinyConfigParser {

    private MutinyConfigParser() {
    }

    public static MutinyConfig parse(Config config, Path openApiFilePath) {
        boolean enabled = getValues(config, openApiFilePath, CodegenConfig.ConfigName.MUTINY, Boolean.class).orElse(false);
        boolean returnResponse = getValues(config, openApiFilePath, CodegenConfig.ConfigName.MUTINY_RETURN_RESPONSE,
                Boolean.class).orElse(false);
        Map<String, String> mutinyOperationIds = getValues(config.unwrap(SmallRyeConfig.class), openApiFilePath,
                CodegenConfig.ConfigName.MUTINY_OPERATION_IDS, String.class, String.class).orElse(new HashMap<>());
        boolean globalReturnResponse = getValues(config, openApiFilePath, CodegenConfig.ConfigName.RETURN_RESPONSE,
                Boolean.class).orElse(false);
        return new MutinyConfig(enabled, returnResponse, mutinyOperationIds, globalReturnResponse);
    }
}
