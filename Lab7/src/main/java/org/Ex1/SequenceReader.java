package org.Ex1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class SequenceReader {

    private SequenceReader() {
    }

    public static String readFromResources(String resourceName) throws IOException {
        InputStream in = SequenceReader.class.getClassLoader().getResourceAsStream(resourceName);
        if (in == null) {
            throw new IOException("Resource not found: " + resourceName
                    + " (ensure the file is in the classpath, e.g., in src/main/resources)");
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(">") || line.startsWith(";")) {
                    continue; // FASTA header / comment
                }
                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c)) {
                        sb.append(Character.toUpperCase(c));
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String readHeader(String resourceName) throws IOException {
        InputStream in = SequenceReader.class.getClassLoader().getResourceAsStream(resourceName);
        if (in == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String first = reader.readLine();
            return first != null && first.startsWith(">") ? first.substring(1).trim() : "";
        }
    }
}
