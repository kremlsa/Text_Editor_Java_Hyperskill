package editor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {

    public static String load(String fileName) {
        Path file = Path.of(fileName);
        try {
            String body = Files.readString(file);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(String fileName, String body) {
        Path file = Path.of(fileName);
        try {
            Files.writeString(file, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
