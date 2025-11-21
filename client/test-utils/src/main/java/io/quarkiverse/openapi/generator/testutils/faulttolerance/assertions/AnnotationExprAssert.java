package io.quarkiverse.openapi.generator.testutils.faulttolerance.assertions;

import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;

public class AnnotationExprAssert extends AbstractAssert<AnnotationExprAssert, AnnotationExpr> {

    AnnotationExprAssert(AnnotationExpr actual) {
        super(actual, AnnotationExprAssert.class);
    }

    public AnnotationExprAssert doesNotNaveAnyAttribute() {
        List<MemberValuePair> valuePairs = actual.getChildNodes().stream()
                .filter(MemberValuePair.class::isInstance)
                .map(MemberValuePair.class::cast)
                .toList();

        if (valuePairs.size() > 0) {
            failWithMessage("%s annotation is expected not to have any attributes, but has: %d",
                    actual.getNameAsString(), valuePairs.size());
        }
        return this;
    }
}
