package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class MotifScanner {

    static final char[] BASES = {'A', 'C', 'G', 'T'};
    static final double PSEUDOCOUNT = 1.0;
    static final double BACKGROUND = 0.25;
    static final int TOP = 10;

    static final String[] EXAMPLES = {
            "TTGACATATAT",
            "TAGACTTATGA",
            "CTGACGTAAAT",
            "TTGTCATACAA",
            "GTGACATTTAT",
            "TCGACAGATAA",
            "TTGAAATATCT"
    };

    static final String FASTA_RESOURCE = "/GCF_003575425.1_ASM357542v1_genomic.fna";
    static final String CHART_FILE = "scores.png";

    record Hit(String name, int position, String window, double score) {}

    static LinkedHashMap<String, String> readFasta(String resource) throws IOException {
        InputStream in = MotifScanner.class.getResourceAsStream(resource);
        if (in == null) throw new FileNotFoundException("Resource not found: " + resource);
        List<String> lines;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            lines = br.lines().toList();
        }
        LinkedHashMap<String, String> seqs = new LinkedHashMap<>();
        String name = null;
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith(">")) {
                if (name != null) seqs.put(name, sb.toString());
                name = line.substring(1).trim();
                sb.setLength(0);
            } else {
                sb.append(line.toUpperCase());
            }
        }
        if (name != null) seqs.put(name, sb.toString());
        return seqs;
    }

    static int index(char c) {
        switch (c) {
            case 'A': return 0;
            case 'C': return 1;
            case 'G': return 2;
            case 'T': return 3;
            default:  return -1;
        }
    }

    static int[][] countMatrix(List<String> examples, int len) {
        int[][] counts = new int[4][len];
        for (String s : examples) {
            if (s.length() != len)
                throw new IllegalArgumentException("All example sequences must have length " + len);
            for (int j = 0; j < len; j++) {
                int b = index(s.charAt(j));
                if (b >= 0) counts[b][j]++;
            }
        }
        return counts;
    }

    static double[][] weightMatrix(int[][] counts, int n) {
        int len = counts[0].length;
        double[][] pwm = new double[4][len];
        for (int b = 0; b < 4; b++)
            for (int j = 0; j < len; j++)
                pwm[b][j] = (counts[b][j] + PSEUDOCOUNT) / (n + 4 * PSEUDOCOUNT);
        return pwm;
    }

    static double[][] logMatrix(double[][] pwm) {
        int len = pwm[0].length;
        double[][] lwm = new double[4][len];
        for (int b = 0; b < 4; b++)
            for (int j = 0; j < len; j++)
                lwm[b][j] = Math.log(pwm[b][j] / BACKGROUND);
        return lwm;
    }

    static double score(String window, double[][] lwm) {
        double s = 0;
        for (int j = 0; j < window.length(); j++) {
            int b = index(window.charAt(j));
            if (b >= 0) s += lwm[b][j];
        }
        return s;
    }

    static double[] scoreAll(String seq, double[][] lwm) {
        int len = lwm[0].length;
        int count = Math.max(0, seq.length() - len + 1);
        double[] scores = new double[count];
        for (int i = 0; i < count; i++) scores[i] = score(seq.substring(i, i + len), lwm);
        return scores;
    }

    static void print(String title, int[][] m) {
        System.out.println(title);
        for (int b = 0; b < 4; b++) {
            System.out.print(BASES[b] + ":");
            for (int v : m[b]) System.out.printf("%8d", v);
            System.out.println();
        }
        System.out.println();
    }

    static void print(String title, double[][] m) {
        System.out.println(title);
        for (int b = 0; b < 4; b++) {
            System.out.print(BASES[b] + ":");
            for (double v : m[b]) System.out.printf(Locale.US, "%8.3f", v);
            System.out.println();
        }
        System.out.println();
    }

    static double niceStep(double range, int parts) {
        double raw = range / parts;
        double magnitude = Math.pow(10, Math.floor(Math.log10(raw)));
        for (double m : new double[]{1, 2, 2.5, 5, 10}) {
            if (m * magnitude >= raw) return m * magnitude;
        }
        return 10 * magnitude;
    }

    static BufferedImage drawChart(List<String> names, List<double[]> allScores, List<Hit> topHits) {
        int width = 1200, height = 500;
        int left = 70, right = 20, top = 50, bottom = 60;
        int plotW = width - left - right, plotH = height - top - bottom;

        long total = 0;
        double max = 0, min = 0;
        for (double[] s : allScores) {
            total += s.length;
            for (double v : s) {
                max = Math.max(max, v);
                min = Math.min(min, v);
            }
        }
        if (max == min) max = min + 1;
        double step = niceStep(max - min, 5);
        min = Math.floor(min / step) * step;
        max = Math.ceil(max / step) * step;
        double range = max - min;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Motif score at every position (" + names.size() + " sequence"
                + (names.size() == 1 ? "" : "s") + ", top " + topHits.size() + " hits marked)", left, 30);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();

        int yTicks = (int) Math.round(range / step);
        for (int t = 0; t <= yTicks; t++) {
            double v = min + step * t;
            int y = top + plotH - (int) Math.round((v - min) / range * plotH);
            g.setColor(new Color(230, 230, 230));
            g.drawLine(left, y, left + plotW, y);
            g.setColor(Color.GRAY);
            String label = String.format(Locale.US, "%.2f", v);
            g.drawString(label, left - 8 - fm.stringWidth(label), y + 4);
        }

        if (total > 1) {
            double xStep = Math.max(1, Math.round(niceStep(total, 8)));
            for (double p = 0; p <= total; p += xStep) {
                int x = left + (int) Math.round(p / (total - 1) * (plotW - 1));
                if (x > left + plotW) break;
                g.setColor(Color.GRAY);
                g.drawLine(x, top + plotH, x, top + plotH + 4);
                String label = String.valueOf((long) p + 1);
                g.drawString(label, x - fm.stringWidth(label) / 2, top + plotH + 18);
            }
        }

        double[] colMin = new double[plotW];
        double[] colMax = new double[plotW];
        Arrays.fill(colMin, Double.POSITIVE_INFINITY);
        Arrays.fill(colMax, Double.NEGATIVE_INFINITY);
        Map<String, Long> offsets = new HashMap<>();
        long offset = 0;
        for (int k = 0; k < allScores.size(); k++) {
            offsets.put(names.get(k), offset);
            double[] s = allScores.get(k);
            for (int i = 0; i < s.length; i++) {
                int px = total <= 1 ? 0 : (int) ((offset + i) * (plotW - 1) / (total - 1));
                colMin[px] = Math.min(colMin[px], s[i]);
                colMax[px] = Math.max(colMax[px], s[i]);
            }
            offset += s.length;
            if (k < allScores.size() - 1 && allScores.size() <= 100 && total > 1) {
                int x = left + (int) (offset * (plotW - 1) / (total - 1));
                g.setColor(new Color(200, 200, 200));
                g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{4f, 4f}, 0f));
                g.drawLine(x, top, x, top + plotH);
                g.setStroke(new BasicStroke(1f));
            }
        }

        int zeroY = top + plotH - (int) Math.round((0 - min) / range * plotH);
        g.setColor(new Color(70, 130, 180));
        int prevX = -1, prevY = -1;
        for (int px = 0; px < plotW; px++) {
            if (colMin[px] == Double.POSITIVE_INFINITY) continue;
            int x = left + px;
            int yLow = top + plotH - (int) Math.round((colMin[px] - min) / range * plotH);
            int yHigh = top + plotH - (int) Math.round((colMax[px] - min) / range * plotH);
            g.drawLine(x, yLow, x, yHigh);
            int yMid = (yLow + yHigh) / 2;
            if (prevX >= 0 && x - prevX <= 1 + plotW / Math.max(1, total)) g.drawLine(prevX, prevY, x, yMid);
            prevX = x;
            prevY = yMid;
        }

        g.setColor(Color.DARK_GRAY);
        g.drawLine(left, zeroY, left + plotW, zeroY);
        g.drawLine(left, top, left, top + plotH);

        List<Rectangle> placed = new ArrayList<>();
        for (int r = 0; r < topHits.size(); r++) {
            Hit h = topHits.get(r);
            long globalIndex = offsets.get(h.name()) + h.position() - 1;
            int x = left + (total <= 1 ? 0 : (int) (globalIndex * (plotW - 1) / (total - 1)));
            int y = top + plotH - (int) Math.round((h.score() - min) / range * plotH);
            g.setColor(new Color(230, 120, 30));
            g.fillOval(x - 4, y - 4, 8, 8);
            String label = String.valueOf(r + 1);
            Rectangle box = new Rectangle(x + 6, y - 4 - fm.getAscent(), fm.stringWidth(label), fm.getAscent());
            boolean moved = true;
            while (moved) {
                moved = false;
                for (Rectangle p : placed) {
                    if (p.intersects(box)) {
                        box.translate(0, -fm.getAscent() - 2);
                        moved = true;
                    }
                }
            }
            placed.add(box);
            g.setColor(Color.DARK_GRAY);
            g.drawString(label, box.x, box.y + box.height);
        }

        String xLabel = names.size() == 1 ? "Position in sequence"
                : "Position (sequences joined in FASTA order, dashed lines separate them)";
        g.drawString(xLabel, left + plotW / 2 - fm.stringWidth(xLabel) / 2, height - 15);
        g.rotate(-Math.PI / 2);
        String yLabel = "Score (ln)";
        g.drawString(yLabel, -(top + plotH / 2 + fm.stringWidth(yLabel) / 2), 18);
        g.dispose();
        return img;
    }

    public static void main(String[] args) throws IOException {
        List<String> examples = new ArrayList<>();
        for (String s : EXAMPLES) examples.add(s.toUpperCase());
        Map<String, String> sequences = readFasta(FASTA_RESOURCE);

        int n = examples.size();
        int len = examples.get(0).length();

        int[][] counts = countMatrix(examples, len);
        double[][] pwm = weightMatrix(counts, n);
        double[][] lwm = logMatrix(pwm);

        print("Count matrix:", counts);
        print("Positional weight matrix:", pwm);
        print("Log weight matrix:", lwm);

        List<String> names = new ArrayList<>();
        List<double[]> allScores = new ArrayList<>();
        PriorityQueue<Hit> best = new PriorityQueue<>(Comparator.comparingDouble(Hit::score));
        long windows = 0;
        for (Map.Entry<String, String> e : sequences.entrySet()) {
            String seq = e.getValue();
            double[] scores = scoreAll(seq, lwm);
            if (scores.length == 0) continue;
            names.add(e.getKey());
            allScores.add(scores);
            windows += scores.length;
            for (int i = 0; i < scores.length; i++) {
                if (best.size() < TOP || scores[i] > best.peek().score()) {
                    best.add(new Hit(e.getKey(), i + 1, seq.substring(i, i + len), scores[i]));
                    if (best.size() > TOP) best.poll();
                }
            }
        }

        List<Hit> topHits = new ArrayList<>(best);
        topHits.sort(Comparator.comparingDouble(Hit::score).reversed().thenComparingInt(Hit::position));

        System.out.println("Sequences: " + sequences.size() + "   Windows scored: " + windows);
        System.out.println("Top " + topHits.size() + " hits:");
        System.out.printf("%-5s %-30s %-10s %-" + Math.max(6, len) + "s %8s%n",
                "Rank", "Sequence", "Pos", "Window", "Score");
        for (int i = 0; i < topHits.size(); i++) {
            Hit h = topHits.get(i);
            String name = h.name().length() > 30 ? h.name().substring(0, 27) + "..." : h.name();
            System.out.printf(Locale.US, "%-5d %-30s %-10d %-" + Math.max(6, len) + "s %8.3f%n",
                    i + 1, name, h.position(), h.window(), h.score());
        }

        BufferedImage chart = drawChart(names, allScores, topHits);
        ImageIO.write(chart, "png", new File(CHART_FILE));
        System.out.println("\nChart saved to " + new File(CHART_FILE).getAbsolutePath());

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Motif scores");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JLabel(new ImageIcon(chart)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
    }
}