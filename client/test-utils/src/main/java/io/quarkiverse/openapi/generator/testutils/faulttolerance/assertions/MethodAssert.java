package io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions;

import org.assertj.core.api.AbstractAssert;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

public final class MethodAssert extends AbstractAssert<MethodAssert, MethodDeclaration> {

    public static final String CIRCUIT_BREAKER_ANNOTATION_NAME = "CircuitBreaker";
    public static final String TIMEOUT_ANNOTATION_NAME = "Timeout";
    public static final String RETRY_ANNOTATION_NAME = "Retry";

    private MethodAssert(MethodDeclaration actual) {
        super(actual, MethodAssert.class);
    }

    public static MethodAssert assertThat(MethodDeclaration actual) {
        return new MethodAssert(actual);
    }

    public AnnotationExprAssert hasAnnotation(String annotationName) {
        AnnotationExpr annotationExpr = actual.getAnnotationByName(annotationName).orElse(null);
        if (annotationExpr == null) {
            failWithMessage("Method named %s is expected to have the %s annotation, but it doesn't",
                    actual.getNameAsString(), annotationName);
        }
        return new AnnotationExprAssert(annotationExpr);
    }

    public MethodAssert doesNotHaveAnnotation(String annotationName) {
        if (actual.getAnnotationByName(annotationName).isPresent()) {
            failWithMessage("Method named %s is expected to not have the %s annotation, but it does",
                    actual.getNameAsString(), annotationName);
        }
        return this;
    }
}
