package Project3_6481328.MainGame;

public class BombPickup {
    private final double x;
    private final double y;
    private final double radius;
    private final long expireAt;

    public BombPickup(double x, double y, long expireAt) {
        this.x = x;
        this.y = y;
        this.radius = 16;
        this.expireAt = expireAt;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public long getExpireAt() {
        return expireAt;
    }
}