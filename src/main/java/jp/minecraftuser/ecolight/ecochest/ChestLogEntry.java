package jp.minecraftuser.ecolight.ecochest;

class ChestLogEntry {
    public String getLocation() {
        return location;
    }

    public String getPlayer() {
        return player;
    }

    public String getAction() {
        return action;
    }

    private String location;
    private String player;
    private String action;

    public ChestLogEntry(String location, String player, String act) {
        this.location = location;
        this.player = player;
        this.action = act;
    }

}