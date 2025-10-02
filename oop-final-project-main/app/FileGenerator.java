package app;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import main.MainMenu;

public class FileGenerator {
    public static void writeReceiptToFile(Map<String, String> receipt, String filePath, String title) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("=== " + title + " ===\n\n");
            for (Map.Entry<String, String> entry : receipt.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                writer.write(String.format("%-20s: %s\n", key, value));
            }
            writer.write("\n=========================\n");
            MainMenu.errorMsg = "Receipt written to " + filePath;
        } catch (IOException e) {
            MainMenu.errorMsg = "Failed to write receipt: " + e.getMessage();
        }
    }
}
