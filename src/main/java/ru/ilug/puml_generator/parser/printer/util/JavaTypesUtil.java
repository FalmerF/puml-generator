package ru.ilug.puml_generator.parser.printer.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class JavaTypesUtil {

    public static String getTypeDeclarationName(TypeDeclaration<?> typeDeclaration) {
        try {
            return getFullyQualifiedName(typeDeclaration).orElseThrow();
        } catch (Exception e) {
            if (typeDeclaration.isClassOrInterfaceDeclaration()) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = typeDeclaration.asClassOrInterfaceDeclaration();
                CompilationUnit unit = classOrInterfaceDeclaration.findCompilationUnit().orElse(null);

                if (unit != null) {
                    PackageDeclaration packageDeclaration = unit.getPackageDeclaration().orElse(null);

                    if (packageDeclaration != null) {
                        return packageDeclaration.getNameAsString() + "." + typeDeclaration.getNameAsString();
                    }
                }
            }

            return typeDeclaration.getNameAsString();
        }
    }

    @SuppressWarnings("unchecked")
    public static Optional<String> getFullyQualifiedName(TypeDeclaration<?> typeDeclaration) {
        if (typeDeclaration.isTopLevelType()) {
            return typeDeclaration.findCompilationUnit().map(cu -> cu.getPackageDeclaration()
                    .map(NodeWithName::getNameAsString)
                    .map(pkg -> pkg + "." + typeDeclaration.getNameAsString())
                    .orElseGet(typeDeclaration::getNameAsString));
        }
        return typeDeclaration.findAncestor(TypeDeclaration.class)
                .map(td -> (TypeDeclaration<?>) td)
                .flatMap(td -> getFullyQualifiedName(td)
                        .map(fqn -> fqn + "#" + typeDeclaration.getNameAsString()));
    }

    @Nullable
    public static ResolvedReferenceType resolveReferenceType(ReferenceType referenceType) {
        try {
            SymbolResolver resolver = StaticJavaParser.getParserConfiguration().getSymbolResolver().orElseThrow();
            ResolvedType resolvedType = resolver.toResolvedType(referenceType, ResolvedType.class);
            return resolvedType.asReferenceType();
        } catch (UnsolvedSymbolException | UnsupportedOperationException e) {
            return null;
        }
    }

    @Nullable
    public static TypeDeclaration<?> getDeclarationFromReference(ResolvedReferenceTypeDeclaration resolvedTypeDeclaration) {
        if (resolvedTypeDeclaration instanceof JavaParserInterfaceDeclaration declaration) {
            return declaration.getWrappedNode();
        } else if (resolvedTypeDeclaration instanceof JavaParserClassDeclaration declaration) {
            return declaration.getWrappedNode();
        }

        return null;
    }
}
