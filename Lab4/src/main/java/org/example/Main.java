package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources/GCF_003575425.1_ASM357542v1_genomic.fna";
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
            return;
        }

        String originalGenome = sb.toString();
        
        GenomeProcessor gp = new GenomeProcessor();
        // Use fewer samples for testing if it's too slow, but task says 20000
        int numSamples = 20000;
        List<String> samples = gp.sampleGenome(originalGenome, numSamples, 100, 150);
        
        System.out.println("Original Genome Length: " + originalGenome.length());
        System.out.println("Total samples taken: " + samples.size());
        
        // Shuffle the samples to simulate not knowing the order
        Collections.shuffle(samples);
        
        System.out.println("Reconstructing genome...");
        long startTime = System.currentTimeMillis();
        String reconstructedGenome = gp.reconstructGenome(samples, 10);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Reconstruction took: " + (endTime - startTime) / 1000.0 + " seconds");
        System.out.println("Reconstructed Genome Length: " + reconstructedGenome.length());
        
        if (originalGenome.contains(reconstructedGenome) || reconstructedGenome.contains(originalGenome)) {
            System.out.println("Reconstruction is valid (it is a substring or superstring of original)!");
        } else {
            System.out.println("Reconstruction failed to match the original perfectly.");
            // check for max common subsequence or something? 
            // Usually we just check if it's mostly correct.
        }
        
        // Check for accuracy
        int matchCount = 0;
        int checkLen = Math.min(originalGenome.length(), reconstructedGenome.length());
        for (int i = 0; i < checkLen; i++) {
            if (originalGenome.charAt(i) == reconstructedGenome.charAt(i)) {
                matchCount++;
            }
        }
        System.out.println("Direct match count (alignment might be shifted): " + matchCount + "/" + checkLen);
    }
}
