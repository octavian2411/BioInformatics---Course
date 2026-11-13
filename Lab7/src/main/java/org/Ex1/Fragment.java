package org.Ex1;

public record Fragment(int start, int end) {
    public int length() {
        return end - start + 1;
    }
}
