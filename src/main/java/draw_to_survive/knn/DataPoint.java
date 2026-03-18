package draw_to_survive.knn;

public class DataPoint {
    public double[] features;
    public String label;

    public DataPoint(double[] features, String label) {
        this.features = features;
        this.label = label;
    }
}