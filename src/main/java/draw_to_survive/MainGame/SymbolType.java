package draw_to_survive.MainGame;

import java.awt.*;
import java.util.Random;

public enum SymbolType {
    VERTICAL_LINE("vertical_line", "|", new Color(220, 80, 80)),
    V("V", "V", new Color(80, 130, 220)),
    CIRCLE("circle", "O", new Color(80, 200, 90)),
    Z("Z", "Z", new Color(170, 90, 220));

    private final String display;
    private final String shortText;
    private final Color fallbackColor;

    SymbolType(String display, String shortText, Color fallbackColor) {
        this.display = display;
        this.shortText = shortText;
        this.fallbackColor = fallbackColor;
    }

    public String getDisplay() {
        return display;
    }

    public String getShortText() {
        return shortText;
    }

    public Color getFallbackColor() {
        return fallbackColor;
    }

    public static SymbolType random(Random random) {
        SymbolType[] values = values();
        return values[random.nextInt(values.length)];
    }

    public static SymbolType fromPrediction(String raw) {
        if (raw == null) return null;

        String s = raw.trim().toLowerCase();

        if (s.equals("vertical_line") || s.equals("vertical line") || s.equals("|") || s.equals("line") || s.equals("i")) {
            return VERTICAL_LINE;
        }
        if (s.equals("v")) {
            return V;
        }
        if (s.equals("circle") || s.equals("o") || s.equals("0")) {
            return CIRCLE;
        }
        if (s.equals("z")) {
            return Z;
        }

        return null;
    }
}