package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FindRep {
    private static String originalGenome;
    static {
        String filePath = "src/main/resources/test_genome.fna";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    sb.append(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        originalGenome = sb.toString();
    }
    static void main(String[] args) {

        char[] letters = {'A', 'T', 'C', 'G'};
        for (int length = 2; length <= 4; length++) {
            findCombinations("", letters, length);
        }
    }

    private static void findCombinations(String prefix, char[] letters, int length) {
        if (length == 0) {
            findRepetitions(originalGenome, prefix, 6, 10);
            return;
        }

        for (char c : letters) {
            findCombinations(prefix + c, letters, length - 1);
        }
    }

    public static void findRepetitions(String genome, String repSeq, int minRep, int maxRep) {
        int seqLen = repSeq.length();
        int i = 0;
        while (i <= genome.length() - seqLen) {
            int count = 0;
            int start = i;
            while (i <= genome.length() - seqLen && genome.substring(i, i + seqLen).equals(repSeq)) {
                count++;
                i += seqLen;
            }

            if (count >= minRep && count <= maxRep) {
                System.out.println("Found " + count + " repetitions of " + repSeq + " at index " + start);
            }

            if (count == 0) {
                i++;
            }
        }
    }
}
