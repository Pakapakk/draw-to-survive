package Project3_6481328.MainGame;

import Project3_6481328.knn.DataPoint;
import Project3_6481328.knn.KNNModel;
import Project3_6481328.knn.VectorDatasetLoader;
import Project3_6481328.utils.Settings;

import java.util.ArrayList;

public class GameKNNLoader {

    private GameKNNLoader() {
    }

    public static KNNModel buildModel() {
        try {
            ArrayList<DataPoint> dataset = VectorDatasetLoader.load(Settings.DATASET_VECTORS_PATH);

            KNNModel model = new KNNModel(Settings.KNN_K);
            model.setUnknownThreshold(Settings.KNN_UNKNOWN_THRESHOLD);
            model.addAll(dataset);

            System.out.println("KNN model loaded with " + dataset.size() + " samples");
            return model;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}