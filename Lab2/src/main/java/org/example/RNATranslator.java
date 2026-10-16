package org.example;

import java.util.Map;

public class RNATranslator {
    public final Map<String, String> codonTable;

    public RNATranslator(Map<String, String> codonTable) {
        this.codonTable = codonTable;
    }

    public String translate(String sequence) {
        StringBuilder translated = new StringBuilder();
        int seqStart = sequence.indexOf("AUG");

        for (int i = seqStart; i < sequence.length() - 2; i += 3) {
            String current = sequence.substring(i, i + 3);
            if (isStop(current)) {
                return translated.toString();
            }
            if (codonTable.containsKey(current))
                translated.append(codonTable.get(current) + "-");
        }

        return translated.toString().substring(0, translated.length() - 2);
    }

    private boolean isStop(String seq){
        return codonTable.containsKey(seq) && codonTable.get(seq) == "Stop";
    }
}

