package Project3_6481328.MainGame;

import java.util.Random;

public class DifficultyManager {

    private long elapsedMs;
    private final Difficulty difficulty;

    public DifficultyManager(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void update(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    private double sec() {
        return elapsedMs / 1000.0;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public long getNormalSpawnIntervalMs() {
        switch (difficulty) {
            case EASY:
                return Math.max(650, (long) (1650 - sec() * 6.0));
            case HARD:
                return Math.max(300, (long) (1150 - sec() * 9.0));
            case BRUTAL:
                return Math.max(200, (long) (900 - sec() * 12.0));
            case IMPOSSIBLE:
                return Math.max(120, (long) (600 - sec() * 30.0));
            case NORMAL:
            default:
                return Math.max(450, (long) (1400 - sec() * 7.5));
        }
    }

    public double getNormalEnemySpeed() {
        switch (difficulty) {
            case EASY:
                return Math.min(150, 65 + sec() * 0.45);
            case HARD:
                return Math.min(220, 95 + sec() * 0.70);
            case BRUTAL:
                return Math.min(260, 110 + sec() * 0.90);
            case IMPOSSIBLE:
                return Math.min(300, 130 + sec() * 1.10);
            case NORMAL:
            default:
                return Math.min(180, 80 + sec() * 0.55);
        }
    }

    public long randomRingCooldownMs(Random random) {
        long min;
        long max;

        switch (difficulty) {
            case EASY:
                min = Math.max(9000, (long) (17000 - sec() * 25));
                max = Math.max(11500, (long) (20000 - sec() * 25));
                break;
            case HARD:
                min = Math.max(4000, (long) (11000 - sec() * 45));
                max = Math.max(5500, (long) (13500 - sec() * 45));
                break;
            case BRUTAL:
                min = Math.max(2500, (long) (8000 - sec() * 55));
                max = Math.max(3500, (long) (10000 - sec() * 55));
                break;
            case IMPOSSIBLE:
                min = Math.max(1500, (long) (5500 - sec() * 65));
                max = Math.max(2500, (long) (7000 - sec() * 65));
                break;
            case NORMAL:
            default:
                min = Math.max(6000, (long) (14000 - sec() * 35));
                max = Math.max(8500, (long) (17000 - sec() * 35));
                break;
        }

        return min + random.nextInt((int) Math.max(1, max - min + 1));
    }

    public long getRingWarningMs() {
        switch (difficulty) {
            case EASY:
                return Math.max(400, (long) (850 - sec() * 1.0));
            case HARD:
                return Math.max(180, (long) (550 - sec() * 2.0));
            case BRUTAL:
                return Math.max(130, (long) (450 - sec() * 2.5));
            case IMPOSSIBLE:
                return Math.max(100, (long) (350 - sec() * 3.0));
            case NORMAL:
            default:
                return Math.max(280, (long) (700 - sec() * 1.5));
        }
    }

    public double getRingSpeed() {
        switch (difficulty) {
            case EASY:
                return Math.min(260, 100 + sec() * 0.08);
            case HARD:
                return Math.min(380, 140 + sec() * 0.14);
            case BRUTAL:
                return Math.min(440, 170 + sec() * 0.18);
            case IMPOSSIBLE:
                return Math.min(500, 200 + sec() * 0.22);
            case NORMAL:
            default:
                return Math.min(320, 120 + sec() * 0.10);
        }
    }

    public int getRingCount() {
        switch (difficulty) {
            case EASY:
                return Math.min(10, 4 + (int) (sec() / 30.0));
            case HARD:
                return Math.min(18, 8 + (int) (sec() / 20.0));
            case BRUTAL:
                return Math.min(22, 10 + (int) (sec() / 15.0));
            case IMPOSSIBLE:
                return Math.min(28, 14 + (int) (sec() / 12.0));
            case NORMAL:
            default:
                return Math.min(14, 6 + (int) (sec() / 25.0));
        }
    }

    public double getRingRadius() {
        return 350;
    }

    public long getBombRollIntervalMs() {
        switch (difficulty) {
            case EASY:
                return 2200;
            case HARD:
                return 2800;
            case BRUTAL:
                return 3200;
            case IMPOSSIBLE:
                return 3600;
            case NORMAL:
            default:
                return 2500;
        }
    }

    public double getBombSpawnChance() {
        switch (difficulty) {
            case EASY:
                return 0.25;
            case HARD:
                return 0.12;
            case BRUTAL:
                return 0.08;
            case IMPOSSIBLE:
                return 0.05;
            case NORMAL:
            default:
                return 0.18;
        }
    }

    public long getBombLifetimeMs() {
        switch (difficulty) {
            case EASY:
                return 8500;
            case HARD:
                return 5500;
            case BRUTAL:
                return 4000;
            case IMPOSSIBLE:
                return 3000;
            case NORMAL:
            default:
                return 7000;
        }
    }

    public int getSpellKillLimit() {
        switch (difficulty) {
            case EASY:
                return 6;
            case HARD:
                return 3;
            case BRUTAL:
                return 2;
            case IMPOSSIBLE:
                return 1;
            case NORMAL:
            default:
                return 4;
        }
    }
}