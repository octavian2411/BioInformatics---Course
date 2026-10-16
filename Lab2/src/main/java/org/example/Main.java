package org.example;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<String, String> CODON_MAP = new HashMap<>();
    static {
        // Phenylalanine (Phe)
        CODON_MAP.put("UUU", "Phe");
        CODON_MAP.put("UUC", "Phe");

        // Leucine (Leu)
        CODON_MAP.put("UUA", "Leu");
        CODON_MAP.put("UUG", "Leu");
        CODON_MAP.put("CUU", "Leu");
        CODON_MAP.put("CUC", "Leu");
        CODON_MAP.put("CUA", "Leu");
        CODON_MAP.put("CUG", "Leu");

        // Isoleucine (Ile)
        CODON_MAP.put("AUU", "Ile");
        CODON_MAP.put("AUC", "Ile");
        CODON_MAP.put("AUA", "Ile");

        // Methionine (Met)
        CODON_MAP.put("AUG", "Met");

        // Valine (Val)
        CODON_MAP.put("GUU", "Val");
        CODON_MAP.put("GUC", "Val");
        CODON_MAP.put("GUA", "Val");
        CODON_MAP.put("GUG", "Val");

        // Serine (Ser)
        CODON_MAP.put("UCU", "Ser");
        CODON_MAP.put("UCC", "Ser");
        CODON_MAP.put("UCA", "Ser");
        CODON_MAP.put("UCG", "Ser");
        CODON_MAP.put("AGU", "Ser");
        CODON_MAP.put("AGC", "Ser");

        // Proline (Pro)
        CODON_MAP.put("CCU", "Pro");
        CODON_MAP.put("CCC", "Pro");
        CODON_MAP.put("CCA", "Pro");
        CODON_MAP.put("CCG", "Pro");

        // Threonine (Thr)
        CODON_MAP.put("ACU", "Thr");
        CODON_MAP.put("ACC", "Thr");
        CODON_MAP.put("ACA", "Thr");
        CODON_MAP.put("ACG", "Thr");

        // Alanine (Ala)
        CODON_MAP.put("GCU", "Ala");
        CODON_MAP.put("GCC", "Ala");
        CODON_MAP.put("GCA", "Ala");
        CODON_MAP.put("GCG", "Ala");

        // Tyrosine (Tyr)
        CODON_MAP.put("UAU", "Tyr");
        CODON_MAP.put("UAC", "Tyr");

        // Histidine (His)
        CODON_MAP.put("CAU", "His");
        CODON_MAP.put("CAC", "His");

        // Glutamine (Gln)
        CODON_MAP.put("CAA", "Gln");
        CODON_MAP.put("CAG", "Gln");

        // Asparagine (Asn)
        CODON_MAP.put("AAU", "Asn");
        CODON_MAP.put("AAC", "Asn");

        // Lysine (Lys)
        CODON_MAP.put("AAA", "Lys");
        CODON_MAP.put("AAG", "Lys");

        // Aspartic acid (Asp)
        CODON_MAP.put("GAU", "Asp");
        CODON_MAP.put("GAC", "Asp");

        // Glutamic acid (Glu)
        CODON_MAP.put("GAA", "Glu");
        CODON_MAP.put("GAG", "Glu");

        // Cysteine (Cys)
        CODON_MAP.put("UGU", "Cys");
        CODON_MAP.put("UGC", "Cys");

        // Tryptophan (Trp)
        CODON_MAP.put("UGG", "Trp");

        // Arginine (Arg)
        CODON_MAP.put("CGU", "Arg");
        CODON_MAP.put("CGC", "Arg");
        CODON_MAP.put("CGA", "Arg");
        CODON_MAP.put("CGG", "Arg");
        CODON_MAP.put("AGA", "Arg");
        CODON_MAP.put("AGG", "Arg");

        // Glycine (Gly)
        CODON_MAP.put("GGU", "Gly");
        CODON_MAP.put("GGC", "Gly");
        CODON_MAP.put("GGA", "Gly");
        CODON_MAP.put("GGG", "Gly");

        // Stop codons
        CODON_MAP.put("UAA", "Stop");
        CODON_MAP.put("UAG", "Stop");
        CODON_MAP.put("UGA", "Stop");
    }


    static void main() {
        String mrna = "CCCAUGGCUUUCAAGAACCCGAUGGGUACCAUGAAAGGCUAA";
        RNATranslator translator = new RNATranslator(CODON_MAP);
        System.out.println(translator.translate(mrna));
    }
}
