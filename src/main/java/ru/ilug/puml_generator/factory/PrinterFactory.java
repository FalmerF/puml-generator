package ru.ilug.puml_generator.factory;

import ru.ilug.puml_generator.parser.printer.Printer;

public interface PrinterFactory {

    Printer createBasePrinter();

}
