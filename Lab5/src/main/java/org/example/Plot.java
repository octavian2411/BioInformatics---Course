package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Plot extends JFrame {

    public Plot(String title, List<String> samples, int maxLength) {
        super(title);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Electrophoresis diagram final positions",
                "",
                "BP",
                new XYSeriesCollection(),
                PlotOrientation.VERTICAL,
                false, true, false
        );

        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setVisible(false);

        for(var sample : samples) {
            addValueLine(plot, sample.length(), Color.BLUE);
        }


        plot.getRangeAxis().setRange(0.0, maxLength);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private void addValueLine(XYPlot plot, double value, Color color) {
        ValueMarker marker = new ValueMarker(value);

        marker.setPaint(color);
        marker.setStroke(new BasicStroke(2.0f));

        plot.addRangeMarker(marker);
    }

    static void main(String[] args) {
        String filePath = "src/main/resources/GCF_003575425.1_ASM357542v1_genomic.fna";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    sb.append(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }

        String originalGenome = sb.toString();
        System.out.println("Original Genome Length: " + originalGenome.length());
        int maxLength = 3000;
        List<String> samples = new GenomeProcessor().sampleGenome(originalGenome, 10, 100, maxLength);

        int i = 1;
        for(var sample : samples){
            System.out.println("Sample " + i + ": " + sample);
            i++;
        }

        SwingUtilities.invokeLater(() -> {
            Plot frame = new Plot("", samples, maxLength);
            frame.setSize(500, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}