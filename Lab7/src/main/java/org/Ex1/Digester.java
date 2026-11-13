package org.Ex1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public final class Digester {

    private Digester() {
    }

    public static List<Integer> findCutPositions(String dna, RestrictionEnzyme enzyme) {
        List<Integer> cuts = new ArrayList<>();
        int idx = dna.indexOf(enzyme.site());
        while (idx >= 0) {
            cuts.add(idx + enzyme.cutOffset());
            idx = dna.indexOf(enzyme.site(), idx + 1);
        }
        return cuts;
    }

    public static List<Fragment> fragments(int sequenceLength, Collection<Integer> cuts) {
        List<Fragment> result = new ArrayList<>();
        int previous = 0;
        for (int cut : new TreeSet<>(cuts)) {
            if (cut > 0 && cut < sequenceLength) {
                result.add(new Fragment(previous + 1, cut));
                previous = cut;
            }
        }
        result.add(new Fragment(previous + 1, sequenceLength));
        return result;
    }
}
