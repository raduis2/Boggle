package com.buddy.boggle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by radu on 11/19/15.
 */
public class Main {

    private static final String FULL_DICTIONARY = "data/words.txt";
    private static final String SHORT = "data/short.txt";
    private static final String FIRST_1K = "data/medium.txt";
    private static final String FIRST_100K = "data/100k.txt";

    public static void printStats(final List<String> allWords) {
        System.out.println(String.format("Found %d words.", allWords.size()));
        Map<Integer, List<String>> buckets = allWords.stream().collect(Collectors.groupingBy(w -> w.length()));
        buckets.entrySet().stream()
                .forEach(e -> System.out.println(
                        String.format(" - %2d words of size %d: %s", e.getValue().size(), e.getKey(), e.getValue())));
    }

    public static void main(String args[]) {
        int boardSize = 20;
        if (args.length > 0) {
            int s = Integer.parseInt(args[0]);
            if (s > 0 && s <= 20) boardSize = s;
        }
        Trie trie = new Trie();
        trie.loadFile(FULL_DICTIONARY);
        System.out.println("#Words in dictionary: " + trie.numWords());
        Boggle boggle = new Boggle(boardSize, trie);
        boggle.generateBoard();
        long startTime = System.currentTimeMillis();
        List<String> allWords = boggle.findAllWords().stream().collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        System.out.println("Search took " + (endTime - startTime) + "ms.");
        printStats(allWords);
    }
}
