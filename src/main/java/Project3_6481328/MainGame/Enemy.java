package Project3_6481328.MainGame;

public class Enemy {
    private final SymbolType type;
    private double x;
    private double y;
    private double radius;
    private double speed;

    private EnemyMode mode;
    private double targetX;
    private double targetY;

    public Enemy(SymbolType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.radius = 20;
        this.speed = 90;
        this.mode = EnemyMode.CHASE_PLAYER;
    }

    public SymbolType getType() {
        return type;
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public EnemyMode getMode() {
        return mode;
    }

    public void setMode(EnemyMode mode) {
        this.mode = mode;
    }

    public double getTargetX() {
        return targetX;
    }

    public void setTargetX(double targetX) {
        this.targetX = targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public void setTargetY(double targetY) {
        this.targetY = targetY;
    }
}