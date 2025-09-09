/**
 * Represents a Trie data structure for storing and validating words.
 */
public class DictionaryTrie {
    /**
     * The root node of the Trie.
     */
    public DNode root;

    /**
     * Inserts a word into the Trie.
     *
     * @param wordRaw The word to insert.
     * @return True if the word is inserted successfully, false otherwise.
     */
    public boolean insertWord(final String wordRaw) {
        final String word = wordRaw.toLowerCase();

        if (!(word.matches("^[a-z]*$") && !word.matches(".*q[^u].*"))) {
            return false;
        }

        char[] wordArr = word.toCharArray();
        DNode finger = root;

        for (int i = 0; i < wordArr.length; i++) {

            if (wordArr[i] == 'u' && i > 0 && wordArr[i - 1] == 'q') {
                continue;
            }

            finger = finger.getChild(wordArr[i]);
        }

        finger.character = Character.toUpperCase(finger.character);
        return true;
    }

    /**
     * Checks if a word is valid in the Trie.
     *
     * @param wordRaw The word to check for validity.
     * @return True if the word is valid, false otherwise.
     */
    public boolean isValid(final String wordRaw) {
        final String word = wordRaw.toLowerCase();

        if (!(word.matches("^[a-z]*$") && !word.matches(".*q[^u].*"))) {
            return false;
        }

        char[] wordArr = word.toCharArray();
        DNode finger = root;

        for (int i = 0; i < wordArr.length; i++) {
            if (wordArr[i] == 'u' && i > 0 && wordArr[i - 1] == 'q') {
                continue;
            }

            if (!finger.hasChild(wordArr[i])) {
                return false;
            }

            finger = finger.getChild(wordArr[i]);
        }
        ;
        return Character.isUpperCase(finger.character);
    }

    /**
     * Constructs a DictionaryTrie with an empty root node.
     */
    public DictionaryTrie() {
        root = new DNode('\0');
    }

    /**
     * Represents a node in the Trie.
     */
    public class DNode {

        /**
         * The character associated with the node.
         */
        public char character;

        private ChildHashSet children;

        /**
         * Constructs a Trie node with the given character.
         *
         * @param c The character for the node.
         */
        private DNode(final char c) {
            character = c;
            children = new ChildHashSet();
        }

        /**
         * Gets the child node associated with the given character.
         *
         * @param c The character to find in the children.
         * @return The child node if found, null otherwise.
         */
        public DNode getChild(char c) {
            int index = Character.toLowerCase(c) - 'a';

            if (index < 0 || index > 25) throw new IllegalArgumentException();

            DNode child = children.hasChild(c);
            if (child == null) {
                child = children.putChild(c);
            }
            return child;
        }

        /**
         * Checks if the node has a child with the given character.
         *
         * @param c The character to check in the children.
         * @return True if the node has a child with the given character, false otherwise.
         */
        public boolean hasChild(char c) {
            int index = Character.toLowerCase(c) - 'a';
            if (index < 0 || index > 25) throw new IllegalArgumentException();
            return children.hasChild(c) != null ? true : false;
        }

        /**
         * Checks if the node is a leaf node.
         *
         * @return True if the node is a leaf node, false otherwise.
         */
        public boolean isLeaf() {
            return Character.isUpperCase(character);
        }

        /**
         * Represents a set of children nodes in the Trie.
         */
        private class ChildHashSet {
            DNode[] children;

            /**
             * Constructs an empty set of children nodes.
             */
            public ChildHashSet() {
                children = new DNode[0];
            }

            /**
             * Gets the child node with the given character.
             *
             * @param c The character to find in the children.
             * @return The child node if found, null otherwise.
             */
            DNode hasChild(char c) {
                if (children.length == 0) {
                    return null;
                }
                int index = Character.toLowerCase(c) - 'a';
                int searchPosStart = index % children.length;
                for (int i = 0; i < children.length; i++) {
                    if (children[(i + searchPosStart) % children.length] == null) {
                        return null;
                    }
                    if (Character.toLowerCase(children[(i + searchPosStart) % children.length].character) == Character.toLowerCase(c)) {
                        return children[(i + searchPosStart) % children.length];
                    }
                }
                return null;
            }

            /**
             * Adds a child node with the given character.
             *
             * @param c The character for the new child node.
             * @return The new child node.
             */
            DNode putChild(char c) {
                DNode child = hasChild(c);
                if (child != null) return child;
                DNode[] newChildren = new DNode[children.length + 1];

                int index = Character.toLowerCase(c) - 'a';
                int searchPosStart = index % newChildren.length;

                for (DNode childi : children) {

                    int tryIndex = searchPosStart % newChildren.length;

                    while (newChildren[tryIndex] != null) {
                        tryIndex = (tryIndex + 1) % newChildren.length;

                    }

                    newChildren[tryIndex] = childi;
                }

                int tryIndex = searchPosStart % newChildren.length;

                while (newChildren[tryIndex] != null) {
                    tryIndex = (tryIndex + 1) % newChildren.length;
                }

                newChildren[tryIndex] = new DNode(c);

                children = newChildren;
                return children[tryIndex];
            }

        }
    }

}