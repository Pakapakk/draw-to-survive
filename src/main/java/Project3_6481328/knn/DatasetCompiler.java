package Project3_6481328.knn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class DatasetCompiler {

    public static void main(String[] args) {
        try {
            String inputPath = "src/main/java/Project3_6481328/dataset/symbol_dataset.png";
            String vectorOutputPath = "src/main/java/Project3_6481328/resources/dataset_vectors.txt";
//            String slicedOutputBase = "src/main/java/Project3_6481328/dataset/sliced";

            BufferedImage img = ImageIO.read(new File(inputPath));

            if (img == null) {
                throw new RuntimeException("Cannot load dataset image: " + inputPath);
            }

            int rows = 10;
            int cols = 20;

            int cellW = img.getWidth() / cols;
            int cellH = img.getHeight() / rows;

            System.out.println("Image loaded: " + img.getWidth() + "x" + img.getHeight());
            System.out.println("Cell size: " + cellW + "x" + cellH);

            File vectorFile = new File(vectorOutputPath);
            File vectorParent = vectorFile.getParentFile();
            if (vectorParent != null && !vectorParent.exists()) {
                vectorParent.mkdirs();
            }

//            File slicedBaseDir = new File(slicedOutputBase);
//            if (!slicedBaseDir.exists()) {
//                slicedBaseDir.mkdirs();
//            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(vectorFile));

            int count = 0;

            for (int r = 0; r < rows; r++) {
                String label = getLabel(r);

//                File labelDir = new File(slicedBaseDir, label);
//                if (!labelDir.exists()) {
//                    labelDir.mkdirs();
//                }

                for (int c = 0; c < cols; c++) {
                    int x = c * cellW;
                    int y = r * cellH;

                    BufferedImage cell = img.getSubimage(x, y, cellW, cellH);

//                    // Save original sliced cell
//                    String rawName = String.format("%s_r%d_c%d_raw.png", label, r, c);
//                    File rawFile = new File(labelDir, rawName);
//                    ImageIO.write(cell, "png", rawFile);
//
//                    // Preprocess and save processed 32x32 image too
//                    BufferedImage processed = ImageUtil.vectorToImage(
//                            ImageUtil.datasetCellToVector(cell)
//                    );
//
//                    String processedName = String.format("%s_r%d_c%d_processed.png", label, r, c);
//                    File processedFile = new File(labelDir, processedName);
//                    ImageIO.write(processed, "png", processedFile);

                    // Save vector line
                    double[] vec = ImageUtil.datasetCellToVector(cell);

                    writer.write(label + "|");
                    for (int i = 0; i < vec.length; i++) {
                        writer.write(Integer.toString((int) vec[i]));
                        if (i < vec.length - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();

                    count++;
                }
            }

            writer.close();

            System.out.println("DONE");
            System.out.println("Total samples: " + count);
            System.out.println("Saved vectors to: " + vectorOutputPath);
//            System.out.println("Saved sliced images to: " + slicedOutputBase);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLabel(int row) {
        if (row <= 1) return "vertical_line";
        if (row <= 3) return "v";
        if (row <= 5) return "circle";
        return "z";
    }
}