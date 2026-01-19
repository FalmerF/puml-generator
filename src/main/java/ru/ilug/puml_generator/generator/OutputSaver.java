package ru.ilug.puml_generator.generator;

/**
 * An interface for saving the generated PlantUML content.
 * <p>
 * This interface defines a contract for the components responsible for writing the generated PlantUML code to various targets.
 * <p>
 * Interface implementations can support different ways of saving:
 * <lu>
 * <li>Writing to a local file</li>
 * <li>Saving to the database</li>
 * <li>Output to the console or standard stream</li>
 * </lu>
 */
public interface OutputSaver {

    /**
     * Saves the generated contents of PlantUML.
     *
     * @param pumlContent the text content of the diagram in the PlantUML format
     */
    void save(String pumlContent) throws Exception;

}
