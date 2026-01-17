package ru.ilug.puml_generator.factory;

import com.github.javaparser.JavaParser;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.parser.ClassFilter;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.PrinterType;
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

import java.util.*;

@RequiredArgsConstructor
public class UnitPrinterFactory implements BasePrinterFactory {

    private final Config config;
    private final JavaParser javaParser;
    private final Collection<Printer> additionalPrinters = new LinkedList<>();
    private final Map<String, List<Printer>> printersMap = new HashMap<>();

    public void addPrinters(Printer... printers) {
        addPrinters(Arrays.stream(printers).toList());
    }

    public void addPrinters(Collection<Printer> printers) {
        additionalPrinters.addAll(printers);
    }

    @Override
    public Printer create() {
        printersMap.clear();
        addPrintersToMap(additionalPrinters);

        ClassFilter classFilter = new ClassFilter(
                config.getPackagesInclude(), config.getPackagesExclude(),
                config.isInterfaces(), config.isAbstractClasses(), config.isSubClasses()
        );
        addPrinterToMap(new ClassDependenciesPrinter(classFilter, javaParser));
        addPrinterToMap(new ClassRelationsPrinter(classFilter, javaParser));

        addPrinterToMap(new ClassTypePrinter());
        addPrinterToMap(new ClassNamePrinter());
        addPrinterToMap(createClassBodyPrinter());

        if (config.isGenerics()) {
            addPrinterToMap(new ClassGenericsPrinter());
        }

        List<Printer> classPrinters = getSortedPrintersByType(PrinterType.CLASS.name());
        return new UnitPrinter(classFilter, classPrinters);
    }

    private void addPrintersToMap(Collection<Printer> printers) {
        for (Printer printer : printers) {
            addPrinterToMap(printer);
        }
    }

    private void addPrinterToMap(Printer printer) {
        printersMap.computeIfAbsent(printer.getType(), p -> new LinkedList<>())
                .add(printer);
    }

    private List<Printer> getSortedPrintersByType(String type) {
        List<Printer> printersCollection = printersMap.get(type);

        if (printersCollection == null) {
            return Collections.emptyList();
        }

        printersCollection.sort(Comparator.comparing(Printer::getPosition));
        return printersCollection;
    }

    private Printer createClassBodyPrinter() {
        List<Printer> fieldPrinters = createFieldPrinters();
        List<Printer> methodPrinters = createMethodPrinters();

        return new ClassBodyPrinter(
                config.isFields(), config.isPublicFields(), config.isPrivateFields(), config.isProtectedFields(), config.isStaticFields(),
                config.isMethods(), config.isPublicMethods(), config.isPrivateMethods(), config.isProtectedMethods(), config.isStaticMethods(), config.isAbstractMethods(),
                fieldPrinters, methodPrinters
        );
    }

    private List<Printer> createFieldPrinters() {
        addPrinterToMap(new FieldStaticModifierPrinter());
        if (config.isFieldVisibility()) {
            addPrinterToMap(new FieldVisibilityPrinter());
        }
        if (config.isFieldType()) {
            addPrinterToMap(new FieldTypePrinter());
        }
        if (config.isFieldName()) {
            addPrinterToMap(new FieldNamePrinter());
        }
        return getSortedPrintersByType(PrinterType.FIELD.name());
    }

    private List<Printer> createMethodPrinters() {
        addPrinterToMap(new MethodModifierPrinter());
        if (config.isMethodVisibility()) {
            addPrinterToMap(new MethodVisibilityPrinter());
        }
        if (config.isMethodType()) {
            addPrinterToMap(new MethodTypePrinter());
        }
        if (config.isMethodName()) {
            addPrinterToMap(new MethodNamePrinter());
        }

        List<Printer> methodParameterPrinters = createMethodParameterPrinters();
        addPrinterToMap(new MethodArgumentsPrinter(methodParameterPrinters));

        return getSortedPrintersByType(PrinterType.METHOD.name());
    }

    private List<Printer> createMethodParameterPrinters() {
        if (config.isMethodArgs()) {
            if (config.isMethodArgsType()) {
                addPrinterToMap(new ParameterTypePrinter());
            }

            if (config.isMethodArgsName()) {
                addPrinterToMap(new ParameterNamePrinter());
            }
        }

        return getSortedPrintersByType(PrinterType.METHOD_PARAMETER.name());
    }
}
