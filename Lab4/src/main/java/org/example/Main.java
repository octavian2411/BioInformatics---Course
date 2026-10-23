package org.example;

import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GenomeProcessor processor = new GenomeProcessor();

        String genomeToSplit = "ATGCATGCATGCATGCATGCATGCATGCATGCATGCATGC" +
                               "GCTAGCTAGCTAGCTAGCTAGCTAGCTAGCTAGCTAGCTA";
        int a = 15;
        int b = 25;
        
        System.out.println("--- Splitting Genome ---");
        System.out.println("Original Genome: " + genomeToSplit);
        List<String> fragments = processor.splitGenome(genomeToSplit, a, b);
        for (int i = 0; i < fragments.size(); i++) {
            System.out.println("Fragment " + i + ": " + fragments.get(i));
        }

        System.out.println("\n--- Rebuilding Genome from Randomly Split Fragments ---");
        
        List<String> overlapFragments = new java.util.ArrayList<>(fragments);
        
        System.out.println("Fragments to rebuild: " + overlapFragments);
        Collections.shuffle(overlapFragments);
        
        String rebuilt = processor.rebuildGenome(overlapFragments);
        System.out.println("Rebuilt result: " + rebuilt);
        
        System.out.println("Original Genome: " + genomeToSplit);

    }
}
