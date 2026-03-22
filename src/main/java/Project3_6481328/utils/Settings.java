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
    public static final String HEART_PATH = PATH + "resources/game_assets/heart.png";

    // =========================
    // Sound Paths
    // =========================
    public static final String SOUNDS_PATH = PATH + "resources/sounds/";
    public static final String SFX_MENU_MUSIC    = SOUNDS_PATH + "menuMusic.wav";
    public static final String SFX_GAME_MUSIC    = SOUNDS_PATH + "gameMusic.wav";
    public static final String SFX_BUTTON        = SOUNDS_PATH + "buttonSfx.wav";
    public static final String SFX_PLAYER_DEAD   = SOUNDS_PATH + "playerDeadSfx.wav";
    public static final String SFX_ENEMY_DEAD    = SOUNDS_PATH + "enemyDeadSfx.wav";
    public static final String SFX_SPELL_1       = SOUNDS_PATH + "spellCast_1.wav";
    public static final String SFX_SPELL_2       = SOUNDS_PATH + "spellCast_2.wav";
    public static final String SFX_SPELL_3       = SOUNDS_PATH + "spellCast_3.wav";
    public static final String SFX_SPELL_4       = SOUNDS_PATH + "spellCast_4.wav";
    public static final String SFX_FREEZE        = SOUNDS_PATH + "spellFreeze.wav";
    public static final String SFX_RING_WARNING  = SOUNDS_PATH + "circleWarning.wav";
    public static final String SFX_GAME_OVER     = SOUNDS_PATH + "gameOver.wav";
    public static final String SFX_HURT     = SOUNDS_PATH + "playerHurt.wav";

    // =========================
    // Player Skins
    // =========================
    public static final String PLAYER_SKINS_BASE_PATH = PATH + "resources/game_assets/player_skins/";
    public static final String DEFAULT_PLAYER_SKIN = "white";

    public static final String[] AVAILABLE_PLAYER_SKINS = {
            "white", "brown", "green", "grey", "pink", "purple"
    };

    public static String getPlayerSkinFolder(String skinName) {
        return PLAYER_SKINS_BASE_PATH + skinName + "/";
    }

    public static String getPlayerIdlePath(String skinName) {
        return getPlayerSkinFolder(skinName) + "idle.gif";
    }

    public static String getPlayerMovePath(String skinName) {
        return getPlayerSkinFolder(skinName) + "move.gif";
    }

    public static String getPlayerHurtPath(String skinName) {
        return getPlayerSkinFolder(skinName) + "hurt.gif";
    }

    public static String getPlayerDeadPath(String skinName) {
        return getPlayerSkinFolder(skinName) + "dead.gif";
    }

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
    public static final Color BRUTAL_COLOR = new Color(255, 80, 30);
    public static final Color IMPOSSIBLE_COLOR = new Color(220, 0, 0);

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
    public static final float FONT_TITLE_BIG = 44f;
    public static final float FONT_TITLE_MEDIUM = 28f;
    public static final float FONT_LABEL = 20f;
    public static final float FONT_INPUT = 18f;
    public static final float FONT_SMALL = 16f;
    public static final float FONT_TINY = 14f;

    public static final float FONT_BOMB = 16f;
    public static final float FONT_ENEMY_FALLBACK = 26f;
    public static final float FONT_HUD = 20f;
    public static final float FONT_STATUS = 17f;
    public static final float FONT_GAME_OVER = 44f;
    public static final float FONT_FINAL_SCORE = 24f;
    public static final float FONT_SCORE_BIG = 22f;

    // =========================
    // Layout / Sizes
    // =========================
    public static final int RIGHT_PANEL_WIDTH = 300;
    public static final int DRAW_PANEL_SIZE = 260;

    public static final int GRID_SIZE = 40;
    public static final int ENEMY_DRAW_SIZE = 48;
    public static final int PLAYER_DRAW_SIZE = 56;

    public static final int HUD_X = 12;
    public static final int HUD_Y = 12;
    public static final int HUD_W = 420;
    public static final int HUD_H = 125;
    public static final int HUD_ARC = 18;

    public static final int TEXTFIELD_HEIGHT = 44;
    public static final int COMBOBOX_HEIGHT = 44;

    public static final int BOMB_SIZE = 40;

    public static final int PLAYER_MAX_HEALTH = 3;
    public static final int HEART_SIZE = 28;
    public static final long IFRAMES_DURATION_MS = 1500;

    public static final int EXPLOSION_SIZE = 48;
    public static final int EXPLOSION_DURATION_MS = 600;

    // =========================
    // KNN
    // =========================
    public static final int KNN_K = 7;
    public static final double KNN_UNKNOWN_THRESHOLD = 20.0;
}