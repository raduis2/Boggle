import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Merged into single file.
 */
public class Solution {

public static class TrieNode {
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

public static class Trie {

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


public static class Boggle {
    private Trie dictionary;
    private int size;
    private String[] board;

    private class Cell {
        int row;
        int col;
        public Cell(int x, int y) {
            row = x;
            col = y;
        }
        public int getRow() { return row; }
        public int getCol() { return col; }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Cell) {
                return this.row == ((Cell)o).row && this.col==((Cell)o).col;
            }
            return false;
        }
    }

    public Boggle(final int size, final Trie dictionary) {
        this.dictionary = dictionary;
        this.size = size;
        this.board = new String[size];
    }

    public Character letterAt(int row, int col) {
        if (row>=0 && row<size && col>=0 && col<size) {
            return board[row].charAt(col);
        }
        return ' ';
    }

    public Character letterAt(Cell x) {
        return board[x.getRow()].charAt(x.getCol());
    }

    public List<Cell> getAdjacent(Cell cell) {
        List<Cell> adj = new ArrayList<Cell>();
        int row = cell.getRow();
        int col = cell.getCol();
        for (int i=-1; i<=1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((!(i==row && j==col)) && row+i>=0 && row+i<size && col+j>=0 && col+j<size) {
                    adj.add(new Cell(row+i, col+j));
                }
            }
        }
        return adj;
    }

    public void generateBoard() {
        // String rc = RandomStringUtils.randomAlphabetic(size);
        String separator = String.format(String.format("  %%0%dd", size), 0).replaceAll("0", "-");
        System.out.println(String.format("Random %dx%d board:", size, size));
        System.out.println(separator);
        Random r = new Random(System.currentTimeMillis());
        for (int i=0; i<size; i++) {
            char[] row = new char[size];
            for (int j=0; j<size; j++) {
                row[j] = (char)('a' + r.nextInt(26));
            }
            board[i] = new String(row);
            String fmt = String.format("| %%-%ds |", size);
            System.out.println(String.format(fmt, board[i].toUpperCase()));
        }
        System.out.println(separator);
    }

    public String getWord(Queue<Cell> path) {
        StringBuilder sb = new StringBuilder();
        path.stream().forEach(c -> sb.append(letterAt(c)));
        return sb.toString();
    }

    public void explore(Cell cell, Deque<Cell> curPath, Collection<String> words) {
        curPath.offer(cell);
        String curWord = getWord(curPath);
        if (dictionary.isValidPrefix(curWord)) {
            if (curWord.length()> 2 && dictionary.contains(curWord)) {
                words.add(curWord);
            }
            getAdjacent(cell).stream()
                .filter(x -> !curPath.contains(x))
                .forEach(x -> explore(x, curPath, words));
        }
        curPath.removeLast();
    }

    public Collection<String> findAllWords() {
        Collection<String> words = new HashSet<String>();
        Deque<Cell> curPath = new ArrayDeque<Cell>();
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                explore(new Cell(r,c), curPath, words);
            }
        }
        return words;
    }

}

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
        int boardSize = 5;
        if (args.length > 0) {
            int s = Integer.parseInt(args[0]);
            if (s > 0 && s <= 1024) boardSize = s;
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
