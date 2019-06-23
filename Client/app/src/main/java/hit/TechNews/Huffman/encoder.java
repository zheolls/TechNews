package hit.TechNews.Huffman;

import java.io.*;
import java.util.PriorityQueue;

public class encoder {

    private huffmannode root;/*霍夫曼树根节点*/
    private int[] count = new int[256];/*存储字节频率*/
    private String[] code = new String[256];/*存储字节编码*/
    /**
     * 元素索引比值大128
     */
    private PriorityQueue<huffmannode> queue = new PriorityQueue<huffmannode>(); /*优先队列   用于生成树*/

    /**
     *
     *
     * @param file
     * @throws IOException
     */
    public encoder(File file) throws IOException {
        readFile(file);
        sort();
        createTree();
        encode();
    }


    public encoder(int[] count) {
        this.count = count;
        sort();
        createTree();
    }

    /**
     * 读取文件
     *
     * @param file
     * @throws IOException
     */
    private void readFile(File file) throws IOException {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
                readFile(fileList[i]);
        } else {
            DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(file), 1024));

            int size;
            while ((size = input.available()) > 0) {
                int length = (size >= 3145728) ? 3145728 : size;
                byte[] btFile = new byte[length];
                /**
                 * 大于3M分段
                 */
                input.read(btFile);
                count(btFile);
                btFile=null;
            }

            input.close();
            input = null;
        }
    }

    /**
     * 统计频次 byte为-128-127  加128后存储 后面记得-128
     */

    private void count(byte[] bs) {
        for (int i = 0; i < bs.length; i++)
            count[bs[i] + 128]++;
    }

    /**
     * Traverses count array to form a PriorityQueue.
     */
    private void sort() {
        for (int i = 0; i < count.length; i++) {
            if (count[i] != 0)
                queue.add(new huffmannode((byte) (i - 128), count[i]));
        }
    }

    /**
     * 利用队列创建哈夫曼树
     */
    private void createTree() {
        while (queue.size() > 1) {
            huffmannode node1 = queue.poll();
            huffmannode node2 = queue.poll();
            huffmannode node3 = new huffmannode();

            node3.setCount(node1.getCount() + node2.getCount());
            node3.setLeft(node1.compareTo(node2) > 0 ? node2 : node1);
            node3.setRight(node1.compareTo(node2) > 0 ? node1 : node2);
            /**
             * 按哈夫曼编码规则确定左右子树
             */
            queue.add(node3);
             /*新节点进入队列*/
        }

        root = queue.poll();
    }

    /**
     * Encode each byte of nodes in the Huffman tree.
     */
    private void encode() {
        encode(root, "");
         /*
    至根节点时，最后编码为空
     */
    }

    /**
     * 确定该节点编码
     * @param huffmanNode
     * @param codestr
     */

    private void encode(huffmannode huffmanNode, String codestr) {
        if (huffmanNode == null)
            return;
        else if (huffmanNode.getLeft() == null
                && huffmanNode.getRight() == null) {
            if (huffmanNode == root)
                code[(int) root.getBt() + 128] = "0";
            /**
             * 编码0
             */
            else
                code[(int) huffmanNode.getBt() + 128] = codestr;
            codestr = "";
        } else {
            encode(huffmanNode.getLeft(), codestr + "0");
            encode(huffmanNode.getRight(), codestr + "1");
            /*递归*/
        }
    }


    public int[] getCount() {
        return count;
    }

    public String[] getCode() {
        return code;
    }

    public huffmannode getRoot() {
        return root;
    }

}
