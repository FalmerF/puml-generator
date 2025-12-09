package ru.ilug.puml_generator.config;

import java.util.regex.Pattern;

public record PackagesConfig(Pattern[] include, Pattern[] exclude) {
}
