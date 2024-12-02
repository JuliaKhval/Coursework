package Another;

import HuffmanCoding.Huffman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

        algorithmComboBox = new JComboBox<>(new String[]{"Huffman (Text)", "Huffman (Image)", "LZ77", "LZW", "RLE", "Another.DEFLATE"});
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
            case "Huffman (Text)":
                info = "Huffman coding for text is a lossless data compression algorithm.";
                break;
            case "Huffman (Image)":
                info = "Huffman coding for images uses the same principles as for text but is optimized for image data.";
                break;
            case "LZ77":
                info = "LZ77 is a lossless data compression algorithm that operates on sliding window compression.";
                break;
            case "LZW":
                info = "LZW (Lempel-Ziv-Welch) is a universal lossless data compression algorithm.";
                break;
            case "RLE":
                info = "RLE (Run-Length Encoding) is a simple form of lossless data compression.";
                break;
            case "Another.DEFLATE":
                info = "DEFLATE is a lossless data compression algorithm that uses a combination of the LZ77 algorithm and Huffman coding.";
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
                //String pathFile = loadedFile.getPath();
                String fileType = (String) fileTypeComboBox.getSelectedItem();

                if (fileType.equals("Image")) {
                    BufferedImage image = ImageIO.read(new File(loadedFile.getPath()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    loadedFileData = baos.toByteArray();
                } else {
                    loadedFileData = Files.readAllBytes(loadedFile.toPath());
                }//попробовать читать немного подругому

                outputTextArea.append("Loaded file: " + loadedFile.getAbsolutePath() + "\n");
                outputTextArea.append("File size: " + loadedFileData.length + " bytes\n");
                compressButton.setEnabled(true);
                decompressButton.setEnabled(true);
            } catch (Exception ex) {
                outputTextArea.append("Failed to load file: " + ex.getMessage() + "\n");
            }
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return lastIndex == -1 ? "" : name.substring(lastIndex + 1);
    }

    private static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    private static void compressFile() {
        if (loadedFileData != null) {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            try {
                byte[] compressedData = null;
                String compressedContent = null;


                switch (selectedAlgorithm) {
                    case "Huffman (Text)":
                    case "Huffman (Image)":
                        Huffman.buildHuffmanTree(loadedFileData);
                        compressedContent = Huffman.encodeData(loadedFileData);
                        compressedData = bitStringToBytes(compressedContent);

                        break;

                }

                    if (compressedData != null) {
                    outputTextArea.append("Compressed using " + selectedAlgorithm + ":\n");
                    outputTextArea.append("Compressed file size: " + compressedData.length + " bytes\n");
                    }
            } catch (Exception ex) {
                outputTextArea.append("Compression failed: " + ex.getMessage() + "\n");
            }
        } else {
            outputTextArea.append("Please load a file first.\n");
        }
    }

    private static void decompressFile( ) {
        if (loadedFileData != null) {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            try {
                String decompressedContent = null;

                byte[] compressedData = null;
                byte [] decompressedData = null;
                //Huffman huffmanCoding = new Huffman();

                switch (selectedAlgorithm) {
                    case "Huffman (Text)":
                    case "Huffman (Image)":
                        Huffman.buildHuffmanTree(loadedFileData);
                        String compressedContent = Huffman.encodeData(loadedFileData);
                        compressedData = bitStringToBytes(compressedContent); // Use bitStringToBytes to convert bit string to bytes
                        decompressedData = Huffman.decodeData(compressedContent);
                        //decompressedData = decompressedContent.getBytes();
                        break;
                    // Your other algorithms here
                }

                //if (decompressedContent != null) {
                    outputTextArea.append("Decompressed using " + selectedAlgorithm + ":\n");
                    outputTextArea.append("Decompressed file size: " + decompressedData.length + " bytes\n");
               // }
            } catch (Exception ex) {
                outputTextArea.append("Decompression failed: " + ex.getMessage() + "\n");
            }
        } else {
            outputTextArea.append("Please load a file first.\n");
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
}
