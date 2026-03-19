package Project3_6481328.knn;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static final int TARGET_SIZE = 32;
    private static final int PADDING = 2;

    private ImageUtil() {}

    // =========================
    // DATASET CELL: white shape on black background
    // =========================
    public static double[] datasetCellToVector(BufferedImage original) {
        BufferedImage binary = thresholdWhiteOnBlack(original);
        BufferedImage cropped = cropWhiteShape(binary);
        BufferedImage fitted = fitToSquare(cropped, TARGET_SIZE, PADDING, Color.BLACK);
        return whiteOnBlackImageToVector(fitted);
    }

    // =========================
    // USER DRAWING: black shape on white background
    // =========================
    public static double[] drawingToVector(BufferedImage original) {
        BufferedImage binary = thresholdBlackOnWhite(original);
        BufferedImage inverted = invertBinaryImage(binary); // white shape on black
        BufferedImage cropped = cropWhiteShape(inverted);
        BufferedImage fitted = fitToSquare(cropped, TARGET_SIZE, PADDING, Color.BLACK);
        return whiteOnBlackImageToVector(fitted);
    }

    // Dataset: bright stroke -> white, dark bg -> black
    public static BufferedImage thresholdWhiteOnBlack(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;

                out.setRGB(x, y, gray >= 128 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }

        return out;
    }

    // Drawing: dark pen -> black, light bg -> white
    public static BufferedImage thresholdBlackOnWhite(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;

                out.setRGB(x, y, gray < 128 ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        return out;
    }

    public static BufferedImage invertBinaryImage(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y) & 0xFFFFFF;
                out.setRGB(x, y, rgb == 0xFFFFFF ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        return out;
    }

    public static BufferedImage cropWhiteShape(BufferedImage img) {
        int minX = img.getWidth();
        int minY = img.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y) & 0xFFFFFF;
                if (rgb == 0xFFFFFF) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX == -1 || maxY == -1) {
            BufferedImage empty = new BufferedImage(TARGET_SIZE, TARGET_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = empty.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, TARGET_SIZE, TARGET_SIZE);
            g.dispose();
            return empty;
        }

        int w = maxX - minX + 1;
        int h = maxY - minY + 1;

        BufferedImage cropped = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = cropped.createGraphics();
        g.drawImage(img, 0, 0, w, h, minX, minY, maxX + 1, maxY + 1, null);
        g.dispose();

        return cropped;
    }

    public static BufferedImage fitToSquare(BufferedImage img, int size, int padding, Color bgColor) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();

        g.setColor(bgColor);
        g.fillRect(0, 0, size, size);

        int available = size - 2 * padding;
        int srcW = img.getWidth();
        int srcH = img.getHeight();

        double scale = Math.min((double) available / srcW, (double) available / srcH);
        int newW = Math.max(1, (int) Math.round(srcW * scale));
        int newH = Math.max(1, (int) Math.round(srcH * scale));

        int x = (size - newW) / 2;
        int y = (size - newH) / 2;

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(img, x, y, newW, newH, null);
        g.dispose();

        return out;
    }

    public static double[] whiteOnBlackImageToVector(BufferedImage img) {
        double[] vec = new double[TARGET_SIZE * TARGET_SIZE];
        int index = 0;

        for (int y = 0; y < TARGET_SIZE; y++) {
            for (int x = 0; x < TARGET_SIZE; x++) {
                int rgb = img.getRGB(x, y) & 0xFFFFFF;
                vec[index++] = (rgb == 0xFFFFFF) ? 1.0 : 0.0;
            }
        }

        return vec;
    }

    public static BufferedImage vectorToImage(double[] vec) {
        BufferedImage img = new BufferedImage(TARGET_SIZE, TARGET_SIZE, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < TARGET_SIZE; y++) {
            for (int x = 0; x < TARGET_SIZE; x++) {
                double v = vec[y * TARGET_SIZE + x];
                img.setRGB(x, y, v > 0.5 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }

        return img;
    }

    public static void printAsciiPreview(double[] vec) {
        if (vec.length != TARGET_SIZE * TARGET_SIZE) {
            System.out.println("Invalid vector size");
            return;
        }

        for (int y = 0; y < TARGET_SIZE; y++) {
            for (int x = 0; x < TARGET_SIZE; x++) {
                double v = vec[y * TARGET_SIZE + x];
                System.out.print(v > 0.5 ? "##" : "  ");
            }
            System.out.println();
        }
    }
}