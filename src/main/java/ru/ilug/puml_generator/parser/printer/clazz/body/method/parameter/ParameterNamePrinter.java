package ru.ilug.puml_generator.parser.printer.clazz.body.method.parameter;

import com.github.javaparser.ast.body.Parameter;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.PrinterType;

public class ParameterNamePrinter implements Printer {

    @Override
    public String getType() {
        return PrinterType.METHOD_PARAMETER.name();
    }

    @Override
    public int getPosition() {
        return 1000;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        Parameter parameter = properties.get(Parameter.class);
        return parameter.getNameAsString();
    }
}
