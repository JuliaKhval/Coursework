import HuffmanCoding.Huffman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.List;


public class CompressionApp {

    private static File loadedFile;
    private static byte[] loadedFileData;
    private static JTextArea outputTextArea;
    private static JComboBox<String> fileTypeComboBox;
    private static JComboBox<String> algorithmComboBox;
    private static JButton loadButton;
    private static JButton compressButton;
    private static JButton decompressButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CompressionApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Compression Algorithms Comparison");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel fileTypeLabel = new JLabel("Choose File Type:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fileTypeLabel, gbc);

        fileTypeComboBox = new JComboBox<>(new String[]{"Text", "Image"});
        gbc.gridx = 1;
        panel.add(fileTypeComboBox, gbc);

        JLabel algorithmLabel = new JLabel("Choose Compression Algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(algorithmLabel, gbc);

        algorithmComboBox = new JComboBox<>(new String[]{ "Huffman", "ShannonFano", "LZW", "RLE", "DEFLATE"});
        gbc.gridx = 1;
        panel.add(algorithmComboBox, gbc);

        loadButton = new JButton("Load File");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loadButton, gbc);

        compressButton = new JButton("Compress");
        compressButton.setEnabled(false);
        gbc.gridx = 1;
        panel.add(compressButton, gbc);

        decompressButton = new JButton("Decompress");
        decompressButton.setEnabled(false);
        gbc.gridx = 2;
        panel.add(decompressButton, gbc);

        outputTextArea = new JTextArea();
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(outputTextArea), gbc);

        frame.add(panel);
        frame.setVisible(true);

        algorithmComboBox.addActionListener(e -> displayAlgorithmInfo());
        loadButton.addActionListener(e -> loadFile(frame));
        compressButton.addActionListener(e -> compressFile());
        decompressButton.addActionListener(e -> decompressFile());
    }

    private static void displayAlgorithmInfo() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        String info = "";

        switch (selectedAlgorithm) {

            case "Huffman":
                info = "Алгоритм Хаффмана сжимает данные без потерь, кодируя часто встречающиеся символы короткими битовыми последовательностями. Он особенно эффективен для текстовых файлов и файлов с повторяющимися символами.";
                break;
            case "ShannonFano":
                info = "Алгоритм Шеннона-Фано сжимает данные без потерь, кодируя часто встречающиеся символы короткими битовыми последовательностями. Он эффективен для текстовых файлов и файлов с неоднородным распределением частот символов.";
                break;
            case "LZW":
                info = "Алгоритм LZW (Lempel-Ziv-Welch) сжимает данные без потерь, используя динамически создаваемый словарь для кодирования повторяющихся подстрок. Он особенно эффективен для текстовых файлов и файлов с повторяющимися подстроками.";
                break;
            case "RLE":
                info = "Алгоритм RLE (Run-Length Encoding) сжимает данные без потерь, заменяя последовательности повторяющихся символов на один символ и количество его повторений. Он наиболее эффективен для файлов с длинными последовательностями одинаковых символов, таких как изображения с большими одноцветными областями.";
                break;
            case "DEFLATE":
                info = "Алгоритм Deflate сжимает данные без потерь, используя комбинацию алгоритмов LZ77 и Хаффмана. Он особенно эффективен для сжатия текстовых файлов, архивов (например, ZIP), и изображений (например, PNG) благодаря своей способности эффективно обрабатывать повторяющиеся строки и символы.";
                break;
        }

        outputTextArea.setText(info + "\n\n");
    }

    private static void loadFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                loadedFile = fileChooser.getSelectedFile();
                String fileType = (String) fileTypeComboBox.getSelectedItem();

                if (fileType.equals("Image")) {
                    BufferedImage image = ImageIO.read(new File(loadedFile.getPath()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    loadedFileData = baos.toByteArray();
                } else {
                    loadedFileData = Files.readAllBytes(loadedFile.toPath());
                }

                outputTextArea.append("Loaded file: " + loadedFile.getAbsolutePath() + "\n");
                outputTextArea.append("File size: " + loadedFileData.length + " bytes\n");
                compressButton.setEnabled(true);
                decompressButton.setEnabled(true);
            } catch (Exception ex) {
                outputTextArea.append("Failed to load file: " + ex.getMessage() + "\n");
            }
        }
    }

    private static byte[] bitStringToBytes(String bitString) {
        int byteLength = (bitString.length() + 7) / 8;
        byte[] byteArray = new byte[byteLength];

        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                byteArray[i / 8] = (byte) (byteArray[i / 8] | (1 << (7 - (i % 8))));
            }
        }

        return byteArray;
    }

    private static void compressFile() {
        if (loadedFileData != null) {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            try {
                int compressedData = 0;
                byte[] compressedContentByte = null;
                String compressedContent = null;
                List<Integer> compressed = null;
                Huffman huffmanCoding = new Huffman();
                // java.util.List<Match> matches = null;
                long startTime = System.currentTimeMillis();
                switch (selectedAlgorithm) {
                    case "Huffman":

                        huffmanCoding.buildHuffmanTree(loadedFileData);
                        compressedContent = huffmanCoding.encodeData(loadedFileData);
                        compressedData = bitStringToBytes(compressedContent).length;
                        break;
                    case "RLE":
                        compressedContentByte = RLE.compress(loadedFileData);
                        compressedData = compressedContentByte.length;
                        break;
                    case "LZW":
                        compressed = LZW.compress(loadedFileData);
                        compressedData = compressed.size();
                        break;
                    case "DEFLATE":
                        compressedContentByte = Deflate.compress(loadedFileData);
                        compressedData = compressedContentByte.length;
                        break;
                    case "ShannonFano":
                        Map<Byte, Integer> frequencies = new HashMap<>();
                        for (byte b : loadedFileData) {
                            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
                        }

                        List<ShannonFano.Node> nodes = new ArrayList<>();
                        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
                            nodes.add(new ShannonFano.Node(entry.getKey(), entry.getValue()));
                        }


                        while (nodes.size() > 1) {
                            Collections.sort(nodes, Comparator.comparingInt(n -> n.frequency));
                            ShannonFano.Node left = nodes.remove(0);
                            ShannonFano.Node right = nodes.remove(0);
                            nodes.add(new ShannonFano.Node(left, right));
                        }
                        ShannonFano.Node root = nodes.get(0);


                        Map<Byte, String> codes = new HashMap<>();
                        ShannonFano.buildCodes(root, "", codes);
                        compressedContentByte =  ShannonFano.compress(loadedFileData, codes);
                        compressedData = compressedContentByte.length;
                        break;

                }
             long endTime = System.currentTimeMillis();
                long duration = endTime -startTime;
                if (compressedData != 0)
                {
                    double compressionRatio = (1 - (double)compressedData/ loadedFileData.length) * 100;
                    outputTextArea.append("Compressed using " + selectedAlgorithm + ":\n");
                    outputTextArea.append("Compressed file size: " + compressedData+ " bytes\n");
                    outputTextArea.append("Compression time: " + duration + " ms\n");
                    outputTextArea.append("Compression ratio: " + String.format("%.2f", compressionRatio) + "%\n");}
            } catch (Exception ex) {
                outputTextArea.append("Compression failed: " + ex.getMessage() + "\n");
            }
        } else {
            outputTextArea.append("Please load a file\n");
        }
    }

    private static void decompressFile() {
        if (loadedFileData != null) {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            try {
                int decompressedData = 0;
                byte[]decompressedContent = null;
                Huffman huffmanCoding = new Huffman();

                switch (selectedAlgorithm) {
                    case "Huffman":

                        huffmanCoding.buildHuffmanTree(loadedFileData);
                        String compressedContent = huffmanCoding.encodeData(loadedFileData);
                        decompressedData = huffmanCoding.decodeData(compressedContent).length;
                        break;
                    case "RLE":
                        byte [] compressedContentByte = RLE.compress(loadedFileData);

                        decompressedContent = RLE.decompress(compressedContentByte);
                        decompressedData = decompressedContent.length;
                        break;
                    case "LZW":
                        List<Integer>  compressed = LZW.compress(loadedFileData);
                        byte[] decompressed = LZW.decompress(compressed);
                        decompressedData = decompressed.length;
                    case "DEFLATE":
                        compressedContentByte = Deflate.compress(loadedFileData);
                        decompressedContent = Deflate.decompress(compressedContentByte);
                        decompressedData = decompressedContent.length;

                        break;
                    case "ShannonFano":
                        Map<Byte, Integer> frequencies = new HashMap<>();
                        for (byte b : loadedFileData) {
                            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
                        }

                        List<ShannonFano.Node> nodes = new ArrayList<>();
                        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
                            nodes.add(new ShannonFano.Node(entry.getKey(), entry.getValue()));
                        }

                        while (nodes.size() > 1) {
                            Collections.sort(nodes, Comparator.comparingInt(n -> n.frequency));
                            ShannonFano.Node left = nodes.remove(0);
                            ShannonFano.Node right = nodes.remove(0);
                            nodes.add(new ShannonFano.Node(left, right));
                        }
                        ShannonFano.Node root = nodes.get(0);

                        Map<Byte, String> codes = new HashMap<>();
                        ShannonFano.buildCodes(root, "", codes);
                        compressedContentByte =  ShannonFano.compress(loadedFileData, codes);
                        decompressedContent = ShannonFano.decompress(compressedContentByte,codes);
                        decompressedData = decompressedContent.length;
                        break;

                }

                if (decompressedData != 0) {
                    outputTextArea.append("Decompressed using " + selectedAlgorithm + ":\n");
                    outputTextArea.append("Decompressed file size: " + decompressedData + " bytes\n");
                }
            } catch (Exception ex) {
                outputTextArea.append("Decompression failed: " + ex.getMessage() + "\n");
            }
        } else {
            outputTextArea.append("Please load a file first.\n");
        }
    }
}
