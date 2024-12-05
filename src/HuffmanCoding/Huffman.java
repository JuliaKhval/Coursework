package HuffmanCoding;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.ByteArrayOutputStream;

public class Huffman {

    private static Map<Byte, String> huffmanCode = new HashMap<>();
    private static Node root;

    public static void encode(Node root, String str, Map<Byte, String> huffmanCode) {
        if (root == null)
            return;

        if (root.left == null && root.right == null) {
            huffmanCode.put(root.data, str);
        }

        encode(root.left, str + "0", huffmanCode);
        encode(root.right, str + "1", huffmanCode);
    }

    public static int decode(Node root, int index, StringBuilder sb, ByteArrayOutputStream out) {
        if (root == null)
            return index;

        if (root.left == null && root.right == null) {
            out.write(root.data);
            return index;
        }

        index++;

        if (sb.charAt(index) == '0')
            index = decode(root.left, index, sb, out);
        else
            index = decode(root.right, index, sb, out);

        return index;
    }

    public static void buildHuffmanTree(byte[] data) {
        Map<Byte, Integer> freq = new HashMap<>();
        for (byte b : data) {
            freq.put(b, freq.getOrDefault(b, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((l, r) -> l.freq - r.freq);

        for (Map.Entry<Byte, Integer> entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() != 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.freq + right.freq;
            pq.add(new Node((byte) '\0', sum, left, right));
        }

        root = pq.peek();
        encode(root, "", huffmanCode);
    }

    public static String encodeData(byte[] data) {
        StringBuilder encoded = new StringBuilder();
        for (byte b : data) {
            encoded.append(huffmanCode.get(b));
        }
        return encoded.toString();
    }

    public static byte[] decodeData(String encodedData) {
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        int index = -1;
        while (index < encodedData.length() - 1) {
            index = decode(root, index, new StringBuilder(encodedData), decoded);
        }
        return decoded.toByteArray();
    }

    public static void main(String[] args) {
        try {
            // Example for text
            String text = "Huffman coding is a data compression algorithm.";
            byte[] textData = text.getBytes();
            buildHuffmanTree(textData);
            String encodedText = encodeData(textData);
            byte[] decodedText = decodeData(encodedText);
            System.out.println("Original text size: " + textData.length + " bytes");
            System.out.println("Compressed text size: " + encodedText.length() / 8 + " bytes");
            System.out.println("Decoded text: " + new String(decodedText));

            // Example for images
            String imagePath = "E:\\Юлька\\IMG_20220323_204502_809.JPG";  // Update the path accordingly
            BufferedImage image = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageData = baos.toByteArray();
            buildHuffmanTree(imageData);
            String encodedImage = encodeData(imageData);
            byte[] decodedImage = decodeData(encodedImage);
            System.out.println("Original image size: " + imageData.length + " bytes");
            System.out.println("Compressed image size: " + encodedImage.length() / 8 + " bytes");
            System.out.println("Decoded image size: " + decodedImage.length + " bytes");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
