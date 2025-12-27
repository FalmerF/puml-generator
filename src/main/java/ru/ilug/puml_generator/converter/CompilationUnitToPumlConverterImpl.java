package ru.ilug.puml_generator.converter;

import com.github.javaparser.ast.CompilationUnit;
import lombok.AllArgsConstructor;
import ru.ilug.puml_generator.controller.CompilationUnitToPumlConverter;

import java.util.List;

@AllArgsConstructor
public class CompilationUnitToPumlConverterImpl implements CompilationUnitToPumlConverter {

    private final JavaUnitParser parser;

    @Override
    public String convert(List<CompilationUnit> units) {
        StringBuilder builder = new StringBuilder("skinparam linetype ortho\n!pragma useIntermediatePackages false\n");

        for(CompilationUnit unit : units) {
            String parsedUnit = parser.parse(unit);
            builder.append(parsedUnit);
        }

        return builder.toString();
    }

}
