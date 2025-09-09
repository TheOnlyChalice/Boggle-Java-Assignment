import java.util.Iterator;

/**
 * Represents a graph structure for storing paths.
 */
public class Graph implements Iterable<Graph.Node> {

    /**
     * Represents a node in the graph, containing information about the XY coordinates.
     */
    public class Node {
        final byte xy;

        /**
         * Constructs a graph node with the given XY coordinates.
         *
         * @param xy The byte representation of XY coordinates.
         */
        private Node(byte xy) {
            this.xy = xy;
        }

        /**
         * Gets the XY coordinates as an integer array.
         *
         * @return An integer array representing XY coordinates.
         */
        public int[] getXY() {
            return new int[]{(xy & 0b1100) >>> 2, xy & 0b11};
        }
    }

    public final byte[] path;

    /**
     * Constructs an empty graph.
     */
    public Graph() {
        this(0);
    }

    /**
     * Constructs a graph with a specified length.
     *
     * @param length The length of the graph.
     */
    public Graph(int length) {
        path = new byte[length];
    }

    /**
     * Clones the graph and adds a new node with the specified XY coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return A new graph with the added node.
     * @throws IllegalArgumentException if the coordinates are out of bounds.
     */
    public Graph cloneAdd(int x, int y) {
        if (x > 3 || x < 0 || y > 3 || y < 0) throw new IllegalArgumentException();

        Graph cloned = new Graph(this.path.length + 1);
        System.arraycopy(this.path, 0, cloned.path, 0, path.length);

        cloned.path[this.path.length] = (byte) ((byte) (x << 2) + (byte) (y));
        return cloned;
    }

    /**
     * Gets the path array of the graph.
     *
     * @return The path array.
     */
    public byte[] getPath() {
        return path;
    }

    /**
     * Returns an iterator for traversing the graph nodes.
     *
     * @return An iterator for the graph nodes.
     */
    @Override
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {
            int pos = 0;
            byte[] path = getPath();

            @Override
            public boolean hasNext() {
                return pos < path.length;
            }

            @Override
            public Node next() {
                return new Node(path[pos++]);
            }
        };
    }
}
