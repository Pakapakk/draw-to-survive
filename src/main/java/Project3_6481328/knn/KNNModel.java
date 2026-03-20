package Project3_6481328.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KNNModel {

    private final ArrayList<DataPoint> dataset;
    private final int k;
    private double unknownThreshold = 10.5;

    public KNNModel(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }
        this.k = k;
        this.dataset = new ArrayList<>();
    }

    public void setUnknownThreshold(double unknownThreshold) {
        this.unknownThreshold = unknownThreshold;
    }

    public double getUnknownThreshold() {
        return unknownThreshold;
    }

    public void addData(double[] features, String label) {
        dataset.add(new DataPoint(features, label));
    }

    public void addDataPoint(DataPoint dp) {
        dataset.add(dp);
    }

    public void addAll(ArrayList<DataPoint> data) {
        dataset.addAll(data);
    }

    public int size() {
        return dataset.size();
    }

    public String predict(double[] input) {
        return predictDetailed(input).getLabel();
    }

    public PredictionResult predictDetailed(double[] input) {
        if (dataset.isEmpty()) {
            return new PredictionResult("unknown", Double.MAX_VALUE, true);
        }

        ArrayList<Neighbor> neighbors = new ArrayList<>();

        for (DataPoint dp : dataset) {
            double dist = euclideanDistance(dp.features, input);
            neighbors.add(new Neighbor(dp.label, dist));
        }

        Collections.sort(neighbors);

        int limit = Math.min(k, neighbors.size());

        HashMap<String, Integer> votes = new HashMap<>();
        HashMap<String, Double> distanceSums = new HashMap<>();

        for (int i = 0; i < limit; i++) {
            Neighbor n = neighbors.get(i);

            votes.put(n.label, votes.getOrDefault(n.label, 0) + 1);
            distanceSums.put(n.label, distanceSums.getOrDefault(n.label, 0.0) + n.distance);
        }

        String bestLabel = null;
        int bestVote = -1;
        double bestAvgDistance = Double.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            String label = entry.getKey();
            int vote = entry.getValue();
            double avgDistance = distanceSums.get(label) / vote;

            if (vote > bestVote) {
                bestVote = vote;
                bestAvgDistance = avgDistance;
                bestLabel = label;
            } else if (vote == bestVote && avgDistance < bestAvgDistance) {
                bestAvgDistance = avgDistance;
                bestLabel = label;
            }
        }

        boolean unknown = bestAvgDistance > unknownThreshold;

        if (unknown) {
            return new PredictionResult("unknown", bestAvgDistance, true);
        }

        return new PredictionResult(bestLabel, bestAvgDistance, false);
    }

    private double euclideanDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Feature lengths do not match");
        }

        double sum = 0.0;

        int size = 32;

        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];

            int x = i % size;
            int y = i / size;

            // distance from center
            double dx = x - size / 2.0;
            double dy = y - size / 2.0;

            double weight = 1.0 + 1.5 * Math.exp(-(dx * dx + dy * dy) / 100.0);

            sum += weight * d * d;
        }

        return Math.sqrt(sum);
    }

    private static class Neighbor implements Comparable<Neighbor> {
        String label;
        double distance;

        Neighbor(String label, double distance) {
            this.label = label;
            this.distance = distance;
        }

        @Override
        public int compareTo(Neighbor other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}