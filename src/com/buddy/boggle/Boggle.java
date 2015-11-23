package com.buddy.boggle;

import java.util.*;

/**
 * Created by radu on 11/19/15.
 */
public class Boggle {
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
