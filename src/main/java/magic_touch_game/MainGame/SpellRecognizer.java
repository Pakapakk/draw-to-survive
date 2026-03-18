package magic_touch_game.MainGame;

import magic_touch_game.knn.ImageUtil;
import magic_touch_game.knn.KNNModel;
import magic_touch_game.knn.PredictionResult;

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