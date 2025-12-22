package ru.ilug.puml_generator.parser.printer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.ilug.puml_generator.parser.ClassFilter;

import java.util.List;

@Setter
@RequiredArgsConstructor
public class UnitPrinter implements Printer {

    private final ClassFilter classFilter;
    private final List<Printer> classPrinters;

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public String print(PrinterProperties properties) {
        CompilationUnit unit = properties.get(CompilationUnit.class);

        StringBuilder builder = new StringBuilder();

        for (TypeDeclaration<?> typeDeclaration : unit.getTypes()) {
            if (!classFilter.filter(typeDeclaration)) {
                continue;
            }

            PrinterProperties classProperties = new PrinterProperties(unit, typeDeclaration);

            StringBuilder classBuilder = new StringBuilder();

            for (Printer classPrinter : classPrinters) {
                String content = classPrinter.print(classProperties);

                if (content != null) {
                    classBuilder.append(content).append(" ");
                }
            }

            if (!classBuilder.isEmpty()) {
                builder.append("\n").append(classBuilder).append("\n");
            }
        }

        return builder.toString();
    }

}
