package magic_touch_game.knn;

public class PredictionResult {
    private final String label;
    private final double distance;
    private final boolean unknown;

    public PredictionResult(String label, double distance, boolean unknown) {
        this.label = label;
        this.distance = distance;
        this.unknown = unknown;
    }

    public String getLabel() {
        return label;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isUnknown() {
        return unknown;
    }

    @Override
    public String toString() {
        return "PredictionResult{" +
                "label='" + label + '\'' +
                ", distance=" + distance +
                ", unknown=" + unknown +
                '}';
    }
}