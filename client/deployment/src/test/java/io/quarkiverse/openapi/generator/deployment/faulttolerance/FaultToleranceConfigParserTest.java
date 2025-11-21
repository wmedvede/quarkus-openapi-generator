package io.quarkiverse.openapi.generator.deployment.faulttolerance;

import static io.quarkiverse.openapi.generator.deployment.assertions.Assertions.assertThat;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.deployment.MockConfigUtils;

class FaultToleranceConfigParserTest {
    @Test
    void enabledFaultTolerance() {
        Config config = MockConfigUtils.getTestConfig("/faulttolerance/enabled_fault_tolerance_application.properties");
        FaultToleranceConfig faultToleranceConfig = FaultToleranceConfigParser.parse(config);

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getCountries")).isTrue();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getCountries")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getCountries")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getByCapital")).isTrue();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getByCapital")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getByCapital")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CityResource", "get")).isTrue();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CityResource", "get")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CityResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "RetryResource", "get")).isTrue();
        assertThat(faultToleranceConfig.hasRetry("org.acme", "RetryResource", "get")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "RetryResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "TimeoutResource", "get")).isTrue();
        assertThat(faultToleranceConfig.hasTimeout("org.acme", "TimeoutResource", "get")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "TimeoutResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "MultiFaultToleranceResource", "get")).isTrue();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "MultiFaultToleranceResource", "get")).isNotNull();
        assertThat(faultToleranceConfig.hasTimeout("org.acme", "MultiFaultToleranceResource", "get")).isTrue();
        assertThat(faultToleranceConfig.hasRetry("org.acme", "MultiFaultToleranceResource", "get")).isTrue();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "MultiFaultToleranceResource", "get")).isTrue();
    }

    @Test
    void disabledFaultTolerance() {
        Config config = MockConfigUtils.getTestConfig("/faulttolerance/disabled_fault_tolerance_application.properties");
        FaultToleranceConfig faultToleranceConfig = FaultToleranceConfigParser.parse(config);

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getCountries")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getCountries")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getCountries")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getByCapital")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getByCapital")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getByCapital")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CityResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CityResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CityResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "RetryResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasRetry("org.acme", "RetryResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "RetryResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "TimeoutResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasTimeout("org.acme", "TimeoutResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "TimeoutResource", "get")).isNotNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "MultiFaultToleranceResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "MultiFaultToleranceResource", "get")).isNotNull();
        assertThat(faultToleranceConfig.hasTimeout("org.acme", "MultiFaultToleranceResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasRetry("org.acme", "MultiFaultToleranceResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "MultiFaultToleranceResource", "get")).isFalse();
    }

    @Test
    void missingFaultTolerance() {
        Config config = MockConfigUtils.getTestConfig("/faulttolerance/missing_enable_fault_tolerance_application.properties");
        FaultToleranceConfig faultToleranceConfig = FaultToleranceConfigParser.parse(config);

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getCountries")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getCountries")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getCountries")).isNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CountryResource", "getByCapital")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CountryResource", "getByCapital")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CountryResource", "getByCapital")).isNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "CityResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasCircuitBreaker("org.acme", "CityResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "CityResource", "get")).isNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "RetryResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasRetry("org.acme", "RetryResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "RetryResource", "get")).isNull();

        assertThat(faultToleranceConfig.hasFaultTolerance("org.acme", "TimeoutResource", "get")).isFalse();
        assertThat(faultToleranceConfig.hasTimeout("org.acme", "TimeoutResource", "get")).isFalse();
        assertThat(faultToleranceConfig.getMethodFaultTolerance("org.acme", "TimeoutResource", "get")).isNull();
    }
}
