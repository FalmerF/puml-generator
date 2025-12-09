package ru.ilug.puml_generator.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class DependencyVisitor extends VoidVisitorAdapter<CompilationUnit> {

    private final Set<String> dependencies;

    @Override
    public void visit(ClassOrInterfaceType classOrInterfaceType, CompilationUnit unit) {
        if (classOrInterfaceType.isReferenceType()) {
            String resolvedName = JavaTypesUtil.getClassOrInterfaceTypeName(unit, classOrInterfaceType);
            if (resolvedName != null) {
                dependencies.add(resolvedName);
            }
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
                    ResolvedReferenceTypeDeclaration type = methodCallExpr.resolve().declaringType();
                    dependencies.add(type.getQualifiedName());
                }
            }
        } catch (Exception ignore) {
        }
        super.visit(methodCallExpr, unit);
    }
}