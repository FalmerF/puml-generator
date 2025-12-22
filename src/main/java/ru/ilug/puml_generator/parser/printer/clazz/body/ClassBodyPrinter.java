package ru.ilug.puml_generator.parser.printer.clazz.body;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterProperties;

import java.util.List;

@RequiredArgsConstructor
public class ClassBodyPrinter implements Printer {

    private final boolean fieldsEnable, publicFields, privateFields, protectedFields, staticFields;
    private final boolean methodsEnable;
    private final List<Printer> fieldsPrinters;
    private final List<Printer> methodsPrinters;

    @Override
    public int getPosition() {
        return 200;
    }

    @Override
    public @Nullable String print(PrinterProperties properties) {
        CompilationUnit unit = properties.get(CompilationUnit.class);
        TypeDeclaration<?> typeDeclaration = properties.get(TypeDeclaration.class);

        StringBuilder builder = new StringBuilder("{");

        if (fieldsEnable) {
            printFields(unit, typeDeclaration, builder);
        }

        if (methodsEnable) {
            if (builder.length() > 1) {
                builder.append("\n");
            }

            printMethods(unit, typeDeclaration, builder);
        }

        return builder.append("\n}").toString();
    }

    private void printFields(CompilationUnit unit, TypeDeclaration<?> typeDeclaration, StringBuilder builder) {
        for (BodyDeclaration<?> declaration : typeDeclaration.getMembers()) {
            if (!declaration.isFieldDeclaration()) {
                continue;
            }

            FieldDeclaration fieldDeclaration = declaration.asFieldDeclaration();

            if (fieldDeclaration.getModifiers().stream().anyMatch(this::isFieldModifierDisallowed)) {
                continue;
            }

            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
                builder.append("\n    {field} ");
                PrinterProperties fieldProperties = new PrinterProperties(unit, typeDeclaration, fieldDeclaration, variableDeclarator);

                for (Printer printer : fieldsPrinters) {
                    String content = printer.print(fieldProperties);

                    if (content != null) {
                        builder.append(content).append(" ");
                    }
                }
            }
        }
    }

    private void printMethods(CompilationUnit unit, TypeDeclaration<?> typeDeclaration, StringBuilder builder) {
        for (BodyDeclaration<?> declaration : typeDeclaration.getMembers()) {
            if (!declaration.isMethodDeclaration()) {
                continue;
            }

            MethodDeclaration methodDeclaration = declaration.asMethodDeclaration();

            builder.append("\n    {method} ");
            PrinterProperties methodProperties = new PrinterProperties(unit, typeDeclaration, methodDeclaration);

            for (Printer printer : methodsPrinters) {
                String content = printer.print(methodProperties);

                if (content != null) {
                    builder.append(content);
                }
            }
        }
    }

    private boolean isFieldModifierDisallowed(Modifier modifier) {
        Modifier.Keyword keyword = modifier.getKeyword();

        return (keyword == Modifier.Keyword.PUBLIC && !publicFields)
                || (keyword == Modifier.Keyword.PRIVATE && !privateFields)
                || (keyword == Modifier.Keyword.PROTECTED && !protectedFields)
                || (keyword == Modifier.Keyword.STATIC && !staticFields);

    }
}
