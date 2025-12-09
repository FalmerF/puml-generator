package ru.ilug.puml_generator.parser.printer.clazz.body.method;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MethodArgumentsPrinter implements Printer {

    private final List<Printer> methodParameterPrinters;

    @Override
    public int getPosition() {
        return 300;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        MethodDeclaration methodDeclaration = properties.get(MethodDeclaration.class);
        StringBuilder builder = new StringBuilder("(");

        String result = methodDeclaration.getParameters().stream()
                .map(parameter -> {
                    PrinterProperties methodProperties = properties.clone();
                    methodProperties.put(parameter);

                    return methodParameterPrinters.stream()
                            .map(printer -> printer.print(methodProperties))
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(" "));
                }).collect(Collectors.joining(", "));

        return builder.append(result).append(")").toString();
    }
}
