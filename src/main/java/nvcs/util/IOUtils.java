package nvcs.util;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Helper class that contains utility methods for IO.
 */
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * Extracts file name from the given {@code filePath}.
     *
     * @param filePath absolute file path
     * @return file name
     */
    public static String getFileName(String filePath) {
        return filePath.substring(
                filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * Loads content from file located in the given {@code filePath}.
     *
     * @param filePath file location
     * @return file content
     */
    public static String loadFile(String filePath) {
        File file = new File(filePath);

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            return bufferedReader.lines()
                    .collect(Collectors.joining("\n"));
        } catch (Exception ignored) {
            throw new RuntimeException("Failed to load file: " + filePath);
        }
    }

    /**
     * Saves the given {@code fileContent} into file with the given {@code filePath}.
     *
     * @param filePath    target file location
     * @param fileContent content to save
     */
    public static void saveFile(String filePath, String fileContent) {
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            bufferedWriter.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + filePath);
        }
    }
}
