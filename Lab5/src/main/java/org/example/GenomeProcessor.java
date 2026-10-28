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
}
