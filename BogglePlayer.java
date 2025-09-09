import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BogglePlayer {
    // Data structures to represent the Boggle game state
    public static int[] dictionaryTree;     // Structure to store the dictionary tree
    public static byte[][] board;           // 4x4 Boggle board
    public static boolean[][] visited;      // Tracking visited positions during DFS
    public static byte[] traceByte, stringByte;  // Arrays to store trace information
    public static int[][] traceXY;          // 2D array to store x, y coordinates during DFS
    public static ArrayList<Word> answers;  // List to store valid words found during DFS
    public static Word[] words;             // Array to store the final list of words

    // Constructor to initialize the BogglePlayer with a word file
    public BogglePlayer(String wordFile) {
        Scanner scan;
        try {
            scan = new Scanner(new File(wordFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Build dictionary tree
        ArrayList<Boolean> isAWord = new ArrayList<Boolean>();
        ArrayList<Byte> alphabet = new ArrayList<Byte>();
        ArrayList<ArrayList<Integer>> child = new ArrayList<ArrayList<Integer>>();

        // Initialize the root node of the tree
        isAWord.add(false); // The root is not a word
        ArrayList<Integer> intArray = new ArrayList<Integer>(26);
        for (int i = 0; i < 26; i++) {
            intArray.add(0);
        }
        child.add(intArray);
        alphabet.add((byte) -1);

        // Add all valid words to the tree
        while (scan.hasNext()) {
            String str = scan.next().toUpperCase();
            if (isValidWord(str)) {
                int p = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (child.get(p).get(str.charAt(i) - 'A') == 0) {
                        newDictionaryNode(p, str.charAt(i), child, isAWord, alphabet);
                    }
                    p = child.get(p).get(str.charAt(i) - 'A');

                    if (str.charAt(i) == 'Q') {
                        i++;
                    }
                }
                isAWord.set(p, true);
            }
        }
        scan.close();

        // Calculate position map for dictionary tree
        int[] posMap = new int[isAWord.size()];
        posMap[0] = 0;
        for (int i = 1; i < posMap.length; i++) {
            int childCount = 0;
            for (int j = 0; j < child.get(i - 1).size(); j++) {
                if (child.get(i - 1).get(j) != 0) {
                    childCount++;
                }
            }
            posMap[i] = 1 + posMap[i - 1] + childCount;
        }

        // Fill in the data for the dictionary tree
        dictionaryTree = new int[posMap[isAWord.size() - 1] + 26];
        for (int i = 0; i < isAWord.size(); i++) {
            int childCount = (i == isAWord.size() - 1 ? posMap[i] + 1 : posMap[i + 1]) - posMap[i] - 1;

            dictionaryTree[posMap[i]] = childCount;

            if (childCount != 0) {
                childCount = 0; // Reused
                for (int j = 0; j < child.get(i).size(); j++) {
                    if (child.get(i).get(j) != 0) {
                        dictionaryTree[posMap[i] + (++childCount)] = dCompose(isAWord.get(child.get(i).get(j)), (byte) j, posMap[child.get(i).get(j)]);
                    }
                }
            }
        }

        // Initialize Boggle board and other arrays
        board = new byte[4][4];
        visited = new boolean[4][4];
        traceByte = new byte[20];
        stringByte = new byte[20];
        traceXY = new int[20][2];
        answers = new ArrayList<Word>();
        words = new Word[20];
    }

    // Method to create a new dictionary node
    public void newDictionaryNode(int p, char c, ArrayList<ArrayList<Integer>> child, ArrayList<Boolean> isAWord, ArrayList<Byte> alphabet) {
        child.get(p).set(c - 'A', child.size()); // Point to new node

        ArrayList<Integer> intArray = new ArrayList<Integer>(26);
        for (int i = 0; i < 26; i++) {
            intArray.add(0);
        }
        child.add(intArray); // New node

        isAWord.add(false);
        alphabet.add((byte) c);
    }

    // Method to check if a word is valid (length between 3 and 16, handling 'Q' followed by 'U')
    public boolean isValidWord(String str) {
        if (str.length() < 3 || 16 < str.length()) { // The length must be between 3 and 16
            return false;
        } else {
            for (int i = 0; i < str.length() - 1; i++) { // Check for single 'Q'
                if (str.charAt(i) == 'Q' && str.charAt(i + 1) != 'U') { // Invalid if 'Q' is not followed by 'U'
                    return false;
                }
            }
            return str.charAt(str.length() - 1) != 'Q'; // Check if the last character is 'Q'
        }
    }

    // Method to compose dictionary node data
    public int dCompose(boolean isAWord, byte alphabet, int child) {
        return ((isAWord ? 1 : 0) << 31) | (((int) alphabet) << 23) | (child);
    }

    // Method to get the alphabet byte from dictionary node data
    public byte dGetByte(int data) {
        return (byte) (data >> 23);
    }

    // Method to find valid words on the Boggle board
    public Word[] getWords(char[][] board) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                BogglePlayer.board[i][j] = (byte) board[i][j];
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                traceXY[0][0] = i;
                traceXY[0][1] = j;
                dfs(0, i, j, 0);
            }
        }

        for (int i = 0; i < words.length && i < answers.size(); i++) {
            words[i] = answers.get(i);
        }

        return words;
    }

    // Depth-first search to find valid words on the Boggle board
    public void dfs(int p, int x, int y, int depth) {
        int[][] NEXT_STEP = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        visited[x][y] = true;
        for (int[] ints : NEXT_STEP) {
            int newX = x + ints[0], newY = y + ints[1];
            int index = findIndex(p, newX, newY);

            if (index != 0) {
                traceByte[depth] = (byte) (dGetByte(dictionaryTree[p + index]) + 'A');
                traceXY[depth + 1][0] = newX;
                traceXY[depth + 1][1] = newY;

                dfs((dictionaryTree[p + index] & 0x7fffff), newX, newY, depth + 1);

                if (dictionaryTree[p + index] < 0) {
                    dictionaryTree[p + index] &= 0x7fffffff;

                    int j, k;
                    for (j = 0, k = 0; j <= depth; j++) {
                        stringByte[k++] = traceByte[j];
                        if (traceByte[j] == (byte) 'Q') {
                            stringByte[k++] = (byte) 'U';
                        }
                    }

                    Word aWord = new Word();
                    aWord.setWord(new String(stringByte, 0, k));
                    for (j = 1; j <= depth + 1; j++) {
                        aWord.addLetterRowAndCol(traceXY[j][0], traceXY[j][1]);
                    }
                    answers.add(aWord);
                }
            }
        }
        visited[x][y] = false;
    }

    // Method to find the index of a valid word on the Boggle board
    public int findIndex(int p, int x, int y) {
        if (dictionaryTree[p] != 0 && 0 <= x && x < 4 && 0 <= y && y < 4 && !visited[x][y]) {
            for (int i = 1; i <= dictionaryTree[p]; i++) {
                if (board[x][y] == dGetByte(dictionaryTree[p + i]) + 'A') {
                    return i;
                }
            }
        }
        return 0;
    }
}
