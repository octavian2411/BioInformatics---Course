package org.Ex2;

import com.sun.jdi.IntegerValue;

import java.io.*;
import java.util.*;


public class TransposonDetector {

    static final String FASTA_FILE = "genome.fna";
    static final int MIN_TIR = 4;
    static final int MAX_TIR = 5;
    static final int MIN_LEN = 30;
    static final int MAX_LEN = Integer.MAX_VALUE;

    static String seq;

    static class TE {
        int start, end;
        String tir;
        TE(int start, int end, String tir) {
            this.start = start;
            this.end = end;
            this.tir = tir;
        }
        int length() { return end - start + 1; }
    }

    public static void main(String[] args) throws Exception {
        InputStream in = TransposonDetector.class.getResourceAsStream("/" + FASTA_FILE);
        if (in == null) throw new FileNotFoundException(FASTA_FILE + " not found in src/main/resources");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null)
                if (!line.startsWith(">")) sb.append(line.trim().toUpperCase());
        }
        seq = sb.toString();
        System.out.println("Sequence length: " + seq.length() + " bp\n");

        List<String> options = new ArrayList<>();
        for (int len = MIN_TIR; len <= MAX_TIR; len++)
            generate("", len, options);
        System.out.println("TIR options generated: " + options.size() + "\n");

        List<TE> found = new ArrayList<>();
        for (String tir : options)
            search(tir, found);
        found.sort((a, b) -> a.start != b.start ? a.start - b.start : a.end - b.end);

        for (int k = 0; k < found.size(); k++) {
            TE t = found.get(k);
            System.out.printf("TE%d  start=%d  end=%d  length=%d%n", k + 1, t.start, t.end, t.length());
            System.out.println("     left  TIR: " + t.tir);
            System.out.println("     right TIR: " + reverseComplement(t.tir) + "   (reverse complement of left)");
            System.out.println("     sequence : " + seq.substring(t.start - 1, t.end) + "\n");
        }

        for (int a = 0; a < found.size(); a++)
            for (int b = a + 1; b < found.size() && found.get(b).start <= found.get(a).end; b++) {
                TE outer = found.get(a), inner = found.get(b);
                if (outer.start < inner.start && inner.end < outer.end)
                    System.out.printf("Nested: TE (%d-%d) inside TE (%d-%d)%n",
                            inner.start, inner.end, outer.start, outer.end);
            }

        System.out.println("\nTotal: " + found.size() + " transposon(s)");
    }

    static void generate(String prefix, int len, List<String> out) {
        if (prefix.length() == len) {
            out.add(prefix);
            return;
        }
        for (char c : "ACGT".toCharArray())
            generate(prefix + c, len, out);
    }

    static void search(String tir, List<TE> found) {
        String rc = reverseComplement(tir);
        List<Integer> lefts = positions(tir);
        List<Integer> rights = positions(rc);
        int len = tir.length();

        for (int left : lefts)
            for (int right : rights) {
                int start = left;
                int end = right + len;
                int size = end - start;
                if (size < MIN_LEN || size > MAX_LEN) continue;

                boolean longerOutside = start > 0 && end < seq.length()
                        && seq.charAt(start - 1) == complement(seq.charAt(end));
                boolean longerInside = seq.charAt(start + len) == complement(seq.charAt(end - 1 - len));
                if (longerOutside || longerInside) continue;

                found.add(new TE(start + 1, end, tir));
            }
    }

    static List<Integer> positions(String pattern) {
        List<Integer> result = new ArrayList<>();
        int idx = seq.indexOf(pattern);
        while (idx >= 0) {
            result.add(idx);
            idx = seq.indexOf(pattern, idx + 1);
        }
        return result;
    }

    static String reverseComplement(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--)
            sb.append(complement(s.charAt(i)));
        return sb.toString();
    }

    static char complement(char c) {
        return switch (c) {
            case 'A' -> 'T';
            case 'T' -> 'A';
            case 'C' -> 'G';
            case 'G' -> 'C';
            default -> 'X';
        };
    }
}