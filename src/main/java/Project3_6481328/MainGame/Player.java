package Project3_6481328.MainGame;

import Project3_6481328.utils.Settings;

public class Player {
    private double x;
    private double y;
    private final double radius;
    private final double speed;

    private int health;
    private long invincibleUntil = 0;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 18;
        this.speed = 240;
        this.health = Settings.PLAYER_MAX_HEALTH;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getRadius() { return radius; }
    public double getSpeed() { return speed; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, health); }

    public boolean isInvincible(long now) { return now < invincibleUntil; }
    public void setInvincibleUntil(long t) { this.invincibleUntil = t; }
    public long getInvincibleUntil() { return invincibleUntil; }
}