package draw_to_survive.MainGame;

import draw_to_survive.knn.ImageUtil;
import draw_to_survive.knn.KNNModel;
import draw_to_survive.knn.PredictionResult;

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