package org.Ex1;

public record RestrictionEnzyme(String name, String site, int cutOffset) {

    public static RestrictionEnzyme parse(String name, String pattern) {
        int caret = pattern.indexOf('^');
        if (caret < 0) {
            throw new IllegalArgumentException("Pattern must contain '^' to mark the cut: " + pattern);
        }
        String site = pattern.replace("^", "").toUpperCase();
        if (!site.matches("[ACGT]+")) {
            throw new IllegalArgumentException("Site may only contain A, C, G, T: " + pattern);
        }
        return new RestrictionEnzyme(name, site, caret);
    }

    public String notation() {
        return site.substring(0, cutOffset) + "^" + site.substring(cutOffset);
    }
}
