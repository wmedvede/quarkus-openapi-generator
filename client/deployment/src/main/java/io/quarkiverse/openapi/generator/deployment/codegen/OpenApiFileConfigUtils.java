package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSanitizedFileName;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.smallrye.config.SmallRyeConfig;

/**
 * Utility methods for reading configurations related to an OpenApi file.
 */
public class OpenApiFileConfigUtils {

    private static final String CONFIG_KEY_PROPERTY = "config-key";

    private OpenApiFileConfigUtils() {
    }

    public static <T> Optional<T> getValues(final Config config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<T> propertyType) {

        return getConfigKeyValues(config, openApiFilePath, configName, propertyType)
                .or(() -> getValuesBySpecConfigName(config, openApiFilePath, configName, propertyType));
    }

    public static <T> Optional<T> getConfigKeyValues(final Config config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<T> propertyType) {

        Optional<String> possibleConfigKey = getConfigKeyValue(config, openApiFilePath);
        return possibleConfigKey
                .flatMap(s -> getValuesByConfigKey(config, CodegenConfig.getSpecConfigNameByConfigKey(s, configName),
                        propertyType, configName));

    }

    public static Optional<String> getConfigKeyValue(Config config, Path openApiFilePath) {
        String configKey = String.format("quarkus.openapi-generator.codegen.spec.%s.%s", getSanitizedFileName(openApiFilePath),
                CONFIG_KEY_PROPERTY);
        return config.getOptionalValue(configKey, String.class)
                .filter(Predicate.not(String::isBlank));
    }

    public static <K, V> Optional<Map<K, V>> getValues(final SmallRyeConfig config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass) {

        return getConfigKeyValues(config, openApiFilePath, configName, kClass, vClass)
                .or(() -> getValuesBySpecConfigName(config, openApiFilePath, configName, kClass, vClass));
    }

    private static <K, V> Optional<Map<K, V>> getConfigKeyValues(final SmallRyeConfig config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass) {

        Optional<String> possibleConfigKey = getConfigKeyValue(config, openApiFilePath);
        return possibleConfigKey.flatMap(s -> getValuesByConfigKey(config, configName, kClass, vClass, s));

    }

    private static <T> Optional<T> getValuesBySpecConfigName(Config config, Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<T> propertyType) {
        return config
                .getOptionalValue(CodegenConfig.getSpecConfigName(configName, openApiFilePath), propertyType)
                .or(() -> config.getOptionalValue(CodegenConfig.getGlobalConfigName(configName), propertyType));
    }

    private static <K, V> Optional<Map<K, V>> getValuesBySpecConfigName(SmallRyeConfig config, Path openApiFilePath,
            CodegenConfig.ConfigName configName, Class<K> kClass, Class<V> vClass) {
        return config
                .getOptionalValues(CodegenConfig.getSpecConfigName(configName, openApiFilePath), kClass, vClass)
                .or(() -> config.getOptionalValues(CodegenConfig.getGlobalConfigName(configName), kClass, vClass));
    }

    private static <T> Optional<T> getValuesByConfigKey(Config config, String configName, Class<T> propertyType,
            CodegenConfig.ConfigName codegenConfigName) {
        return config
                .getOptionalValue(configName, propertyType)
                .or(() -> config.getOptionalValue(CodegenConfig.getGlobalConfigName(codegenConfigName), propertyType));
    }

    private static <K, V> Optional<Map<K, V>> getValuesByConfigKey(SmallRyeConfig config, CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass, String configKey) {
        return config
                .getOptionalValues(CodegenConfig.getSpecConfigNameByConfigKey(configKey, configName), kClass,
                        vClass)
                .or(() -> config.getOptionalValues(CodegenConfig.getGlobalConfigName(configName), kClass, vClass));
    }
}
