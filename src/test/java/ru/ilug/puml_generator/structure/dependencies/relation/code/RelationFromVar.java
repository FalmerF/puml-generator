package ru.ilug.puml_generator.structure.dependencies.relation.code;

import ru.ilug.puml_generator.structure.dependencies.relation.ValueCreator;

public class RelationFromVar {

    public void useVarValue() {
        ValueCreator creator = new ValueCreator();
        var value = creator.createValue();
    }

}
