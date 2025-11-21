package io.quarkiverse.openapi.generator.it.faulttolerance.assertions;

import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions.MethodAssert;

public final class Assertions extends org.assertj.core.api.Assertions {

    private Assertions() {
    }

    public static MethodAssert assertThat(MethodDeclaration actual) {
        return MethodAssert.assertThat(actual);
    }
}
