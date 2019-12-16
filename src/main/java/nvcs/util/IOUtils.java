package nvcs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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
        checkNotNull(filePath, "Null is passed as file path");

        String path = normalizePath(filePath);

        return path.substring(
                path.lastIndexOf(File.separator) + 1);
    }

    /**
     * Loads content from file located in the given {@code filePath}.
     *
     * @param filePath file location
     * @return file content
     */
    public static String loadFile(String filePath) {
        checkNotNull(filePath, "Null is passed as file path");

        File file = new File(normalizePath(filePath));

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
        checkNotNull(filePath, "Null is passed as file path");
        checkNotNull(fileContent, "Null is passed as file content");

        File file = new File(normalizePath(filePath));

        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            bufferedWriter.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + filePath);
        }
    }

    /**
     * Deletes the file with the given {@code filePath}.
     *
     * @param filePath path of file to delete
     */
    public static void deleteFile(String filePath) {
        checkNotNull(filePath, "Null os passed as file path");

        boolean deleteSuccessful = new File(normalizePath(filePath)).delete();

        if (!deleteSuccessful) {
            throw new RuntimeException("Failed to delete file");
        }
    }

    /**
     * Normalizes file path considering {@link File#separator}.
     *
     * @param path file o directory path
     *
     * @return normalized path
     */
    protected static String normalizePath(String path) {
        if (File.separator.equals("/")) {
            return path.replace("\\", "/");
        } else {
            return path.replace("/", "\\");
        }
    }
}
