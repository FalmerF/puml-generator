package ru.ilug.puml_generator.parser.printer.clazz.body.field;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.PrinterType;

public class FieldStaticModifierPrinter implements Printer {

    @Override
    public String getType() {
        return PrinterType.FIELD.name();
    }

    @Override
    public int getPosition() {
        return 1000;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        FieldDeclaration fieldDeclaration = properties.get(FieldDeclaration.class);
        return fieldDeclaration.getModifiers().stream().anyMatch(m -> m.getKeyword() == Modifier.Keyword.STATIC)
                ? "{static}" : null;
    }
}
