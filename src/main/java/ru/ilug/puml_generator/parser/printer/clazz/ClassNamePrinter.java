package ru.ilug.puml_generator.parser.printer.clazz;

import com.github.javaparser.ast.body.TypeDeclaration;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.util.JavaTypesUtil;

public class ClassNamePrinter implements Printer {

    @Override
    public int getPosition() {
        return 100;
    }

    @Override
    public String print(PrinterProperties properties) {
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);
        return JavaTypesUtil.getTypeDeclarationName(typeDeclaration);
    }
}
