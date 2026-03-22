package Project3_6481328.skins;

import java.util.List;

public class SkinManager {

    private static final List<PlayerSkin> SKINS = List.of(
            new PlayerSkin("White", "white"),
            new PlayerSkin("Brown", "brown"),
            new PlayerSkin("Green", "green"),
            new PlayerSkin("Grey", "grey"),
            new PlayerSkin("Pink", "pink"),
            new PlayerSkin("Purple", "purple")
    );

    private static PlayerSkin selectedSkin = SKINS.get(0); // default brown

    public static List<PlayerSkin> getSkins() {
        return SKINS;
    }

    public static PlayerSkin getSelectedSkin() {
        return selectedSkin;
    }

    public static void setSelectedSkin(PlayerSkin skin) {
        if (skin != null) {
            selectedSkin = skin;
        }
    }

    public static String getSkinBasePath() {
        return "/game_assets/player_skins/" + selectedSkin.getFolder() + "/";
    }
}