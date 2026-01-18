package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.PrinterType;
import ru.ilug.puml_generator.parser.printer.util.JavaTypesUtil;

import java.util.Optional;

public class ClassJavadocPrinter implements Printer {

    private static final String NOTE_PATTERN = "\nnote top of %s : %s";

    @Override
    public String getType() {
        return PrinterType.CLASS.name();
    }

    @Override
    public int getPosition() {
        return 6000;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        Optional<Javadoc> javadocOptional = typeDeclaration.getJavadoc();
        if (javadocOptional.isPresent()) {
            Javadoc javadoc = javadocOptional.get();
            String javadocText = javadoc.toText();
            javadocText = javadocText.replaceAll("(\\r\\n)", "\\\\n");

            String typeName = JavaTypesUtil.getTypeDeclarationName(typeDeclaration);
            return NOTE_PATTERN.formatted(typeName, javadocText);
        }

        return null;
    }
}
