package Project3_6481328.MainGame;

import Project3_6481328.knn.ImageUtil;
import Project3_6481328.knn.KNNModel;
import Project3_6481328.knn.PredictionResult;

import java.awt.image.BufferedImage;

public class SpellRecognizer {

    private final KNNModel model;

    public SpellRecognizer() {
        this.model = GameKNNLoader.buildModel();
    }

    public String recognize(BufferedImage drawingImage) {
        if (model == null || drawingImage == null) {
            return "unknown";
        }

        return model.predict(ImageUtil.drawingToVector(drawingImage));
    }

    public PredictionResult recognizeDetailed(BufferedImage drawingImage) {
        if (model == null || drawingImage == null) {
            return new PredictionResult("unknown", Double.MAX_VALUE, true);
        }

        return model.predictDetailed(ImageUtil.drawingToVector(drawingImage));
    }
}