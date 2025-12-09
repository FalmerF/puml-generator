package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

public class ClassTypePrinter implements Printer {

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        if (typeDeclaration.isRecordDeclaration()) {
            return "record";
        }

        if (typeDeclaration.isClassOrInterfaceDeclaration() && typeDeclaration.asClassOrInterfaceDeclaration().isInterface()) {
            return "interface";
        }

        if (typeDeclaration.getModifiers().stream().anyMatch(m -> m.getKeyword() == Modifier.Keyword.ABSTRACT)) {
            return "abstract";
        }

        return "class";
    }
}
