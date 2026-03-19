package Project3_6481328.MainGame;

public class Player {
    private double x;
    private double y;
    private final double radius;
    private final double speed;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 18;
        this.speed = 240;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public double getSpeed() {
        return speed;
    }


}