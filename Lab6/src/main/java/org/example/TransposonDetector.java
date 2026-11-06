package org.example;

import java.io.*;
import java.util.*;

public class TransposonDetector {

    static final String FASTA_FILE = "genome.fasta";
    static final int MIN_REPEAT = 10;
    static final int MIN_TSD = 4, MAX_TSD = 10;
    static final int MIN_LEN = 30, MAX_LEN = 300;

    static String seq;

    static class TE {
        String type;
        int start, end, repeat, tsd, score;
        TE(String type, int start, int end, int repeat, int tsd, int score) {
            this.type = type;
            this.start = start;
            this.end = end;
            this.repeat = repeat;
            this.tsd = tsd;
            this.score = score;
        }
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

        List<TE> found = detect();

        for (int k = 0; k < found.size(); k++) {
            TE t = found.get(k);
            String name = t.type.equals("DNA transposon") ? "TIR" : "LTR";
            String left  = seq.substring(t.start - 1, t.start - 1 + t.repeat);
            String right = seq.substring(t.end - t.repeat, t.end);
            String tsd   = seq.substring(t.start - 1 - t.tsd, t.start - 1);

            System.out.printf("TE%d  %s  start=%d  end=%d  length=%d%n",
                    k + 1, t.type, t.start, t.end, t.end - t.start + 1);
            System.out.println("     left  " + name + ": " + left);
            System.out.println("     right " + name + ": " + right
                    + (name.equals("TIR") ? "   (reverse complement of left)" : "   (same as left)"));
            System.out.println("     TSD      : " + tsd + " (before and after the element)");
            System.out.println("     sequence : " + seq.substring(t.start - 1, t.end) + "\n");
        }
    }

    static List<TE> detect() {
        List<TE> candidates = new ArrayList<>();
        for (int i = MIN_TSD; i < seq.length(); i++)
            for (int e = i + MIN_LEN; e <= Math.min(i + MAX_LEN, seq.length() - MIN_TSD); e++) {
                int tsd = tsdLength(i, e);
                if (tsd == 0)
                    continue;

                int tir = invertedRepeatLength(i, e);
                int ltr = directRepeatLength(i, e);
                if (tir >= MIN_REPEAT)
                    candidates.add(new TE("DNA transposon", i + 1, e, tir, tsd, 2 * tir + tsd));
                else if (ltr >= MIN_REPEAT) {
                    int bonus = (seq.startsWith("TG", i) && seq.startsWith("CA", e - 2)) ? 1 : 0;
                    candidates.add(new TE("Retrotransposon", i + 1, e, ltr, tsd, 2 * (ltr + tsd) + bonus));
                }
            }

        candidates.sort((a, b) -> b.score - a.score);
        List<TE> result = new ArrayList<>();
        for (TE c : candidates) {
            boolean duplicate = false;
            for (TE r : result)
                if (r.type.equals(c.type) && Math.abs(r.start - c.start) <= 5 && Math.abs(r.end - c.end) <= 5)
                    duplicate = true;
            if (!duplicate) result.add(c);
        }
        result.sort((a, b) -> a.start - b.start);
        return result;
    }

    static int tsdLength(int i, int e) {
        for (int t = Math.min(MAX_TSD, Math.min(i, seq.length() - e)); t >= MIN_TSD; t--)
            if (seq.regionMatches(i - t, seq, e, t)) return t;
        return 0;
    }

    static int invertedRepeatLength(int i, int e) {
        int k = 0;
        while (i + k < e - 1 - k && seq.charAt(i + k) == complement(seq.charAt(e - 1 - k)))
            k++;
        return k;
    }

    static int directRepeatLength(int i, int e) {
        for (int len = (e - i) / 2; len >= MIN_REPEAT; len--)
            if (seq.regionMatches(i, seq, e - len, len)) return len;
        return 0;
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