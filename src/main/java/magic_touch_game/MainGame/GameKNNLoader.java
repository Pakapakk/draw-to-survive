package magic_touch_game.MainGame;

import magic_touch_game.knn.DataPoint;
import magic_touch_game.knn.KNNModel;
import magic_touch_game.knn.VectorDatasetLoader;

import java.util.ArrayList;

public class GameKNNLoader {

    private GameKNNLoader() {
    }

    public static KNNModel buildModel() {
        try {
            String datasetPath = "src/main/java/magic_touch_game/resources/dataset_vectors.txt";

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