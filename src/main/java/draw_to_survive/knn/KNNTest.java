package draw_to_survive.knn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KNNTest {

    public static void main(String[] args) {
        try {
            String datasetPath = "src/main/java/magic_touch_game/resources/dataset_vectors.txt";
            String sampleImagePath = "test_input.png";

            System.out.println("=== KNN TEST START ===");

            ArrayList<DataPoint> dataset = VectorDatasetLoader.load(datasetPath);
            System.out.println("Loaded dataset size: " + dataset.size());

            if (dataset.isEmpty()) {
                System.out.println("Dataset is empty");
                return;
            }

            printClassDistribution(dataset);

            KNNModel model = new KNNModel(3);
            model.setUnknownThreshold(10.5);
            model.addAll(dataset);

            System.out.println("Model ready");
            System.out.println("k = 3");
            System.out.println("unknown threshold = " + model.getUnknownThreshold());

            System.out.println();
            System.out.println("=== SELF TEST ON STORED VECTORS ===");
            evaluateOnDataset(model, dataset);

            System.out.println();
            System.out.println("=== SINGLE IMAGE TEST ===");

            File sampleFile = new File(sampleImagePath);
            if (sampleFile.exists()) {
                BufferedImage testImg = ImageIO.read(sampleFile);
                double[] input = ImageUtil.datasetCellToVector(testImg);

                PredictionResult result = model.predictDetailed(input);

                System.out.println("Sample image: " + sampleFile.getAbsolutePath());
                System.out.println("Prediction: " + result.getLabel());
                System.out.println("Distance: " + result.getDistance());
                System.out.println("Unknown: " + result.isUnknown());

                System.out.println();
                System.out.println("ASCII Preview:");
                ImageUtil.printAsciiPreview(input);
            } else {
                System.out.println("Sample image not found: " + sampleFile.getAbsolutePath());
            }

            System.out.println("=== KNN TEST END ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printClassDistribution(ArrayList<DataPoint> dataset) {
        Map<String, Integer> counts = new HashMap<>();

        for (DataPoint dp : dataset) {
            counts.put(dp.label, counts.getOrDefault(dp.label, 0) + 1);
        }

        System.out.println("Class distribution:");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void evaluateOnDataset(KNNModel model, ArrayList<DataPoint> dataset) {
        int correct = 0;
        int unknownCount = 0;

        for (int i = 0; i < dataset.size(); i++) {
            DataPoint dp = dataset.get(i);
            PredictionResult result = model.predictDetailed(dp.features);

            boolean ok = result.getLabel().equals(dp.label);
            if (ok) {
                correct++;
            }
            if (result.isUnknown()) {
                unknownCount++;
            }

            System.out.printf(
                    "[%03d] actual=%-14s predicted=%-14s distance=%-8.4f %s\n",
                    i + 1,
                    dp.label,
                    result.getLabel(),
                    result.getDistance(),
                    ok ? "OK" : "WRONG"
            );
        }

        double accuracy = (correct * 100.0) / dataset.size();

        System.out.println();
        System.out.println("Correct: " + correct + "/" + dataset.size());
        System.out.printf("Accuracy: %.2f%%\n", accuracy);
        System.out.println("Unknown predictions: " + unknownCount);
    }
}