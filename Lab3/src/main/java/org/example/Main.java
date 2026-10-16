package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Map<String, String> DNA_CODON_MAP = new HashMap<>();

    static {
        // Phenylalanine (Phe)
        DNA_CODON_MAP.put("TTT", "Phe");
        DNA_CODON_MAP.put("TTC", "Phe");

        // Leucine (Leu)
        DNA_CODON_MAP.put("TTA", "Leu");
        DNA_CODON_MAP.put("TTG", "Leu");
        DNA_CODON_MAP.put("CTT", "Leu");
        DNA_CODON_MAP.put("CTC", "Leu");
        DNA_CODON_MAP.put("CTA", "Leu");
        DNA_CODON_MAP.put("CTG", "Leu");

        // Isoleucine (Ile)
        DNA_CODON_MAP.put("ATT", "Ile");
        DNA_CODON_MAP.put("ATC", "Ile");
        DNA_CODON_MAP.put("ATA", "Ile");

        // Methionine (Met)
        DNA_CODON_MAP.put("ATG", "Met");

        // Valine (Val)
        DNA_CODON_MAP.put("GTT", "Val");
        DNA_CODON_MAP.put("GTC", "Val");
        DNA_CODON_MAP.put("GTA", "Val");
        DNA_CODON_MAP.put("GTG", "Val");

        // Serine (Ser)
        DNA_CODON_MAP.put("TCT", "Ser");
        DNA_CODON_MAP.put("TCC", "Ser");
        DNA_CODON_MAP.put("TCA", "Ser");
        DNA_CODON_MAP.put("TCG", "Ser");
        DNA_CODON_MAP.put("AGT", "Ser");
        DNA_CODON_MAP.put("AGC", "Ser");

        // Proline (Pro)
        DNA_CODON_MAP.put("CCT", "Pro");
        DNA_CODON_MAP.put("CCC", "Pro");
        DNA_CODON_MAP.put("CCA", "Pro");
        DNA_CODON_MAP.put("CCG", "Pro");

        // Threonine (Thr)
        DNA_CODON_MAP.put("ACT", "Thr");
        DNA_CODON_MAP.put("ACC", "Thr");
        DNA_CODON_MAP.put("ACA", "Thr");
        DNA_CODON_MAP.put("ACG", "Thr");

        // Alanine (Ala)
        DNA_CODON_MAP.put("GCT", "Ala");
        DNA_CODON_MAP.put("GCC", "Ala");
        DNA_CODON_MAP.put("GCA", "Ala");
        DNA_CODON_MAP.put("GCG", "Ala");

        // Tyrosine (Tyr)
        DNA_CODON_MAP.put("TAT", "Tyr");
        DNA_CODON_MAP.put("TAC", "Tyr");

        // Histidine (His)
        DNA_CODON_MAP.put("CAT", "His");
        DNA_CODON_MAP.put("CAC", "His");

        // Glutamine (Gln)
        DNA_CODON_MAP.put("CAA", "Gln");
        DNA_CODON_MAP.put("CAG", "Gln");

        // Asparagine (Asn)
        DNA_CODON_MAP.put("AAT", "Asn");
        DNA_CODON_MAP.put("AAC", "Asn");

        // Lysine (Lys)
        DNA_CODON_MAP.put("AAA", "Lys");
        DNA_CODON_MAP.put("AAG", "Lys");

        // Aspartic acid (Asp)
        DNA_CODON_MAP.put("GAT", "Asp");
        DNA_CODON_MAP.put("GAC", "Asp");

        // Glutamic acid (Glu)
        DNA_CODON_MAP.put("GAA", "Glu");
        DNA_CODON_MAP.put("GAG", "Glu");

        // Cysteine (Cys)
        DNA_CODON_MAP.put("TGT", "Cys");
        DNA_CODON_MAP.put("TGC", "Cys");

        // Tryptophan (Trp)
        DNA_CODON_MAP.put("TGG", "Trp");

        // Arginine (Arg)
        DNA_CODON_MAP.put("CGT", "Arg");
        DNA_CODON_MAP.put("CGC", "Arg");
        DNA_CODON_MAP.put("CGA", "Arg");
        DNA_CODON_MAP.put("CGG", "Arg");
        DNA_CODON_MAP.put("AGA", "Arg");
        DNA_CODON_MAP.put("AGG", "Arg");

        // Glycine (Gly)
        DNA_CODON_MAP.put("GGT", "Gly");
        DNA_CODON_MAP.put("GGC", "Gly");
        DNA_CODON_MAP.put("GGA", "Gly");
        DNA_CODON_MAP.put("GGG", "Gly");

        // Stop codons
        DNA_CODON_MAP.put("TAA", "Stop");
        DNA_CODON_MAP.put("TAG", "Stop");
        DNA_CODON_MAP.put("TGA", "Stop");
    }

    public static String readFasta(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    continue;
                }
                sb.append(line.trim());
            }
        }
        return sb.toString();
    }

    public static Map<String, Integer> countCodons(String sequence) {
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i <= sequence.length() - 3; i++) {
            String codon = sequence.substring(i, i + 3).toUpperCase();
            counts.put(codon, counts.getOrDefault(codon, 0) + 1);
        }
        return counts;
    }
    public static void main(String[] args) {
        String fastaPath = "src/main/resources/GCF_012271725.1_ASM1227172v1_genomic.fna";
        try {
            String sequence = readFasta(fastaPath);
            Map<String, Integer> codonCounts = countCodons(sequence);

            List<Map.Entry<String, Integer>> sortedCodons = codonCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(10)
                    .toList();

            System.out.println("Top 10 highest frequency codons:");
            for (Map.Entry<String, Integer> entry : sortedCodons) {
                String codon = entry.getKey();
                int frequency = entry.getValue();
                String aminoAcid = DNA_CODON_MAP.get(codon);
                System.out.println(codon + " (" + frequency + ") => " + aminoAcid);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
