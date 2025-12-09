package ru.ilug.puml_generator.parser.printer;

import java.util.HashSet;
import java.util.Set;

public class PrinterProperties {

    private final Set<Object> propertiesSet = new HashSet<>();

    public PrinterProperties(Object... args) {
        put(args);
    }

    public void put(Object... args) {
        for (Object object : args) {
            put(object);
        }
    }

    public void put(Object object) {
        propertiesSet.add(object);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        for (Object object : propertiesSet) {
            if (key.isAssignableFrom(object.getClass())) {
                return (T) object;
            }
        }

        return null;
    }

    @Override
    public PrinterProperties clone() {
        return new PrinterProperties(propertiesSet);
    }

}
