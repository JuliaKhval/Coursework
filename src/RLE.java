

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RLE {

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        int i = 0;

        while (i < data.length) {
            byte value = data[i];
            int count = 1;
            while (i + 1 < data.length && data[i + 1] == value) {
                count++;
                i++;
            }
            compressed.write(value);
            compressed.write(count);
            i++;
        }

        return compressed.toByteArray();
    }

    public static byte[] decompress(byte[] data) {
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();

        for (int i = 0; i < data.length; i += 2) {
            byte value = data[i];
            int count = data[i + 1];
            for (int j = 0; j < count; j++) {
                decompressed.write(value);
            }
        }

        return decompressed.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        byte[] input = "hellohellohellossssssssssssaaaaaaaaaaaaaaasssssssssssssaaaaaaaaaaaaaaaaaaaddddddddd".getBytes(); // Пример данных для сжатия

        long startTime, endTime;
        int initialMemory, compressedMemory, decompressedMemory;

        // Измеряем начальный объем данных
        int initialDataSize = input.length;

        // Измеряем время выполнения сжатия и объем данных после сжатия
        startTime = System.nanoTime();

        byte[] compressed = compress(input);
        endTime = System.nanoTime();
        int compressedDataSize = compressed.length;

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


        String imagePath = "E:\\однотон2.jpg";  // Update the path accordingly
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        int a = imageData.length;
        byte[] com = compress(imageData);
        int comDataSize = com.length;
        System.out.println("Объем данных до сжатия: " + a + " байт");
        System.out.println("Объем данных после сжатия: " + comDataSize + " байт");
        byte[] decom = decompress(com);
        int decomDataSize = decom.length;
        System.out.println("Объем данных после декомпрессии: " + decomDataSize + " байт");
    }
}
