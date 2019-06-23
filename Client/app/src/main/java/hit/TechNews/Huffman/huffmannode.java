package hit.TechNews.Huffman;

public class huffmannode implements Comparable<huffmannode> {
    private byte bt;
    private  int count;
    private  huffmannode right;
    private  huffmannode left;
    public huffmannode()
    {

    }
    public huffmannode(byte bt,int count)
    {
        this(bt, count, null, null);
    }
    public huffmannode(byte bt,int count,huffmannode right,huffmannode left)
    {
        this.bt=bt;
        this.count=count;
        this.right=right;
        this.left=left;
    }
    /**
     * @return the bt
     */
    public byte getBt() {
        return bt;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the left
     */
    public huffmannode getLeft() {
        return left;
    }

    /**
     * @param left
     *            the left to set
     */
    public void setLeft(huffmannode left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public huffmannode getRight() {
        return right;
    }

    /**
     * @param right
     *            the right to set
     */
    public void setRight(huffmannode right) {
        this.right = right;
    }

    /**
     * Compare the count of the node with another.
     *
     * @param node
     * 			the node to compared with
     */

    public int compareTo(huffmannode node) {
        return this.count - node.count;
    }


}
