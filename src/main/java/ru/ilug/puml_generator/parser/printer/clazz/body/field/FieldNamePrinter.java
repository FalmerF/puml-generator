package ru.ilug.puml_generator.parser.printer.clazz.body.field;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

public class FieldNamePrinter implements Printer {

    @Override
    public int getPosition() {
        return 200;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        VariableDeclarator variableDeclarator = properties.get(VariableDeclarator.class);
        return variableDeclarator.getNameAsString();
    }
}
