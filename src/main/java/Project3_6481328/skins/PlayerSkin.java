package Project3_6481328.skins;

public class PlayerSkin {
    private final String name;
    private final String folder;

    public PlayerSkin(String name, String folder) {
        this.name = name;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    @Override
    public String toString() {
        return name;
    }
}
