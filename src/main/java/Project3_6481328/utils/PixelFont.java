package Project3_6481328.utils;

import java.awt.*;
import java.io.InputStream;

public class PixelFont {

    private static Font baseFont;

    static {
        try {
            InputStream is = new java.io.FileInputStream(Settings.FONT_PATH);

            if (is == null) {
                throw new RuntimeException("Font file not found in resources!");
            }

            baseFont = Font.createFont(Font.TRUETYPE_FONT, is);

        } catch (Exception e) {
            System.out.println("Failed to load pixel font, fallback to default.");
            baseFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    public static Font get(float size) {
        return baseFont.deriveFont(size);
    }
}