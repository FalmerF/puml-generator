package ru.ilug.puml_generator.parser.printer.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class DependencyVisitor extends VoidVisitorAdapter<CompilationUnit> {

    private final Set<ResolvedReferenceType> dependencies;

    @Override
    public void visit(ClassOrInterfaceType classOrInterfaceType, CompilationUnit unit) {
        ResolvedReferenceType referenceType = JavaTypesUtil.resolveReferenceType(classOrInterfaceType);
        if (referenceType != null) {
            dependencies.add(referenceType);
        }

        super.visit(classOrInterfaceType, unit);
    }

    @Override
    public void visit(MethodCallExpr methodCallExpr, CompilationUnit unit) {
        try {
            Optional<Expression> scopeOptional = methodCallExpr.getScope();

            if (scopeOptional.isPresent()) {
                Expression scope = scopeOptional.get();

                if (scope instanceof NameExpr || scope instanceof FieldAccessExpr) {
                    ResolvedType resolvedType = methodCallExpr.calculateResolvedType();
                    ResolvedReferenceType resolvedReferenceType = resolvedType.asReferenceType();
                    dependencies.add(resolvedReferenceType);
                }
            }
        } catch (Exception ignore) {
        }
        super.visit(methodCallExpr, unit);
    }
}