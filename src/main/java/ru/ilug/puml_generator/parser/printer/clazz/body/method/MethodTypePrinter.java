package ru.ilug.puml_generator.parser.printer.clazz.body.method;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.PrinterType;

public class MethodTypePrinter implements Printer {

    @Override
    public String getType() {
        return PrinterType.METHOD.name();
    }

    @Override
    public int getPosition() {
        return 2000;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        MethodDeclaration methodDeclaration = properties.get(MethodDeclaration.class);
        return methodDeclaration.getType().asString() + " ";
    }
}
