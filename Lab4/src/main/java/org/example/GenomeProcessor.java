package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenomeProcessor {

    private final Random random = new Random();

    public List<String> sampleGenome(String genome, int numSamples, int minLength, int maxLength) {
        List<String> samples = new ArrayList<>();
        if (genome == null || genome.isEmpty() || numSamples <= 0) {
            return samples;
        }

        for (int i = 0; i < numSamples; i++) {
            int length = random.nextInt(maxLength - minLength + 1) + minLength;
            if (length > genome.length()) {
                length = genome.length();
            }
            
            int maxStart = genome.length() - length;
            int start = maxStart > 0 ? random.nextInt(maxStart + 1) : 0;
            samples.add(genome.substring(start, start + length));
        }

        return samples;
    }

    public String reconstructGenome(List<String> samples, int minOverlap) {
        if (samples == null || samples.isEmpty()) {
            return "";
        }

        // De Bruijn Graph approach for 20000 reads is better.
        // But since the task says "find overlaps", I'll use an Overlap Graph approach.
        // To make it fast, we can use a Greedy approach with a better data structure.
        
        java.util.LinkedList<String> reads = new java.util.LinkedList<>(samples);
        
        // Let's use a simpler but faster greedy: 
        // 1. Pick a read.
        // 2. Find anything that overlaps with its end.
        // 3. Repeat.
        
        if (reads.isEmpty()) return "";
        
        StringBuilder reconstructed = new StringBuilder(reads.removeFirst());
        
        boolean added = true;
        while (added) {
            added = false;
            
            // Try to add to the end
            String currentEnd = reconstructed.substring(Math.max(0, reconstructed.length() - 150));
            int bestOverlap = -1;
            int bestIdx = -1;
            
            java.util.Iterator<String> it = reads.iterator();
            int idx = 0;
            while (it.hasNext()) {
                String read = it.next();
                int overlap = getOverlap(currentEnd, read);
                if (overlap >= minOverlap) {
                    if (overlap > bestOverlap) {
                        bestOverlap = overlap;
                        bestIdx = idx;
                        if (bestOverlap > 50) break; // Good enough
                    }
                }
                idx++;
            }
            
            if (bestIdx != -1) {
                String bestRead = reads.remove(bestIdx);
                reconstructed.append(bestRead.substring(bestOverlap));
                added = true;
            }
            
            // Also try to add to the beginning
            if (!added) {
                String currentStart = reconstructed.substring(0, Math.min(reconstructed.length(), 150));
                it = reads.iterator();
                idx = 0;
                while (it.hasNext()) {
                    String read = it.next();
                    int overlap = getOverlap(read, currentStart);
                    if (overlap >= minOverlap) {
                        if (overlap > bestOverlap) {
                            bestOverlap = overlap;
                            bestIdx = idx;
                            if (bestOverlap > 50) break;
                        }
                    }
                    idx++;
                }
                if (bestIdx != -1) {
                    String bestRead = reads.remove(bestIdx);
                    reconstructed.insert(0, bestRead.substring(0, bestRead.length() - bestOverlap));
                    added = true;
                }
            }
            
            if (reads.size() % 1000 == 0) {
                System.out.println("Remaining reads: " + reads.size() + ", Current length: " + reconstructed.length());
            }
        }

        return reconstructed.toString();
    }

    private int getOverlap(String s1, String s2) {
        int maxLen = Math.min(s1.length(), s2.length());
        for (int len = maxLen; len >= 1; len--) {
            if (s1.endsWith(s2.substring(0, len))) {
                return len;
            }
        }
        return 0;
    }
}
