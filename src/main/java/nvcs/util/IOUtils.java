package nvcs.util;

import java.io.*;
import java.util.stream.Collectors;

public final class IOUtils {

    private IOUtils() {
    }

    public static String getFileName(String filePath) {
        return filePath.substring(
                filePath.lastIndexOf(File.separator) + 1);
    }

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
