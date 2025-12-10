package ru.ilug.puml_generator.parser.printer.clazz.body.method;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

public class MethodModifierPrinter implements Printer {

    @Override
    public int getPosition() {
        return 200;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        MethodDeclaration methodDeclaration = properties.get(MethodDeclaration.class);
        for (Modifier modifier : methodDeclaration.getModifiers()) {
            Modifier.Keyword keyword = modifier.getKeyword();

            if (keyword == Modifier.Keyword.STATIC) {
                return "{static}";
            } else if (keyword == Modifier.Keyword.ABSTRACT) {
                return "{abstract}";
            }
        }

        return null;
    }
}
