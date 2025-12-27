package ru.ilug.puml_generator.parser.printer;

import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.config.PackagesConfig;
import ru.ilug.puml_generator.parser.ClassFilter;
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

public class DefaultPrinters {

    public static UnitPrinter createUnitPrinter(Config config) {
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
        printers.add(new ClassDependenciesPrinter(classFilter));
        printers.add(new ClassRelationsPrinter(classFilter));

        return new UnitPrinter(classFilter, printers);
    }

    public static ClassBodyPrinter createClassBodyPrinter(Config config) {
        return new ClassBodyPrinter(
                config.isFields(), config.isPublicFields(), config.isPrivateFields(), config.isProtectedFields(), config.isStaticFields(),
                config.isMethods(), config.isPublicMethods(), config.isPrivateMethods(), config.isProtectedMethods(), config.isStaticMethods(), config.isAbstractMethods(),
                createFieldPrinters(config), createMethodPrinters(config)
        );
    }

    public static List<Printer> createFieldPrinters(Config config) {
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

    public static List<Printer> createMethodPrinters(Config config) {
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
