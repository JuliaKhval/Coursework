import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShannonFano {

    private static class Node {
        byte symbol;
        int frequency;
        Node left, right;

        Node(byte symbol, int frequency) {
            this.symbol = symbol;
            this.frequency = frequency;
        }

        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.frequency = left.frequency + right.frequency;
        }
    }

    private static void buildCodes(Node node, String code, Map<Byte, String> codes) {
        if (node.left == null && node.right == null) {
            codes.put(node.symbol, code);
            return;
        }
        buildCodes(node.left, code + '0', codes);
        buildCodes(node.right, code + '1', codes);
    }

    private static byte[] compress(byte[] data, Map<Byte, String> codes) {
        StringBuilder encodedData = new StringBuilder();
        for (byte b : data) {
            encodedData.append(codes.get(b));
        }

        int byteLength = (encodedData.length() + 7) / 8;
        byte[] compressedData = new byte[byteLength];
        for (int i = 0; i < encodedData.length(); i++) {
            if (encodedData.charAt(i) == '1') {
                compressedData[i / 8] |= 1 << (7 - (i % 8));
            }
        }

        return compressedData;
    }

    private static byte[] decompress(byte[] compressedData, Map<Byte, String> codes) {
        Map<String, Byte> reversedCodes = new HashMap<>();
        for (Map.Entry<Byte, String> entry : codes.entrySet()) {
            reversedCodes.put(entry.getValue(), entry.getKey());
        }

        StringBuilder bitString = new StringBuilder();
        for (byte b : compressedData) {
            bitString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        List<Byte> decompressedData = new ArrayList<>();
        StringBuilder currentCode = new StringBuilder();
        for (int i = 0; i < bitString.length(); i++) {
            currentCode.append(bitString.charAt(i));
            if (reversedCodes.containsKey(currentCode.toString())) {
                decompressedData.add(reversedCodes.get(currentCode.toString()));
                currentCode.setLength(0);
            }
        }

        byte[] result = new byte[decompressedData.size()];
        for (int i = 0; i < decompressedData.size(); i++) {
            result[i] = decompressedData.get(i);
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        byte[] input = "Generating random paragraphs can be an excellent way for writers to get their creative flow going at the beginning of the day. The writer has no idea what topic the random paragraph will be about when it appears. This forces the writer to use creativity to complete one of three common writing challenges. The writer can use the paragraph as the first one of a short story and build upon it. A second option is to use the random paragraph somewhere in a short story they create. The third option is to have the random paragraph be the ending paragraph in a short story. No matter which of these challenges is undertaken, the writer is forced to use creativity to incorporate the paragraph into their writing.".getBytes(); // Пример данных для сжатия

        long startTime, endTime;

        // Подсчет частот символов
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : input) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }

        // Создание узлов листьев
        List<Node> nodes = new ArrayList<>();
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Построение дерева Шеннона-Фано
        while (nodes.size() > 1) {
            Collections.sort(nodes, Comparator.comparingInt(n -> n.frequency));
            Node left = nodes.remove(0);
            Node right = nodes.remove(0);
            nodes.add(new Node(left, right));
        }
        Node root = nodes.get(0);

        // Построение кодов
        Map<Byte, String> codes = new HashMap<>();
        buildCodes(root, "", codes);

        // Измерение времени выполнения сжатия и объем данных после сжатия
        int initialDataSize = input.length;
        startTime = System.nanoTime();
        byte[] compressed = compress(input, codes);
        endTime = System.nanoTime();
        int compressedDataSize = compressed.length;
        System.out.println("Время сжатия: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных до сжатия: " + initialDataSize + " байт");
        System.out.println("Объем данных после сжатия: " + compressedDataSize + " байт");

        // Измерение времени выполнения декомпрессии и объем данных после декомпрессии
        startTime = System.nanoTime();
        byte[] decompressed = decompress(compressed, codes);
        endTime = System.nanoTime();
        int decompressedDataSize = decompressed.length;
        System.out.println("Время декомпрессии: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных после декомпрессии: " + decompressedDataSize + " байт");

        System.out.println("Исходные данные: " + new String(input));
        System.out.println("Данные после сжатия и распаковки: " + new String(decompressed));


        String imagePath = "E:\\однотон.jpg";  // Укажите путь к вашему изображению
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        int initialImageDataSize = imageData.length;

        // Подсчет частот символов для изображения
        frequencies.clear();
        for (byte b : imageData) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }

        // Создание узлов листьев для изображения
        nodes.clear();
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Построение дерева Шеннона-Фано для изображения
        while (nodes.size() > 1) {
            Collections.sort(nodes, Comparator.comparingInt(n -> n.frequency));
            Node left = nodes.remove(0);
            Node right = nodes.remove(0);
            nodes.add(new Node(left, right));
        }
        root = nodes.get(0);

        // Построение кодов для изображения
        codes.clear();
        buildCodes(root, "", codes);

        // Измерение времени выполнения сжатия изображения
        startTime = System.nanoTime();
        compressed = compress(imageData, codes);
        endTime = System.nanoTime();
        compressedDataSize = compressed.length;
        System.out.println("Время сжатия изображения: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных до сжатия: " + initialImageDataSize + " байт");
        System.out.println("Объем данных после сжатия: " + compressedDataSize + " байт");

        // Измерение времени выполнения декомпрессии изображения
        startTime = System.nanoTime();
        decompressed = decompress(compressed, codes);
        endTime = System.nanoTime();
        decompressedDataSize = decompressed.length;
        System.out.println("Время декомпрессии изображения: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных после декомпрессии: " + decompressedDataSize + " байт");
    }
}
