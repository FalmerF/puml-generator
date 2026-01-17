package ru.ilug.puml_generator.parser.printer.clazz.body.field;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.PrinterType;

public class FieldTypePrinter implements Printer {

    @Override
    public String getType() {
        return PrinterType.FIELD.name();
    }

    @Override
    public int getPosition() {
        return 2000;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        VariableDeclarator variableDeclarator = properties.get(VariableDeclarator.class);
        return variableDeclarator.getTypeAsString();
    }
}
