package ru.ilug.puml_generator.clazz.body.method;

public class MethodsGenericsTest {

    <T> T getGeneric() {
        return null;
    }

    <T extends String, B extends Float> T getTFromB(B arg1) {
        return null;
    }

}
