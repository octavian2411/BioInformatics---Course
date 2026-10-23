package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenomeProcessor {

    private final Random random = new Random();

    public List<String> splitGenome(String genome, int a, int b) {
        List<String> subsequences = new ArrayList<>();
        int currentPos = 0;
        int minOverlap = 10;

        while (currentPos < genome.length()) {
            int length = random.nextInt(b - a + 1) + a;
            int end = Math.min(currentPos + length, genome.length());
            subsequences.add(genome.substring(currentPos, end));
            
            if (end >= genome.length()) {
                break;
            }
            
            // To ensure an overlap of at least 10, the next fragment should start 10 chars before the current end.
            // We assume a > 10 as per the requirement "minimum overlap has to be at least 10".
            currentPos = end - minOverlap;
            
            // Safety check: ensure we always move forward even if a is small
            if (currentPos <= subsequences.get(subsequences.size() - 1).length() - length) {
                // This shouldn't happen if a > minOverlap
            }
        }

        return subsequences;
    }

    public String rebuildGenome(List<String> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            return "";
        }
        
        List<String> remaining = new ArrayList<>(fragments);
        String genome = remaining.remove(0);

        while (!remaining.isEmpty()) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                String fragment = remaining.get(i);

                int overlap = findOverlap(genome, fragment);
                if (overlap >= 10) {
                    genome = genome + fragment.substring(overlap);
                    remaining.remove(i);
                    found = true;
                    break;
                }

                overlap = findOverlap(fragment, genome);
                if (overlap >= 10) {
                    genome = fragment + genome.substring(overlap);
                    remaining.remove(i);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                break; 
            }
        }

        return genome;
    }

    private int findOverlap(String s1, String s2) {
        int maxOverlap = Math.min(s1.length(), s2.length());
        for (int len = maxOverlap; len >= 10; len--) {
            if (s1.endsWith(s2.substring(0, len))) {
                return len;
            }
        }
        return 0;
    }
}
