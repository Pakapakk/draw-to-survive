package draw_to_survive.MainGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 980;
    private static final int HEIGHT = 700;

    private final Timer timer;
    private long startTime;
    private long lastUpdateTime;

    private final Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();

    private final Map<SymbolType, BufferedImage> enemySprites = new EnumMap<>(SymbolType.class);

    private long lastNormalSpawnTime = 0;
    private long nextRingEventTime = 0;
    private long lastBombRollTime = 0;

    private final DifficultyManager difficulty;

    private BombPickup activeBomb;
    private RingWarning activeRingWarning;

    private String lastPrediction = "-";
    private String statusText = "Draw a symbol";
    private int score = 0;
    private boolean gameOver = false;

    private boolean up, down, left, right;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setBackground(new Color(18, 18, 24));
        addKeyListener(this);

        loadEnemySprites();

        player = new Player(WIDTH / 2.0, HEIGHT / 2.0);
        difficulty = new DifficultyManager();

        startTime = System.currentTimeMillis();
        lastUpdateTime = startTime;
        nextRingEventTime = startTime + difficulty.randomRingCooldownMs(random);

        timer = new Timer(16, this);
        timer.start();
    }

    private void loadEnemySprites() {
        try {
            BufferedImage sheet = ImageIO.read(Objects.requireNonNull(
                    getClass().getResource("/enemies.png")
            ));

            int cellW = sheet.getWidth() / 2;
            int cellH = sheet.getHeight() / 2;

            enemySprites.put(SymbolType.VERTICAL_LINE, sheet.getSubimage(0, 0, cellW, cellH));
            enemySprites.put(SymbolType.V, sheet.getSubimage(cellW, 0, cellW, cellH));
            enemySprites.put(SymbolType.CIRCLE, sheet.getSubimage(0, cellH, cellW, cellH));
            enemySprites.put(SymbolType.Z, sheet.getSubimage(cellW, cellH, cellW, cellH));

        } catch (Exception ex) {
            System.out.println("Could not load /enemies.png, fallback drawing will be used.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            repaint();
            return;
        }

        long now = System.currentTimeMillis();
        double dt = (now - lastUpdateTime) / 1000.0;
        lastUpdateTime = now;

        difficulty.update(now - startTime);

        updatePlayer(dt);
        updateBomb(now);
        updateSpawns(now);
        updateRingWarning(now);
        updateEnemies(dt);
        checkPlayerCollisions();

        repaint();
    }

    private void updatePlayer(double dt) {
        double vx = 0;
        double vy = 0;

        if (up) vy -= 1;
        if (down) vy += 1;
        if (left) vx -= 1;
        if (right) vx += 1;

        if (vx != 0 || vy != 0) {
            double len = Math.sqrt(vx * vx + vy * vy);
            vx /= len;
            vy /= len;
        }

        player.setX(clamp(player.getX() + vx * player.getSpeed() * dt, player.getRadius(), WIDTH - player.getRadius()));
        player.setY(clamp(player.getY() + vy * player.getSpeed() * dt, player.getRadius(), HEIGHT - player.getRadius()));
    }

    private void updateBomb(long now) {
        if (activeBomb != null && now > activeBomb.getExpireAt()) {
            activeBomb = null;
        }

        if (activeBomb == null && now - lastBombRollTime > difficulty.getBombRollIntervalMs()) {
            lastBombRollTime = now;

            double chance = difficulty.getBombSpawnChance();
            if (enemies.size() >= 12) {
                chance += 0.10;
            }

            if (random.nextDouble() < chance) {
                spawnBomb();
            }
        }

        if (activeBomb != null) {
            double d = distance(player.getX(), player.getY(), activeBomb.getX(), activeBomb.getY());
            if (d <= player.getRadius() + activeBomb.getRadius()) {
                killClosestEnemies(5);
                activeBomb = null;
                statusText = "BOOM! 5 closest symbols destroyed";
            }
        }
    }

    private void spawnBomb() {
        double minDist = 120;
        double maxDist = 250;

        for (int tries = 0; tries < 30; tries++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = minDist + random.nextDouble() * (maxDist - minDist);

            double x = player.getX() + Math.cos(angle) * dist;
            double y = player.getY() + Math.sin(angle) * dist;

            if (x > 40 && x < WIDTH - 40 && y > 40 && y < HEIGHT - 40) {
                activeBomb = new BombPickup(x, y, System.currentTimeMillis() + difficulty.getBombLifetimeMs());
                return;
            }
        }
    }

    private void updateSpawns(long now) {
        if (now - lastNormalSpawnTime >= difficulty.getNormalSpawnIntervalMs()) {
            lastNormalSpawnTime = now;
            spawnNormalEnemy();
        }

        if (now >= nextRingEventTime && activeRingWarning == null) {
            startRingEvent(now);
            nextRingEventTime = now + difficulty.randomRingCooldownMs(random);
        }
    }

    private void spawnNormalEnemy() {
        SymbolType type = SymbolType.random(random);

        int side = random.nextInt(4);
        double x, y;

        switch (side) {
            case 0 -> {
                x = random.nextInt(WIDTH);
                y = -30;
            }
            case 1 -> {
                x = random.nextInt(WIDTH);
                y = HEIGHT + 30;
            }
            case 2 -> {
                x = -30;
                y = random.nextInt(HEIGHT);
            }
            default -> {
                x = WIDTH + 30;
                y = random.nextInt(HEIGHT);
            }
        }

        Enemy enemy = new Enemy(type, x, y);
        enemy.setSpeed(difficulty.getNormalEnemySpeed());
        enemy.setMode(EnemyMode.CHASE_PLAYER);
        enemies.add(enemy);
    }

    private void startRingEvent(long now) {

        // === Choose ONE symbol for the entire ring ===
        SymbolType ringSymbol = SymbolType.random(random);

        // === Choose center (around player OR center of screen) ===
        double angle = random.nextDouble() * Math.PI * 2;
        double offset = 120 + random.nextDouble() * 120;

        double cx = clamp(player.getX() + Math.cos(angle) * offset, 120, WIDTH - 120);
        double cy = clamp(player.getY() + Math.sin(angle) * offset, 120, HEIGHT - 120);

        // === Create warning ===
        activeRingWarning = new RingWarning(
                cx,
                cy,
                difficulty.getRingRadius(),
                ringSymbol,
                now,
                now + difficulty.getRingWarningMs()
        );

        // Optional UI hint
        statusText = "Incoming Ring: " + ringSymbol.getDisplay();
    }

    private void updateRingWarning(long now) {

        if (activeRingWarning != null && now >= activeRingWarning.getEndTime()) {

            spawnRingEnemies(
                    difficulty.getRingRadius(),
                    activeRingWarning.getSymbolType()
            );

            activeRingWarning = null;
        }
    }

    private void spawnRingEnemies(double radius, SymbolType symbolType) {

        int count = difficulty.getRingCount();

        double centerX = player.getX();
        double centerY = player.getY();

        for (int i = 0; i < count; i++) {

            double angle = i * (Math.PI * 2 / count);

            double x = centerX + Math.cos(angle) * radius;
            double y = centerY + Math.sin(angle) * radius;

            Enemy enemy = new Enemy(symbolType, x, y);

            enemy.setMode(EnemyMode.SHRINK_TO_CENTER);
            enemy.setTargetX(centerX);
            enemy.setTargetY(centerY);

            enemy.setSpeed(difficulty.getRingSpeed());

            enemies.add(enemy);
        }

        statusText = "Ring Attack: " + symbolType.getDisplay();
    }

    private void updateEnemies(double dt) {
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy enemy = it.next();

            if (enemy.getMode() == EnemyMode.CHASE_PLAYER) {
                moveToward(enemy, player.getX(), player.getY(), enemy.getSpeed(), dt);
            } else {
                moveToward(enemy, enemy.getTargetX(), enemy.getTargetY(), enemy.getSpeed(), dt);

                if (distance(enemy.getX(), enemy.getY(), enemy.getTargetX(), enemy.getTargetY()) < 10) {
                    it.remove();
                    continue;
                }
            }

            if (enemy.getX() < -120 || enemy.getX() > WIDTH + 120 ||
                    enemy.getY() < -120 || enemy.getY() > HEIGHT + 120) {
                it.remove();
            }
        }
    }

    private void moveToward(Enemy enemy, double tx, double ty, double speed, double dt) {
        double dx = tx - enemy.getX();
        double dy = ty - enemy.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.0001) {
            enemy.setX(enemy.getX() + (dx / dist) * speed * dt);
            enemy.setY(enemy.getY() + (dy / dist) * speed * dt);
        }
    }

    private void checkPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (distance(player.getX(), player.getY(), enemy.getX(), enemy.getY())
                    <= player.getRadius() + enemy.getRadius() - 4) {
                gameOver = true;
                statusText = "Game Over";
                timer.stop();
                break;
            }
        }
    }

    public void castSpell(String rawPrediction) {
        if (gameOver) return;

        SymbolType predicted = SymbolType.fromPrediction(rawPrediction);
        lastPrediction = rawPrediction;

        if (predicted == null) {
            statusText = "Unknown spell";
            return;
        }

        int removed = removeClosestMatchingEnemies(predicted, difficulty.getSpellKillLimit());

        if (removed > 0) {
            score += removed * 10;
            statusText = "Cast " + predicted.getDisplay() + " - removed " + removed;
        } else {
            statusText = "Cast " + predicted.getDisplay() + " - no hit";
        }
    }

    private int removeClosestMatchingEnemies(SymbolType type, int limit) {
        List<Enemy> matches = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.getType() == type) {
                matches.add(enemy);
            }
        }

        matches.sort(Comparator.comparingDouble(
                e -> distance(e.getX(), e.getY(), player.getX(), player.getY())
        ));

        int removed = 0;
        for (int i = 0; i < Math.min(limit, matches.size()); i++) {
            enemies.remove(matches.get(i));
            removed++;
        }

        return removed;
    }

    private void killClosestEnemies(int count) {
        enemies.sort(Comparator.comparingDouble(
                e -> distance(e.getX(), e.getY(), player.getX(), player.getY())
        ));

        int removed = Math.min(count, enemies.size());
        for (int i = 0; i < removed; i++) {
            enemies.remove(0);
        }

        score += removed * 8;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawRingWarning(g2);
        drawBomb(g2);
        drawPlayer(g2);
        drawEnemies(g2);
        drawHUD(g2);

        if (gameOver) {
            drawGameOver(g2);
        }

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(35, 35, 45));
        for (int x = 0; x < WIDTH; x += 40) g2.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += 40) g2.drawLine(0, y, WIDTH, y);
    }

    private void drawRingWarning(Graphics2D g2) {
        if (activeRingWarning == null) return;

        long now = System.currentTimeMillis();
        double progress = (now - activeRingWarning.getStartTime()) /
                (double) (activeRingWarning.getEndTime() - activeRingWarning.getStartTime());

        int alpha = 80 + (int) (100 * Math.abs(Math.sin(progress * Math.PI * 6)));
        g2.setColor(new Color(255, 80, 80, Math.min(alpha, 180)));
        g2.setStroke(new BasicStroke(4f));

        double r = activeRingWarning.getRadius();
        g2.draw(new Ellipse2D.Double(
                activeRingWarning.getCx() - r,
                activeRingWarning.getCy() - r,
                r * 2,
                r * 2
        ));

//        g2.setColor(Color.WHITE);
//        g2.setFont(new Font("Arial", Font.BOLD, 24));
//
//        String text = activeRingWarning.getSymbolType().getDisplay();
//        FontMetrics fm = g2.getFontMetrics();
//
//        int tx = (int) activeRingWarning.getCx() - fm.stringWidth(text) / 2;
//        int ty = (int) activeRingWarning.getCy();
//
//        g2.drawString(text, tx, ty);
    }

    private void drawBomb(Graphics2D g2) {
        if (activeBomb == null) return;

        long now = System.currentTimeMillis();
        boolean blink = ((now / 180) % 2 == 0);

        int x = (int) activeBomb.getX();
        int y = (int) activeBomb.getY();

        g2.setColor(blink ? new Color(255, 210, 50) : new Color(255, 150, 20));
        g2.fillOval(x - 14, y - 14, 28, 28);

        g2.setColor(Color.BLACK);
        g2.drawOval(x - 14, y - 14, 28, 28);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("B", x - 5, y + 5);
    }

    private void drawPlayer(Graphics2D g2) {
        int x = (int) player.getX();
        int y = (int) player.getY();
        int r = (int) player.getRadius();

        g2.setColor(new Color(235, 235, 245));
        g2.fillOval(x - r, y - r, r * 2, r * 2);

        g2.setColor(new Color(80, 80, 100));
        g2.drawOval(x - r, y - r, r * 2, r * 2);
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            drawEnemy(g2, enemy);
        }
    }

    private void drawEnemy(Graphics2D g2, Enemy enemy) {
        int size = 48;
        int drawX = (int) enemy.getX() - size / 2;
        int drawY = (int) enemy.getY() - size / 2;

        BufferedImage sprite = enemySprites.get(enemy.getType());

        if (sprite != null) {
            g2.drawImage(sprite, drawX, drawY, size, size, null);
        } else {
            g2.setColor(enemy.getType().getFallbackColor());
            g2.fillOval(drawX, drawY, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String text = enemy.getType().getShortText();
            FontMetrics fm = g2.getFontMetrics();
            int tx = drawX + (size - fm.stringWidth(text)) / 2;
            int ty = drawY + ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, tx, ty);
        }
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(12, 12, 370, 110, 18, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Score: " + score, 24, 38);

        double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
        g2.drawString(String.format("Time: %.1fs", elapsed), 24, 62);
        g2.drawString("Prediction: " + lastPrediction, 24, 86);

        g2.setFont(new Font("Arial", Font.PLAIN, 15));
        g2.drawString(statusText, 24, 108);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 42));
        String t1 = "GAME OVER";
        int w1 = g2.getFontMetrics().stringWidth(t1);
        g2.drawString(t1, (WIDTH - w1) / 2, HEIGHT / 2 - 20);

        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        String t2 = "Final Score: " + score;
        int w2 = g2.getFontMetrics().stringWidth(t2);
        g2.drawString(t2, (WIDTH - w2) / 2, HEIGHT / 2 + 20);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}