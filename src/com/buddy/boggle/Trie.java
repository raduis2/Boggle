package com.buddy.boggle;

import java.nio.file.FileSystems;
import java.nio.file.Files;

public class Trie {

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(final String word) {
        root.insert(word);
    }

    public boolean contains(final String word) {
        return root.find(word);
    }

    public boolean isValidPrefix(final String word) {
        return root.hasValidPrefix(word);
    }

    public void loadFile(final String filename) {
        long start = System.currentTimeMillis();
        try {
            Files.lines(FileSystems.getDefault().getPath(filename)).forEach(w -> insert(w));
            long stop = System.currentTimeMillis();
            System.out.println("\nLoading dictionary took " + (stop-start) + "ms.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public long numWords() {
        return root.numWords();
    }

    @Override
    public String toString() {
        return root.getAllWords().toString();
    }

}
