package Project3_6481328.MainGame;

public class RingWarning {

    private final double cx;
    private final double cy;
    private final double radius;
    private final SymbolType symbolType;

    private final long startTime;
    private final long endTime;

    public RingWarning(double cx, double cy, double radius,
                       SymbolType symbolType,
                       long startTime, long endTime) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.symbolType = symbolType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public double getCx() { return cx; }
    public double getCy() { return cy; }
    public double getRadius() { return radius; }
    public SymbolType getSymbolType() { return symbolType; }

    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
}