package Project3_6481328.utils;

import java.awt.*;

public final class Settings {

    private Settings() {}

    // =========================
    // Window / Game
    // =========================
    public static final int GAME_WIDTH = 1200;
    public static final int GAME_HEIGHT = 700;
    public static final int TIMER_DELAY = 16;

    // =========================
    // Asset / File Paths
    // =========================
    public static final String PATH = "src/main/java/Project3_6481328/";
    public static final String ENEMY_SPRITE_RESOURCE_PRIMARY = "/Project3_6481328/resources/game_assets/enemies.png";
    public static final String ENEMY_SPRITE_RESOURCE_FALLBACK = "/dataset/enemies.png";
    public static final String ENEMY_SPRITE_FILE_PATH = PATH + "resources/game_assets/enemies.png";

    public static final String DATASET_VECTORS_PATH = PATH + "dataset/dataset_vectors.txt";
    public static final String SCORE_FILE_PATH = PATH + "scores.txt";
    public static final String FONT_PATH = PATH + "resources/fonts/VT323-Regular.ttf";
    public static final String BOMB_PATH = PATH + "resources/game_assets/bomb.gif";
    public static final String EXPLOSION_PATH = PATH + "resources/game_assets/explosions.gif";

    // =========================
    // Colors
    // =========================
    public static final Color BG = new Color(16, 18, 28);
    public static final Color PANEL_BG = new Color(28, 32, 48);
    public static final Color INPUT_BG = new Color(20, 24, 38);

    public static final Color GAME_BG = new Color(18, 18, 24);
    public static final Color GRID = new Color(35, 35, 45);
    public static final Color HUD_BG = new Color(0, 0, 0, 150);
    public static final Color OVERLAY_BG = new Color(0, 0, 0, 180);

    public static final Color TEXT_PRIMARY = new Color(235, 235, 245);
    public static final Color TEXT_SECONDARY = new Color(180, 185, 205);
    public static final Color PLAYER_OUTLINE = new Color(80, 80, 100);

    public static final Color ACCENT = new Color(153, 102, 255);
    public static final Color SCORE_COLOR = new Color(120, 220, 255);

    public static final Color EASY_COLOR = new Color(120, 220, 120);
    public static final Color NORMAL_COLOR = new Color(180, 180, 200);
    public static final Color HARD_COLOR = new Color(255, 120, 120);

    public static final Color RANK_COLOR = new Color(255, 220, 120);
    public static final Color SELECTED_ROW = new Color(70, 55, 130);
    public static final Color ROW_A = new Color(26, 30, 46);
    public static final Color ROW_B = new Color(20, 24, 38);

    public static final Color BUTTON_BLUE = new Color(80, 140, 255);
    public static final Color BUTTON_PURPLE = new Color(120, 80, 180);

    public static final Color RING_WARNING = new Color(255, 80, 80);
    public static final Color BOMB_A = new Color(255, 210, 50);
    public static final Color BOMB_B = new Color(255, 150, 20);

    public static final Color CARD_BORDER = new Color(80, 90, 130);
    public static final Color INPUT_BORDER = new Color(65, 70, 95);

    // =========================
    // Font Sizes
    // =========================
    public static final float FONT_TITLE_BIG = 42f;
    public static final float FONT_TITLE_MEDIUM = 26f;
    public static final float FONT_LABEL = 18f;
    public static final float FONT_INPUT = 16f;
    public static final float FONT_SMALL = 14f;
    public static final float FONT_TINY = 12f;

    public static final float FONT_BOMB = 14f;
    public static final float FONT_ENEMY_FALLBACK = 24f;
    public static final float FONT_HUD = 18f;
    public static final float FONT_STATUS = 15f;
    public static final float FONT_GAME_OVER = 42f;
    public static final float FONT_FINAL_SCORE = 22f;
    public static final float FONT_SCORE_BIG = 20f;

    // =========================
    // Layout / Sizes
    // =========================
    public static final int RIGHT_PANEL_WIDTH = 300;
    public static final int DRAW_PANEL_SIZE = 260;

    public static final int GRID_SIZE = 40;
    public static final int ENEMY_DRAW_SIZE = 48;

    public static final int HUD_X = 12;
    public static final int HUD_Y = 12;
    public static final int HUD_W = 420;
    public static final int HUD_H = 125;
    public static final int HUD_ARC = 18;

    public static final int TEXTFIELD_HEIGHT = 44;
    public static final int COMBOBOX_HEIGHT = 44;

    public static final int BOMB_SIZE = 40;

    public static final int EXPLOSION_SIZE = 48;
    public static final int EXPLOSION_DURATION_MS = 600;

    // =========================
    // KNN
    // =========================
    public static final int KNN_K = 7;
    public static final double KNN_UNKNOWN_THRESHOLD = 20.0;
}