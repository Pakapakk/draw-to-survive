package Project3_6481328.knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class VectorDatasetLoader {

    private VectorDatasetLoader() {}

    public static ArrayList<DataPoint> load(String path) {
        ArrayList<DataPoint> dataset = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length != 2) {
                    continue;
                }

                String label = parts[0].trim();
                String[] values = parts[1].split(",");

                double[] features = new double[values.length];

                for (int i = 0; i < values.length; i++) {
                    features[i] = Double.parseDouble(values[i].trim());
                }

                dataset.add(new DataPoint(features, label));
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }
}