package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Ex1 {
    static Map<String, Integer> countAppearance(String seq, int length) {
        Map<String, Integer> results = new HashMap<>();
        for (int i = 0; i < seq.length() - length + 1; i++) {
            var subSeq = seq.substring(i, i + length);
            if (results.containsKey(subSeq))
                continue;

            int cnt = 0;
            for (int j = i; j < seq.length() - length + 1; j++) {
                var s = seq.substring(j, j + length);

                if (s.equals(subSeq)) {
                    cnt++;
                }
            }
            if (cnt != 0) {
                System.out.println(subSeq + ": " + cnt);
                results.put(subSeq, cnt);
            }
        }
        return results;
    }

    static void createBarChart(Map<String, Integer> data, String title, String categoryAxisLabel, String valueAxisLabel, String fileName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (var entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Occurrences", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        try {
            ChartUtils.saveChartAsPNG(new File(fileName), barChart, 800, 600);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String sequence = "ATCGACGCTAACGTA";

        Map<Character, Integer> occurrenceMap = new HashMap<>();

        for (int i = 0; i < sequence.length(); i++) {
            var c = sequence.charAt(i);
            occurrenceMap.put(c, occurrenceMap.getOrDefault(c, 0) + 1);
        }

        Map<String, Integer> charData = new HashMap<>();
        for (var entry : occurrenceMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            charData.put(entry.getKey().toString(), entry.getValue());
        }
        createBarChart(charData, "Character Occurrences", "Character", "Count", "chars.png");

        System.out.println("\nSequences of length 2:");
        Map<String, Integer> len2Data = countAppearance(sequence, 2);
        createBarChart(len2Data, "Sequences of Length 2", "Sequence", "Count", "len2.png");

        System.out.println("\nSequences of length 3:");
        Map<String, Integer> len3Data = countAppearance(sequence, 3);
        createBarChart(len3Data, "Sequences of Length 3", "Sequence", "Count", "len3.png");
    }
}
