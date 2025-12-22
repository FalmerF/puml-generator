package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.ClassFilter;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.util.JavaTypesUtil;

import java.util.Objects;

@RequiredArgsConstructor
public class ClassDependenciesPrinter implements Printer {

    private final ClassFilter classFilter;

    @Override
    public int getPosition() {
        return 300;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        if (!typeDeclaration.isClassOrInterfaceDeclaration()) {
            return null;
        }

        String typeName = JavaTypesUtil.getTypeDeclarationName(typeDeclaration);

        StringBuilder builder = new StringBuilder();
        ClassOrInterfaceDeclaration declaration = typeDeclaration.asClassOrInterfaceDeclaration();

        filterAndAppendRelations(declaration.getExtendedTypes(), builder, typeName, " <|-- ");
        filterAndAppendRelations(declaration.getImplementedTypes(), builder, typeName, " <|.. ");

        return builder.isEmpty() ? null : builder.toString();
    }

    private void filterAndAppendRelations(NodeList<ClassOrInterfaceType> nodeList,
                                          StringBuilder builder, String typeName, String arrow) {
        nodeList.stream()
                .map(JavaTypesUtil::resolveReferenceType)
                .filter(Objects::nonNull)
                .filter(classFilter::filter)
                .map(ResolvedReferenceType::getQualifiedName)
                .forEach(name -> builder.append("\n").append(name).append(arrow).append(typeName));
    }
}
