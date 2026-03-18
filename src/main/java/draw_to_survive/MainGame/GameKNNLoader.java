package draw_to_survive.MainGame;

import draw_to_survive.knn.DataPoint;
import draw_to_survive.knn.KNNModel;
import draw_to_survive.knn.VectorDatasetLoader;

import java.util.ArrayList;

public class GameKNNLoader {

    private GameKNNLoader() {
    }

    public static KNNModel buildModel() {
        try {
            String datasetPath = "src/main/java/draw_to_survive/resources/dataset_vectors.txt";

            ArrayList<DataPoint> dataset = VectorDatasetLoader.load(datasetPath);

            KNNModel model = new KNNModel(7);
            model.setUnknownThreshold(20.0);
            model.addAll(dataset);

            System.out.println("KNN model loaded with " + dataset.size() + " samples");
            return model;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}