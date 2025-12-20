package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.config.PackagesConfig;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.util.JavaTypesUtil;

import java.util.Objects;

@RequiredArgsConstructor
public class ClassDependenciesPrinter implements Printer {

    private final PackagesConfig packagesConfig;

    @Override
    public int getPosition() {
        return 300;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        CompilationUnit unit = properties.get(CompilationUnit.class);
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        if (!typeDeclaration.isClassOrInterfaceDeclaration()) {
            return null;
        }

        String typeName = JavaTypesUtil.getTypeDeclarationName(unit, typeDeclaration);

        StringBuilder builder = new StringBuilder();
        ClassOrInterfaceDeclaration declaration = typeDeclaration.asClassOrInterfaceDeclaration();

        filterAndAppendRelations(declaration.getExtendedTypes(), unit, builder, typeName, " <|-- ");
        filterAndAppendRelations(declaration.getImplementedTypes(), unit, builder, typeName, " <|.. ");

        return builder.isEmpty() ? null : builder.toString();
    }

    private void filterAndAppendRelations(NodeList<ClassOrInterfaceType> nodeList, CompilationUnit unit,
                                          StringBuilder builder, String typeName, String arrow) {
        nodeList.stream()
                .map(t -> JavaTypesUtil.getClassOrInterfaceTypeName(unit, t))
                .filter(Objects::nonNull)
                .filter(n -> JavaTypesUtil.filterPackage(n, packagesConfig))
                .forEach(name -> builder.append("\n").append(name).append(arrow).append(typeName));
    }
}
