package draw_to_survive.MainGame;

import java.util.Random;

public class DifficultyManager {

    private long elapsedMs;

    public void update(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    private double sec() {
        return elapsedMs / 1000.0;
    }

    public long getNormalSpawnIntervalMs() {
        return Math.max(450, (long) (1400 - sec() * 7.5));
    }

    public double getNormalEnemySpeed() {
        return Math.min(180, 80 + sec() * 0.55);
    }

    public long randomRingCooldownMs(Random random) {
        long min = Math.max(6000, (long) (14000 - sec() * 35));
        long max = Math.max(8500, (long) (17000 - sec() * 35));
        return min + random.nextInt((int) Math.max(1, max - min + 1));
    }

    public long getRingWarningMs() {
        return Math.max(280, (long) (700 - sec() * 1.5));
    }

    public double getRingSpeed() {
        return Math.min(320, 120 + sec() * 0.1);
    }

    public int getRingCount() {
        return Math.min(14, 6 + (int) (sec() / 25.0));
    }

    public double getRingRadius() {
        return 350;
    }

    public long getBombRollIntervalMs() {
        return 2500;
    }

    public double getBombSpawnChance() {
        return 0.18;
    }

    public long getBombLifetimeMs() {
        return 7000;
    }

    public int getSpellKillLimit() {
        return 4;
    }
}