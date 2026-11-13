package org.Ex1;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final String RESOURCE = "GCF_003575425.1_ASM357542v1_genomic.fna";

    private static final int MAX_PRINTED = 10;

     static void main(String[] args) throws IOException {
        String resource = RESOURCE;

        List<RestrictionEnzyme> enzymes = List.of(
                RestrictionEnzyme.parse("EcoRI", "G^AATTC"),
                RestrictionEnzyme.parse("BamHI", "G^GATCC"),
                RestrictionEnzyme.parse("HindIII", "A^AGCTT"),
                RestrictionEnzyme.parse("TaqI", "T^CGA"),
                RestrictionEnzyme.parse("HaeIII", "GG^CC")
        );

        String dna = SequenceReader.readFromResources(resource);
        String header = SequenceReader.readHeader(resource);

        System.out.println("Sequence : " + (header.isEmpty() ? resource : header));
        System.out.println("Length   : " + dna.length() + " bp");
        System.out.println();

        System.out.printf("%-10s %-12s %10s %12s%n", "Enzyme", "Site", "Cleavages", "Fragments");
        System.out.println("-".repeat(47));
        for (RestrictionEnzyme e : enzymes) {
            List<Integer> cuts = Digester.findCutPositions(dna, e);
            System.out.printf("%-10s %-12s %10d %12d%n",
                    e.name(), e.notation(),
                    cuts.size(), Digester.fragments(dna.length(), cuts).size());
        }
        System.out.println();

        Path csv = Path.of("digest_results.csv");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(csv))) {
            out.println("enzyme,fragment_no,start,end,length_bp");

            for (RestrictionEnzyme e : enzymes) {
                List<Integer> cuts = Digester.findCutPositions(dna, e);
                List<Fragment> frags = Digester.fragments(dna.length(), cuts);

                System.out.println("=== " + e.name() + " (" + e.notation() + ") ===");
                System.out.println("Number of cleavages: " + cuts.size());
                System.out.println("Cleavage positions (cut after nucleotide): " + preview(cuts));
                System.out.printf("%-6s %10s %10s %10s%n", "#", "Start", "End", "Length");
                for (int i = 0; i < frags.size(); i++) {
                    Fragment f = frags.get(i);
                    if (i < MAX_PRINTED) {
                        System.out.printf("%-6d %10d %10d %10d%n", i + 1, f.start(), f.end(), f.length());
                    }
                    out.printf("%s,%d,%d,%d,%d%n", e.name(), i + 1, f.start(), f.end(), f.length());
                }
                if (frags.size() > MAX_PRINTED) {
                    System.out.println("... " + (frags.size() - MAX_PRINTED) + " more fragments in " + csv);
                }
                System.out.println();
            }
        }
        System.out.println("All fragments written to " + csv.toAbsolutePath());
    }

    private static String preview(List<Integer> cuts) {
        if (cuts.isEmpty()) {
            return "none";
        }
        if (cuts.size() <= MAX_PRINTED) {
            return cuts.toString();
        }
        return cuts.subList(0, MAX_PRINTED) + " ... (" + (cuts.size() - MAX_PRINTED) + " more)";
    }
}