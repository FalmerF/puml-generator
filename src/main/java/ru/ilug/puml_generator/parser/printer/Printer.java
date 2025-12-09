package ru.ilug.puml_generator.parser.printer;

import org.jspecify.annotations.Nullable;

public interface Printer {

    int getPosition();

    @Nullable
    String print(PrinterProperties properties);

}
