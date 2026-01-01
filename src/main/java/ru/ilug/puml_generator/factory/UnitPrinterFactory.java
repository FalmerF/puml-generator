package ru.ilug.puml_generator.factory;

import com.github.javaparser.JavaParser;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.config.PackagesConfig;
import ru.ilug.puml_generator.parser.ClassFilter;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.UnitPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.*;
import ru.ilug.puml_generator.parser.printer.clazz.body.ClassBodyPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldNamePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldStaticModifierPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldTypePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldVisibilityPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.*;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.parameter.ParameterNamePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.parameter.ParameterTypePrinter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UnitPrinterFactory implements PrinterFactory {

    private final Config config;
    private final JavaParser javaParser;

    @Override
    public Printer createBasePrinter() {
        PackagesConfig packagesConfig = config.getPackages();
        ClassFilter classFilter = new ClassFilter(
                packagesConfig.include(), packagesConfig.exclude(),
                config.isInterfaces(), config.isAbstractClasses(), config.isSubClasses()
        );

        List<Printer> printers = new ArrayList<>();
        printers.add(new ClassTypePrinter());
        printers.add(new ClassNamePrinter());

        if (config.isGenerics()) {
            printers.add(new ClassGenericsPrinter());
        }

        printers.add(createClassBodyPrinter(config));
        printers.add(new ClassDependenciesPrinter(classFilter, javaParser));
        printers.add(new ClassRelationsPrinter(classFilter, javaParser));

        return new UnitPrinter(classFilter, printers);
    }

    private ClassBodyPrinter createClassBodyPrinter(Config config) {
        return new ClassBodyPrinter(
                config.isFields(), config.isPublicFields(), config.isPrivateFields(), config.isProtectedFields(), config.isStaticFields(),
                config.isMethods(), config.isPublicMethods(), config.isPrivateMethods(), config.isProtectedMethods(), config.isStaticMethods(), config.isAbstractMethods(),
                createFieldPrinters(), createMethodPrinters()
        );
    }

    private List<Printer> createFieldPrinters() {
        List<Printer> printers = new ArrayList<>();

        if (config.isFieldVisibility()) {
            printers.add(new FieldVisibilityPrinter());
        }

        printers.add(new FieldStaticModifierPrinter());

        if (config.isFieldType()) {
            printers.add(new FieldTypePrinter());
        }

        if (config.isFieldName()) {
            printers.add(new FieldNamePrinter());
        }

        return printers;
    }

    private List<Printer> createMethodPrinters() {
        List<Printer> printers = new ArrayList<>();

        if (config.isMethodVisibility()) {
            printers.add(new MethodVisibilityPrinter());
        }
        printers.add(new MethodModifierPrinter());

        if (config.isMethodType()) {
            printers.add(new MethodTypePrinter());
        }

        if (config.isMethodName()) {
            printers.add(new MethodNamePrinter());
        }

        List<Printer> methodArgPrinters = new ArrayList<>();

        if (config.isMethodArgs()) {
            if (config.isMethodArgsType()) {
                methodArgPrinters.add(new ParameterTypePrinter());
            }

            if (config.isMethodArgsName()) {
                methodArgPrinters.add(new ParameterNamePrinter());
            }
        }

        printers.add(new MethodArgumentsPrinter(methodArgPrinters));

        return printers;
    }
}
