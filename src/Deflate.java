import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Deflate {

    public static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        deflater.end();

        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            inflater.end();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        byte[] input = "hellohellohellossssssssssssaaaaaaaaaaaaaaasssssssssssssaaaaaaaaaaaa".getBytes(); // Пример данных для сжатия

        long startTime, endTime;
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

        String imagePath = "E:\\однотон2.jpg"; // Обнови путь соответственно
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        int initialImageDataSize = imageData.length;

        startTime = System.nanoTime();
        byte[] compressedImage = compress(imageData);
        endTime = System.nanoTime();
        int compressedImageDataSize = compressedImage.length;

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
}
