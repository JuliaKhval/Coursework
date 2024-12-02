package HuffmanCoding;
class Node {
    byte data;
    int freq;
    Node left = null, right = null;

    Node(byte data, int freq) {
        this.data = data;
        this.freq = freq;
    }

    public Node(byte data, int freq, Node left, Node right) {
        this.data = data;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
}