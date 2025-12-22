package ru.ilug.puml_generator.parser.printer.clazz.body.field;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

public class FieldVisibilityPrinter implements Printer {

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        FieldDeclaration fieldDeclaration = properties.get(FieldDeclaration.class);

        for (Modifier modifier : fieldDeclaration.getModifiers()) {
            Modifier.Keyword keyword = modifier.getKeyword();
            if (keyword == Modifier.Keyword.PUBLIC) {
                return "+";
            } else if (keyword == Modifier.Keyword.PROTECTED) {
                return "#";
            } else if (keyword == Modifier.Keyword.PRIVATE) {
                return "-";
            }
        }

        return null;
    }
}
