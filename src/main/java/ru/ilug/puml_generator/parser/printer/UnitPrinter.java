package ru.ilug.puml_generator.parser.printer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.ilug.puml_generator.parser.ClassFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        printTypeDeclarations(unit, unit.getTypes(), builder);

        return builder.toString();
    }

    private void printTypeDeclarations(CompilationUnit unit, Iterable<TypeDeclaration<?>> typeDeclarations, StringBuilder builder) {
        for (TypeDeclaration<?> typeDeclaration : typeDeclarations) {
            String printedClass = printTypeDeclaration(unit, typeDeclaration);

            if (!printedClass.isEmpty()) {
                builder.append("\n").append(printedClass).append("\n");
            }

            Set<TypeDeclaration<?>> subClasses = typeDeclaration.getMembers().stream()
                    .filter(BodyDeclaration::isTypeDeclaration)
                    .map(d -> (TypeDeclaration<?>) d.asTypeDeclaration())
                    .collect(Collectors.toSet());

            printTypeDeclarations(unit, subClasses, builder);
        }
    }

    private String printTypeDeclaration(CompilationUnit unit, TypeDeclaration<?> typeDeclaration) {
        if (!classFilter.filter(typeDeclaration)) {
            return "";
        }

        PrinterProperties classProperties = new PrinterProperties(unit, typeDeclaration);

        StringBuilder classBuilder = new StringBuilder();

        for (Printer classPrinter : classPrinters) {
            String content = classPrinter.print(classProperties);

            if (content != null) {
                classBuilder.append(content).append(" ");
            }
        }

        return classBuilder.toString();
    }

}
