package ru.ilug.puml_generator.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.types.ResolvedType;
import ru.ilug.puml_generator.config.PackagesConfig;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTypesUtil {

    public static String getTypeDeclarationName(CompilationUnit unit, TypeDeclaration<?> typeDeclaration) {
        try {
            return typeDeclaration.resolve().getQualifiedName();
        } catch (Exception e) {
            PackageDeclaration packageDeclaration = unit.getPackageDeclaration().orElse(null);

            if (packageDeclaration != null) {
                return packageDeclaration.getNameAsString() + "." + typeDeclaration.getNameAsString();
            }

            return typeDeclaration.getNameAsString();
        }
    }

    public static String getClassOrInterfaceTypeName(CompilationUnit unit, ClassOrInterfaceType classOrInterfaceType) {
        try {
            SymbolResolver resolver = StaticJavaParser.getParserConfiguration().getSymbolResolver().orElseThrow();
            ResolvedType resolvedType = resolver.toResolvedType(classOrInterfaceType, ResolvedType.class);
            return resolvedType.asReferenceType().getQualifiedName();
        } catch (Exception e) {
            String identifier = classOrInterfaceType.getNameAsString();

            Optional<ImportDeclaration> importDeclarationOptional = unit.getImports().stream()
                    .filter(i -> i.getName().getIdentifier().equals(identifier)).
                    findFirst();

            if (importDeclarationOptional.isPresent()) {
                ImportDeclaration importDeclaration = importDeclarationOptional.get();
                return importDeclaration.getNameAsString();
            } else {
                return null;
            }
        }
    }

    public static boolean filterPackage(String importName, PackagesConfig packagesConfig) {
        for (Pattern include : packagesConfig.include()) {
            Matcher matcher = include.matcher(importName);
            if (!matcher.find()) {
                return false;
            }
        }

        for (Pattern exclude : packagesConfig.exclude()) {
            Matcher matcher = exclude.matcher(importName);
            if (matcher.find()) {
                return false;
            }
        }

        return true;
    }

}
