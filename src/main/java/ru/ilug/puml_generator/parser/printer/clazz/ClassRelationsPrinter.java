package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.ClassFilter;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.util.DependencyVisitor;
import ru.ilug.puml_generator.parser.printer.util.JavaTypesUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ClassRelationsPrinter implements Printer {

    private final ClassFilter classFilter;
    private final JavaParser javaParser;

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

        String typeName = JavaTypesUtil.getTypeDeclarationName(typeDeclaration);

        Set<ResolvedReferenceType> dependencies = getDependencies(typeDeclaration);
        Set<ResolvedReferenceType> relations = findAllRelations(unit);

        StringBuilder builder = new StringBuilder();

        for (ResolvedReferenceType relation : relations) {
            String qualifiedName = relation.getQualifiedName();
            if (dependencies.contains(relation) || qualifiedName.equals(typeName) || !classFilter.filter(relation)) {
                continue;
            }

            builder.append("\n")
                    .append("\"").append(typeName).append("\"")
                    .append(" --> ")
                    .append("\"").append(relation.getQualifiedName()).append("\"");
        }

        return builder.isEmpty() ? null : builder.toString();
    }

    private Set<ResolvedReferenceType> getDependencies(TypeDeclaration<?> typeDeclaration) {
        ClassOrInterfaceDeclaration declaration = typeDeclaration.asClassOrInterfaceDeclaration();

        Set<ResolvedReferenceType> dependencies = Stream.concat(declaration.getExtendedTypes().stream(), declaration.getImplementedTypes().stream())
                .map(c -> JavaTypesUtil.resolveReferenceType(javaParser, c))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<ResolvedReferenceType> inheritanceDependencies = findInheritanceDependencies(declaration);
        dependencies.addAll(inheritanceDependencies);

        return dependencies;
    }

    private Set<ResolvedReferenceType> findInheritanceDependencies(ClassOrInterfaceDeclaration declaration) {
        return Stream.concat(declaration.getExtendedTypes().stream(), declaration.getImplementedTypes().stream())
                .map(c -> {
                    try {
                        ResolvedReferenceType resolvedReferenceType = JavaTypesUtil.resolveReferenceType(javaParser, c);
                        if (resolvedReferenceType != null) {
                            ResolvedReferenceTypeDeclaration resolvedTypeDeclaration = resolvedReferenceType.getTypeDeclaration().orElseThrow();

                            if (resolvedTypeDeclaration instanceof JavaParserInterfaceDeclaration declaration1) {
                                return declaration1.getWrappedNode();
                            } else if (resolvedTypeDeclaration instanceof JavaParserClassDeclaration declaration1) {
                                return declaration1.getWrappedNode();
                            }
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
                    Set<ResolvedReferenceType> relations = findAllRelations(unit1);
                    Set<ResolvedReferenceType> dependencies = getDependencies(c);

                    return Stream.concat(relations.stream(), dependencies.stream());
                }).collect(Collectors.toSet());
    }

    private Set<ResolvedReferenceType> findAllRelations(CompilationUnit unit) {
        Set<ResolvedReferenceType> relations = new HashSet<>();
        unit.accept(new DependencyVisitor(relations, javaParser), unit);

        return relations;
    }
}
