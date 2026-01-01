package ru.ilug.puml_generator.parser;

import com.github.javaparser.ast.CompilationUnit;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.converter.JavaUnitParser;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;
import ru.ilug.puml_generator.parser.printer.UnitPrinter;

@RequiredArgsConstructor
public class JavaUnitParserImpl implements JavaUnitParser {

    private final Printer basePrinter;

    @Override
    public String parse(CompilationUnit unit) {
        PrinterProperties properties = new PrinterProperties(unit);
        return basePrinter.print(properties);
    }
}
