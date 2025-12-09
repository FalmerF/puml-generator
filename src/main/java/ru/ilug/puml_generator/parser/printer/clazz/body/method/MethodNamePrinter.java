package ru.ilug.puml_generator.parser.printer.clazz.body.method;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

public class MethodNamePrinter implements Printer {

    @Override
    public int getPosition() {
        return 200;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        MethodDeclaration methodDeclaration = properties.get(MethodDeclaration.class);
        return methodDeclaration.getNameAsString();
    }
}
