package ru.ilug.puml_generator.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.parser.printer.util.JavaTypesUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ClassFilter {

    private final Pattern[] includePackages;
    private final Pattern[] excludePackages;
    private final boolean enabledInterfaces;
    private final boolean enabledAbstractClasses;
    private final boolean enabledSubClasses;

    public boolean filter(ResolvedReferenceType referenceType) {
        return filterByPackage(referenceType) && filterByClassType(referenceType);
    }

    public boolean filter(TypeDeclaration<?> typeDeclaration) {
        return filterByPackage(typeDeclaration) && filterByClassType(typeDeclaration);
    }

    private boolean filterByPackage(ResolvedReferenceType referenceType) {
        String className = referenceType.getQualifiedName();
        return filterByPackage(className);
    }

    private boolean filterByPackage(TypeDeclaration<?> typeDeclaration) {
        String className = JavaTypesUtil.getTypeDeclarationName(typeDeclaration);
        return filterByPackage(className);
    }

    private boolean filterByPackage(String className) {
        for (Pattern include : includePackages) {
            Matcher matcher = include.matcher(className);
            if (!matcher.find()) {
                return false;
            }
        }

        for (Pattern exclude : excludePackages) {
            Matcher matcher = exclude.matcher(className);
            if (matcher.find()) {
                return false;
            }
        }

        return true;
    }

    private boolean filterByClassType(ResolvedReferenceType referenceType) {
        ResolvedReferenceTypeDeclaration referenceTypeDeclaration = referenceType.getTypeDeclaration().orElse(null);

        if (referenceTypeDeclaration != null) {
            TypeDeclaration<?> typeDeclaration = JavaTypesUtil.getDeclarationFromReference(referenceTypeDeclaration);

            if (typeDeclaration != null) {
                return filterByClassType(typeDeclaration);
            }
        }

        return true;
    }

    private boolean filterByClassType(TypeDeclaration<?> typeDeclaration) {
        if (typeDeclaration.isClassOrInterfaceDeclaration()) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = typeDeclaration.asClassOrInterfaceDeclaration();

            if (!enabledInterfaces && classOrInterfaceDeclaration.isInterface()) {
                return false;
            }

            if (!enabledAbstractClasses && classOrInterfaceDeclaration.isAbstract()) {
                return false;
            }

            if (!enabledSubClasses && !classOrInterfaceDeclaration.isTopLevelType()) {
                return false;
            }
        }

        return true;
    }

}
