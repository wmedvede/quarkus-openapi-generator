package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.SpecConfig.resolveApiPackage;
import static io.quarkiverse.openapi.generator.deployment.SpecConfig.resolveModelPackage;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.GlobalSettings;

/**
 * Wrapper for the OpenAPIGen tool.
 * This is the same as calling the Maven plugin or the CLI.
 * We are wrapping into a class to generate code that meet our requirements.
 *
 * @see <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public class OpenApiClientGeneratorWrapper {

    private static final String VERBOSE = "verbose";
    private static final String ONCE_LOGGER = "org.openapitools.codegen.utils.oncelogger.enabled";
    private static final String DEFAULT_PACKAGE = "org.openapi.quarkus";

    private final QuarkusCodegenConfigurator configurator;
    private final DefaultGenerator generator;

    private String basePackage = DEFAULT_PACKAGE;
    private String apiPackage = "";
    private String modelPackage = "";

    public OpenApiClientGeneratorWrapper(final Path specFilePath, final Path outputDir) {
        // do not generate docs nor tests
        GlobalSettings.setProperty(CodegenConstants.API_DOCS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, FALSE.toString());
        // generates every Api and Models
        GlobalSettings.setProperty(CodegenConstants.APIS, "");
        GlobalSettings.setProperty(CodegenConstants.MODELS, "");
        GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "");
        // logging
        GlobalSettings.setProperty(VERBOSE, FALSE.toString());
        GlobalSettings.setProperty(ONCE_LOGGER, TRUE.toString());

        this.configurator = new QuarkusCodegenConfigurator();
        this.configurator.setInputSpec(specFilePath.toString());
        this.configurator.setOutputDir(outputDir.toString());
        this.generator = new DefaultGenerator();
    }

    public OpenApiClientGeneratorWrapper withApiPackage(final String pkg) {
        this.apiPackage = pkg;
        return this;
    }

    public OpenApiClientGeneratorWrapper withModelPackage(final String pkg) {
        this.modelPackage = pkg;
        return this;
    }

    public OpenApiClientGeneratorWrapper withBasePackage(final String pkg) {
        this.basePackage = pkg;
        return this;
    }

    /**
     * Adds the circuit breaker configuration to the generator.
     *
     * @param config a map of class names and their methods that should be configured with circuit breaker
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withCircuitBreakerConfiguration(final Map<String, List<String>> config) {
        if (config != null) {
            configurator.addAdditionalProperty("circuit-breaker", config);
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withModelCodeGenConfiguration(final Map<String, Object> config) {
        if (config != null) {
            configurator.addAdditionalProperty("model-codegen", config);
        }
        return this;
    }

    /**
     * Sets the global 'skipFormModel' setting. If not set this setting will default to true.
     *
     * @param skipFormModel whether to skip the generation of models for form parameters
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withSkipFormModelConfig(final String skipFormModel) {
        GlobalSettings.setProperty(CodegenConstants.SKIP_FORM_MODEL, skipFormModel);
        return this;
    }

    public List<File> generate() {
        this.consolidatePackageNames();
        return generator.opts(configurator.toClientOptInput()).generate();
    }

    private void consolidatePackageNames() {
        if (basePackage.isEmpty()) {
            basePackage = DEFAULT_PACKAGE;
        }
        if (apiPackage.isEmpty()) {
            this.apiPackage = resolveApiPackage(basePackage);
        }
        if (modelPackage.isEmpty()) {
            this.modelPackage = resolveModelPackage(basePackage);
        }
        this.configurator.setPackageName(basePackage);
        this.configurator.setApiPackage(apiPackage);
        this.configurator.setModelPackage(modelPackage);
        this.configurator.setInvokerPackage(apiPackage);
    }

}
