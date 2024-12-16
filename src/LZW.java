import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class LZW {
    private static final int DICTIONARY_SIZE = 256;
    private static final int MAX_DICT_SIZE = 4096;

    public static void main(String[] args) throws Exception {
        byte[] input = "aaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbccccccccccc".getBytes(); // Пример данных для сжатия

        long startTime, endTime;
        int initialDataSize = input.length;

        // Измеряем время выполнения сжатия и объем данных после сжатия
        startTime = System.nanoTime();
        List<Integer> compressed = compress(input);
        endTime = System.nanoTime();
        int compressedDataSize = compressed.size() ;

        System.out.println("Время сжатия: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных до сжатия: " + initialDataSize + " байт");
        System.out.println("Объем данных после сжатия: " + compressedDataSize + " байт");

        // Измеряем время выполнения декомпрессии и объем данных после декомпрессии
        startTime = System.nanoTime();
        byte[] decompressed = decompress(compressed);
        endTime = System.nanoTime();
        int decompressedDataSize = decompressed.length;

        System.out.println("Время декомпрессии: " + (endTime - startTime) + " нс");
        System.out.println("Объем данных после декомпрессии: " + decompressedDataSize + " байт");

        System.out.println("Исходные данные: " + new String(input));
        System.out.println("Данные после сжатия и распаковки: " + new String(decompressed));

        String imagePath = "E:\\однотон.jpg"; // Обнови путь соответственно
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        int initialImageDataSize = imageData.length;

        startTime = System.nanoTime();
        List<Integer> compressedImage = compress(imageData);
        endTime = System.nanoTime();
        int compressedImageDataSize = compressedImage.size() ;

        System.out.println("Время сжатия изображения: " + (endTime - startTime) + " нс");
        System.out.println("Объем изображения до сжатия: " + initialImageDataSize + " байт");
        System.out.println("Объем изображения после сжатия: " + compressedImageDataSize + " байт");

        startTime = System.nanoTime();
        byte[] decompressedImage = decompress(compressedImage);
        endTime = System.nanoTime();
        int decompressedImageDataSize = decompressedImage.length;

        System.out.println("Время декомпрессии изображения: " + (endTime - startTime) + " нс");
        System.out.println("Объем изображения после декомпрессии: " + decompressedImageDataSize + " байт");
    }

    public static List<Integer> compress(byte[] input) {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < DICTIONARY_SIZE; i++) {
            dictionary.put("" + (char) i, i);
        }

        String current = "";
        List<Integer> compressed = new ArrayList<>();
        for (byte b : input) {
            String next = current + (char) (b & 0xFF);
            if (dictionary.containsKey(next)) {
                current = next;
            } else {
                compressed.add(dictionary.get(current));
                if (dictionary.size() < MAX_DICT_SIZE) {
                    dictionary.put(next, dictionary.size());
                }
                current = "" + (char) (b & 0xFF);
            }
        }
        if (!current.isEmpty()) {
            compressed.add(dictionary.get(current));
        }

        return compressed;
    }

    public static byte[] decompress(List<Integer> compressed) {
        List<String> dictionary = new ArrayList<>();
        for (int i = 0; i < DICTIONARY_SIZE; i++) {
            dictionary.add("" + (char) i);
        }

        String current = "" + (char) (compressed.get(0).intValue());
        StringBuilder result = new StringBuilder(current);
        for (int i = 1; i < compressed.size(); i++) {
            int nextCode = compressed.get(i);
            String next;
            if (nextCode < dictionary.size()) {
                next = dictionary.get(nextCode);
            } else if (nextCode == dictionary.size()) {
                next = current + current.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + nextCode);
            }

            result.append(next);

            if (dictionary.size() < MAX_DICT_SIZE) {
                dictionary.add(current + next.charAt(0));
            }
            current = next;
        }

        return result.toString().getBytes();
    }
}
