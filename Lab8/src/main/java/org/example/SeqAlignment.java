package org.example;

import java.util.ArrayList;
import java.util.List;

public class SeqAlignment {
    private static final int MATCH = 1;
    private static final int MISMATCH = -1;
    private static final int GAP = -1;

    private final String s1;
    private final String s2;
    private final int[][] matrix;

    public SeqAlignment(String s1, String s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.matrix = new int[s1.length() + 1][s2.length() + 1];
    }

    private int getHighestScore(int i, int j) {
        int score = s1.charAt(i - 1) == s2.charAt(j - 1) ? MATCH : MISMATCH;
        int diagonal = matrix[i - 1][j - 1] + score;
        int top = matrix[i - 1][j] + GAP;
        int left = matrix[i][j - 1] + GAP;
        return Math.max(diagonal, Math.max(top, left));
    }

    public void fillMatrix() {
        for (int i = 0; i <= s1.length(); i++) matrix[i][0] = i * GAP;
        for (int j = 0; j <= s2.length(); j++) matrix[0][j] = j * GAP;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                matrix[i][j] = getHighestScore(i, j);
            }
        }
    }

    public int getScore() {
        return matrix[s1.length()][s2.length()];
    }

    public List<String[]> getAllAlignments() {
        List<String[]> result = new ArrayList<>();
        traceback(s1.length(), s2.length(), new StringBuilder(), new StringBuilder(), result);
        return result;
    }

    private void traceback(int i, int j, StringBuilder a, StringBuilder b, List<String[]> result) {
        if (i == 0 && j == 0) {
            result.add(new String[]{
                    new StringBuilder(a).reverse().toString(),
                    new StringBuilder(b).reverse().toString()
            });
            return;
        }

        if (i > 0 && j > 0) {
            int score = s1.charAt(i - 1) == s2.charAt(j - 1) ? MATCH : MISMATCH;
            if (matrix[i][j] == matrix[i - 1][j - 1] + score) {
                a.append(s1.charAt(i - 1));
                b.append(s2.charAt(j - 1));
                traceback(i - 1, j - 1, a, b, result);
                a.deleteCharAt(a.length() - 1);
                b.deleteCharAt(b.length() - 1);
            }
        }

        if (i > 0 && matrix[i][j] == matrix[i - 1][j] + GAP) {
            a.append(s1.charAt(i - 1));
            b.append('-');
            traceback(i - 1, j, a, b, result);
            a.deleteCharAt(a.length() - 1);
            b.deleteCharAt(b.length() - 1);
        }

        if (j > 0 && matrix[i][j] == matrix[i][j - 1] + GAP) {
            a.append('-');
            b.append(s2.charAt(j - 1));
            traceback(i, j - 1, a, b, result);
            a.deleteCharAt(a.length() - 1);
            b.deleteCharAt(b.length() - 1);
        }
    }

    public void printAlignments() {
        List<String[]> alignments = getAllAlignments();
        System.out.println(alignments.size() + " optimal alignment(s), score " + getScore() + ":");
        for (String[] al : alignments) {
            System.out.println();
            System.out.println(al[0]);
            System.out.println(al[1]);
        }
    }

    public void printMatrix() {
        System.out.print("        ");
        for (char c : s2.toCharArray()) System.out.printf("%4c", c);
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("%4c", i == 0 ? '-' : s1.charAt(i - 1));
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%4d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SeqAlignment sa = new SeqAlignment("ACCGTGAAGCCAATAC", "AGCGTGCAGCCAATAC");
        sa.fillMatrix();
        sa.printMatrix();
        System.out.println("Score: " + sa.getScore());
        System.out.println();
        sa.printAlignments();
    }
}