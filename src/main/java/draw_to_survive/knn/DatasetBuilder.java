package draw_to_survive.knn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DatasetBuilder {

    private static final Set<String> ALLOWED_LABELS = new HashSet<>(
            Arrays.asList("vertical_line", "v", "circle", "zigzag")
    );

    private DatasetBuilder() {
    }

    public static ArrayList<DataPoint> loadDataset(String rootPath) throws Exception {
        ArrayList<DataPoint> data = new ArrayList<>();

        File root = new File(rootPath);

        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("Dataset path not found: " + root.getAbsolutePath());
        }

        File[] labelFolders = root.listFiles();
        if (labelFolders == null) {
            return data;
        }

        for (File labelFolder : labelFolders) {
            if (!labelFolder.isDirectory()) {
                continue;
            }

            String label = labelFolder.getName();

            if (!ALLOWED_LABELS.contains(label)) {
                System.out.println("Skipping unknown folder: " + label);
                continue;
            }

            File[] imageFiles = labelFolder.listFiles();
            if (imageFiles == null) {
                continue;
            }

            int count = 0;

            for (File imageFile : imageFiles) {
                if (!imageFile.isFile()) {
                    continue;
                }

                String lowerName = imageFile.getName().toLowerCase();
                if (!(lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"))) {
                    continue;
                }

                BufferedImage img = ImageIO.read(imageFile);
                if (img == null) {
                    System.out.println("Skipping unreadable image: " + imageFile.getAbsolutePath());
                    continue;
                }

                double[] features = ImageUtil.datasetCellToVector(img);
                data.add(new DataPoint(features, label));
                count++;
            }

            System.out.println("Loaded " + count + " images for class: " + label);
        }

        return data;
    }
}