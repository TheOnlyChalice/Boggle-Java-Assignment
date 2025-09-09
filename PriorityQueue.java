/**
 * Priority queue implementation for storing words with their paths and priorities.
 */
public class PriorityQueue {

    /**
     * Node structure for the priority queue, representing a word with its priority and path.
     */
    class PQNode implements Comparable<PQNode> {
        String name;
        Graph path;
        int priority;

        /**
         * Constructs a PQNode with the given word, priority, and path.
         *
         * @param name     The word.
         * @param priority Points associated with the word.
         * @param path     The row and column location of each character in the word.
         */
        public PQNode(final String name, final int priority, final Graph path) {
            this.name = name;
            this.priority = priority;
            this.path = path;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(PQNode that) {
            return this.priority - that.priority;
        }
    }

    private PQNode[] data;
    private int length, maxLength;

    /**
     * Returns the current length of the priority queue.
     *
     * @return The current length.
     */
    protected int getLength() {
        return length;
    }

    /**
     * Returns the maximum length of the priority queue.
     *
     * @return The maximum length.
     */
    protected int getMaxLength() {
        return maxLength;
    }

    /**
     * Constructs a PriorityQueue with the specified maximum length.
     *
     * @param length The maximum length of the priority queue.
     */
    public PriorityQueue(final int length) {
        data = new PQNode[length];
        this.maxLength = length;
        this.length = 0;
    }

    /**
     * Inserts a word (with its path) into the priority queue based on priority points.
     *
     * @param name     The word to insert.
     * @param priority Points associated with the word.
     * @param path     The row and column location of each character in the word.
     */
    public void insert(final String name, final int priority, final Graph path) {
        if (!(length < maxLength)) {
            return;
        }

        int i = length;
        data[length++] = new PQNode(name, priority, path);

        // Heapify the data array based on priority.
        while (i != 0 && data[parentIndex(i)].compareTo(data[i]) > 0) {
            PQNode temp = data[i];
            data[i] = data[parentIndex(i)];
            data[parentIndex(i)] = temp;

            i = parentIndex(i);
        }
    }

    /**
     * Gets the index of the parent node of the specified index.
     *
     * @param i The index of the node.
     * @return The index of the parent node.
     */
    public static int parentIndex(int i) {
        return (i - 1) / 2;
    }

    /**
     * Gets the index of the left child node of the specified index.
     *
     * @param i The index of the node.
     * @return The index of the left child node.
     */
    public static int leftChildIndex(int i) {
        return (i * 2) + 1;
    }

    /**
     * Gets the index of the right child node of the specified index.
     *
     * @param i The index of the node.
     * @return The index of the right child node.
     */
    public static int rightChildIndex(int i) {
        return (i * 2) + 2;
    }

    /**
     * Performs heapification on the priority queue.
     */
    public void heapify() {
        heapify(0);
    }

    /**
     * Performs heapification at the specified level of the priority queue.
     *
     * @param level The level at which heapification is performed.
     */
    public void heapify(int level) {
        int leftIndex = leftChildIndex(level), rightIndex = rightChildIndex(level);
        int minIndex = level;

        // Swap element with the smaller child
        if (leftIndex < length && data[leftIndex].compareTo(data[minIndex]) < 0) {
            minIndex = leftIndex;
        }
        if (rightIndex < length && data[rightIndex].compareTo(data[minIndex]) < 0) {
            minIndex = rightIndex;
        }

        // Swap parent with smaller node if necessary
        if (level != minIndex) {
            PQNode temp = data[minIndex];
            data[minIndex] = data[level];
            data[level] = temp;

            heapify(minIndex);
        }
    }

    /**
     * Extracts the node with the minimum priority from the priority queue.
     *
     * @return The node with the minimum priority.
     */
    public PQNode extractMin() {
        if (length <= 0) {
            return null;
        }

        if (length == 1) {
            return data[--length];
        }

        PQNode onHold = data[0];
        data[0] = data[length - 1];
        data[--length] = null;

        heapify();

        return onHold;
    }

    /**
     * Returns the node with the minimum priority without removing it from the priority queue.
     *
     * @return The node with the minimum priority.
     */
    public PQNode peekMin() {
        if (length == 0) {
            return null;
        }

        return new PQNode(data[0].name, data[0].priority, data[0].path);
    }

    /**
     * Checks if the priority queue contains a specific word.
     *
     * @param word The word to check.
     * @return True if the word is in the priority queue, false otherwise.
     */
    public boolean contains(String word) {
        for (int i = 0; i < length; i++) {
            if (data[i].name.equals(word)) return true;
        }

        return false;
    }

    /**
     * Checks if the priority queue is empty.
     *
     * @return True if the priority queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return length == 0;
    }
}
