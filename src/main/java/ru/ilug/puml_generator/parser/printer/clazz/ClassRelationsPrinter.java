package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.config.PackagesConfig;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.util.DependencyVisitor;
import ru.ilug.puml_generator.util.JavaTypesUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ClassRelationsPrinter implements Printer {

    private final PackagesConfig packagesConfig;

    @Override
    public int getPosition() {
        return 400;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        CompilationUnit unit = properties.get(CompilationUnit.class);
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        if (!typeDeclaration.isClassOrInterfaceDeclaration() || !typeDeclaration.isTopLevelType()) {
            return null;
        }

        String typeName = JavaTypesUtil.getTypeDeclarationName(unit, typeDeclaration);

        Set<String> dependencies = getDependencies(unit, typeDeclaration, true);
        Set<String> relations = findAllRelations(unit);

        StringBuilder builder = new StringBuilder();

        for (String relation : relations) {
            if (dependencies.contains(relation) || relation.equals(typeName) || !JavaTypesUtil.filterPackage(relation, packagesConfig)) {
                continue;
            }

            builder.append("\n").append(typeName).append(" --> ").append(relation);
        }

        return builder.isEmpty() ? null : builder.toString();
    }

    private Set<String> getDependencies(CompilationUnit unit, TypeDeclaration<?> typeDeclaration, boolean includeInheritanceDependencies) {
        ClassOrInterfaceDeclaration declaration = typeDeclaration.asClassOrInterfaceDeclaration();

        Set<String> dependencies = Stream.concat(declaration.getExtendedTypes().stream(), declaration.getImplementedTypes().stream())
                .map(c -> JavaTypesUtil.getClassOrInterfaceTypeName(unit, c))
                .collect(Collectors.toSet());

        if (includeInheritanceDependencies) {
            Set<String> inheritanceDependencies = findInheritanceDependencies(declaration);
            dependencies.addAll(inheritanceDependencies);
        }

        return dependencies;
    }

    private Set<String> findInheritanceDependencies(ClassOrInterfaceDeclaration declaration) {
        return Stream.concat(declaration.getExtendedTypes().stream(), declaration.getImplementedTypes().stream())
                .map(c -> {
                    try {
                        SymbolResolver resolver = StaticJavaParser.getParserConfiguration().getSymbolResolver().orElseThrow();
                        ResolvedType resolvedType = resolver.toResolvedType(c, ResolvedType.class);
                        ResolvedReferenceType resolvedReferenceType = resolvedType.asReferenceType();
                        ResolvedReferenceTypeDeclaration resolvedTypeDeclaration = resolvedReferenceType.getTypeDeclaration().orElseThrow();

                        if (resolvedTypeDeclaration instanceof JavaParserInterfaceDeclaration declaration1) {
                            return declaration1.getWrappedNode();
                        } else if (resolvedTypeDeclaration instanceof JavaParserClassDeclaration declaration1) {
                            return declaration1.getWrappedNode();
                        }
                    } catch (Exception ignore) {
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(c -> {
                    Optional<CompilationUnit> compilationUnitOptional = c.findCompilationUnit();

                    if (compilationUnitOptional.isEmpty()) {
                        return Stream.empty();
                    }

                    CompilationUnit unit1 = compilationUnitOptional.get();
                    Set<String> relations = findAllRelations(unit1);
                    Set<String> dependencies = getDependencies(unit1, c, true);

                    return Stream.concat(relations.stream(), dependencies.stream());
                }).collect(Collectors.toSet());
    }

    private Set<String> findAllRelations(CompilationUnit unit) {
        Set<String> relations = new HashSet<>();
        unit.accept(new DependencyVisitor(relations), unit);

        return relations;
    }
}
