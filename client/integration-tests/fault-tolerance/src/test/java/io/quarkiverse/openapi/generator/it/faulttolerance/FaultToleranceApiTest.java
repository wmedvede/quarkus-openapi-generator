package io.quarkiverse.openapi.generator.it.faulttolerance;

import static io.quarkiverse.openapi.generator.it.faulttolerance.assertions.Assertions.assertThat;
import static io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions.MethodAssert.CIRCUIT_BREAKER_ANNOTATION_NAME;
import static io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions.MethodAssert.RETRY_ANNOTATION_NAME;
import static io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions.MethodAssert.TIMEOUT_ANNOTATION_NAME;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class FaultToleranceApiTest {

    private static List<MethodDeclaration> methodDeclarations;

    @BeforeAll
    static void setup() throws IOException {
        Path generatedRestClient = Paths
                .get("target/generated-sources/open-api/org/acme/openapi/faulttolerance/api/DefaultApi.java");

        assertThat(generatedRestClient)
                .exists()
                .isRegularFile()
                .content().isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(generatedRestClient);

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                .map(c -> c.getAnnotationByClass(RegisterRestClient.class)).filter(Optional::isPresent).map(Optional::get)
                .map(a -> a.asNormalAnnotationExpr().getPairs())
                .forEach(n -> n.forEach(p -> assertNotEquals("baseUri", p.getName().asString())));

        methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();
    }

    @Test
    void withCircuitBreaker() {
        MethodDeclaration method = assertAndGetMethod("withCircuitBreakerGet");
        assertThat(method).hasAnnotation(CIRCUIT_BREAKER_ANNOTATION_NAME).doesNotNaveAnyAttribute();
        assertThat(method).doesNotHaveAnnotation(TIMEOUT_ANNOTATION_NAME);
        assertThat(method).doesNotHaveAnnotation(RETRY_ANNOTATION_NAME);
    }

    @Test
    void withTimeout() {
        MethodDeclaration method = assertAndGetMethod("withTimeoutGet");
        assertThat(method).hasAnnotation(TIMEOUT_ANNOTATION_NAME).doesNotNaveAnyAttribute();
        assertThat(method).doesNotHaveAnnotation(CIRCUIT_BREAKER_ANNOTATION_NAME);
        assertThat(method).doesNotHaveAnnotation(RETRY_ANNOTATION_NAME);
    }

    @Test
    void withRetry() {
        MethodDeclaration method = assertAndGetMethod("withRetryGet");
        assertThat(method).hasAnnotation(RETRY_ANNOTATION_NAME).doesNotNaveAnyAttribute();
        assertThat(method).doesNotHaveAnnotation(CIRCUIT_BREAKER_ANNOTATION_NAME);
        assertThat(method).doesNotHaveAnnotation(TIMEOUT_ANNOTATION_NAME);
    }

    @Test
    void withTimeoutRetryAndCircuitBreaker() {
        MethodDeclaration method = assertAndGetMethod("withTimeoutRetryAndCircuitBreakerGet");
        assertThat(method).hasAnnotation(TIMEOUT_ANNOTATION_NAME).doesNotNaveAnyAttribute();
        assertThat(method).hasAnnotation(RETRY_ANNOTATION_NAME).doesNotNaveAnyAttribute();
        assertThat(method).hasAnnotation(CIRCUIT_BREAKER_ANNOTATION_NAME).doesNotNaveAnyAttribute();
    }

    @Test
    void withoutFaultTolerance() {
        MethodDeclaration method = assertAndGetMethod("withoutFaultToleranceGet");
        assertThat(method).doesNotHaveAnnotation(CIRCUIT_BREAKER_ANNOTATION_NAME);
        assertThat(method).doesNotHaveAnnotation(TIMEOUT_ANNOTATION_NAME);
        assertThat(method).doesNotHaveAnnotation(RETRY_ANNOTATION_NAME);
    }

    private static MethodDeclaration assertAndGetMethod(String methodName) {
        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals(methodName))
                .findAny();
        assertThat(byeMethod)
                .withFailMessage("Method: %s is not present in CompilationUnit.", methodName)
                .isNotEmpty();
        return byeMethod.get();
    }
}
