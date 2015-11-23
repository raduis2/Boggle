package com.buddy.boggle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by radu on 11/19/15.
 */
public class TrieNode {
    public static final TrieNode TERMINAL = new TrieNode();
    private static final Character EOW = '#';

    private Map<Character,TrieNode> e;

    public TrieNode() {
        e = new HashMap<Character,TrieNode>();
    }

    public Set<Character> edges() {
        return e.keySet();
    }

    public TrieNode child(Character c) {
        return e.get(c);
    }

    public void insert(final String word) {
        if (word==null) return;
        if (word.length()==0) {
            e.put(EOW, TERMINAL);
        } else {
            Character c = word.charAt(0);
            if (e.get(c) == null) {
                e.put(c, new TrieNode());
            }
            TrieNode child = e.get(c);
            child.insert(word.substring(1));
        }
    }

    public boolean find(final String word) {
        if (word == null) return false;
        if (word.isEmpty()) {
            return e.containsKey(EOW);
        }
        TrieNode suffix = e.get(word.charAt(0));
        if (suffix == null) return false;
        return suffix.find(word.substring(1));
    }

    public boolean hasValidPrefix(final String word) {
        if (word == null) return false;
        if (word.isEmpty()) return true;
        TrieNode suffix = e.get(word.charAt(0));
        if (suffix == null) return false;
        return suffix.hasValidPrefix(word.substring(1));
    }

    public Collection<String> getAllWords() {
        Collection<String> allWords = new HashSet<String>();
        for (Character c : e.keySet()) {
            if (c.charValue() == EOW) {
                allWords.add("");
            } else {
                Collection<String> suffixes = e.get(c).getAllWords();
                for (String s : suffixes) {
                    allWords.add(c+s);
                }
            }
        }
        return allWords;
    }

    public long numWords() {
        if (this==TERMINAL) return 1;
        return e.keySet().stream().mapToLong(c->e.get(c).numWords()).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TrieNode) {
            TrieNode other = (TrieNode) o;
            if (this.edges().size() != other.edges().size()) { return false; }
            for (Character c : this.edges()) {
                if (this.child(c) != other.child(c)) { return false; }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return edges().toString();
    }

    @Override
    public int hashCode() {
        int result = edges().stream()
                .mapToInt(c -> 31*c.hashCode()*child(c).toString().hashCode())
                .sum();
        return result;
    }
}
