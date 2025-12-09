package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.TypeParameter;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

import java.util.stream.Collectors;

public class ClassGenericsPrinter implements Printer {
    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        if(!typeDeclaration.isClassOrInterfaceDeclaration()) {
            return null;
        }

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = typeDeclaration.asClassOrInterfaceDeclaration();
        String content = classOrInterfaceDeclaration.getTypeParameters().stream()
                .map(TypeParameter::asString)
                .collect(Collectors.joining(", "));

        return content.isEmpty() ? null : "<%s>".formatted(content);
    }
}
