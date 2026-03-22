package Project3_6481328.MainGame;

import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

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

    private static final int WIDTH = Settings.GAME_WIDTH;
    private static final int HEIGHT = Settings.GAME_HEIGHT;

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
    private boolean facingLeft = false;

    private static final long FREEZE_DURATION_MS = 1000;
    private static final long FREEZE_COOLDOWN_MS = 10000;
    private boolean frozen = false;
    private long freezeEndTime = 0;
    private long freezeCooldownEndTime = 0;

    private boolean paused = false;
    private boolean countingDown = false;
    private int countdownValue = 3;
    private Timer countdownTimer;
    private long pauseStartTime = 0;
    private long totalPausedMs = 0;
    private Rectangle resumeButtonRect;
    private Rectangle quitButtonRect;

    private final DinoDrawSurvivalGame parentFrame;
    private final String playerName;
    private final String selectedSkin;

    private ImageIcon bombGif;
    private ImageIcon explosionGif;
    private BufferedImage heartImage;

    private ImageIcon playerIdleGif;
    private ImageIcon playerMoveGif;
    private ImageIcon playerHurtGif;
    private ImageIcon playerDeadGif;

    private final List<ExplosionEffect> explosions = new ArrayList<>();

    public GamePanel(DinoDrawSurvivalGame parentFrame, String playerName, Difficulty selectedDifficulty, String selectedSkin) {
        this.parentFrame = parentFrame;
        this.playerName = playerName;
        this.selectedSkin = (selectedSkin == null || selectedSkin.isBlank())
                ? Settings.DEFAULT_PLAYER_SKIN
                : selectedSkin;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setBackground(Settings.BG);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!paused || countingDown) return;
                Point p = e.getPoint();
                if (resumeButtonRect != null && resumeButtonRect.contains(p)) {
                    startCountdown();
                } else if (quitButtonRect != null && quitButtonRect.contains(p)) {
                    paused = false;
                    triggerGameOver();
                }
            }
        });

        loadEnemySprites();

        bombGif = new ImageIcon(Settings.BOMB_PATH);
        if (bombGif.getIconWidth() <= 0 || bombGif.getIconHeight() <= 0) {
            System.out.println("Failed to load bomb gif from: " + Settings.BOMB_PATH);
            bombGif = null;
        } else {
            System.out.println("Bomb gif loaded: " + Settings.BOMB_PATH);
        }

        explosionGif = new ImageIcon(Settings.EXPLOSION_PATH);
        if (explosionGif.getIconWidth() <= 0 || explosionGif.getIconHeight() <= 0) {
            System.out.println("Failed to load explosion gif from: " + Settings.EXPLOSION_PATH);
            explosionGif = null;
        } else {
            System.out.println("Explosion gif loaded: " + Settings.EXPLOSION_PATH);
        }

        try {
            java.net.URL heartUrl = getClass().getResource("/Project3_6481328/resources/game_assets/heart.png");
            if (heartUrl != null) {
                heartImage = ImageIO.read(heartUrl);
            } else {
                heartImage = ImageIO.read(new java.io.File(Settings.HEART_PATH));
            }
            System.out.println("Heart image loaded.");
        } catch (Exception e) {
            System.out.println("Failed to load heart image: " + e.getMessage());
            heartImage = null;
        }

        loadPlayerSkin();

        player = new Player(WIDTH / 2.0, HEIGHT / 2.0);
        difficulty = new DifficultyManager(selectedDifficulty);

        startTime = System.currentTimeMillis();
        lastUpdateTime = startTime;
        nextRingEventTime = startTime + difficulty.randomRingCooldownMs(random);

        timer = new Timer(Settings.TIMER_DELAY, this);
        timer.start();
    }

    private void loadPlayerSkin() {
        playerIdleGif = loadGif(Settings.getPlayerIdlePath(selectedSkin), "idle");
        playerMoveGif = loadGif(Settings.getPlayerMovePath(selectedSkin), "move");
        playerHurtGif = loadGif(Settings.getPlayerHurtPath(selectedSkin), "hurt");
        playerDeadGif = loadGif(Settings.getPlayerDeadPath(selectedSkin), "dead");

        if (playerIdleGif == null) {
            playerIdleGif = loadGif(Settings.getPlayerIdlePath(Settings.DEFAULT_PLAYER_SKIN), "idle fallback");
        }
        if (playerMoveGif == null) {
            playerMoveGif = loadGif(Settings.getPlayerMovePath(Settings.DEFAULT_PLAYER_SKIN), "move fallback");
        }
        if (playerHurtGif == null) {
            playerHurtGif = loadGif(Settings.getPlayerHurtPath(Settings.DEFAULT_PLAYER_SKIN), "hurt fallback");
        }
        if (playerDeadGif == null) {
            playerDeadGif = loadGif(Settings.getPlayerDeadPath(Settings.DEFAULT_PLAYER_SKIN), "dead fallback");
        }
    }

    private ImageIcon loadGif(String path, String label) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            System.out.println("Failed to load player " + label + " gif from: " + path);
            return null;
        } else {
            System.out.println("Loaded player " + label + " gif: " + path);
            return icon;
        }
    }

    private void loadEnemySprites() {
        BufferedImage sheet = null;

        try {
            java.net.URL url = getClass().getResource(Settings.ENEMY_SPRITE_RESOURCE_PRIMARY);
            if (url != null) {
                sheet = ImageIO.read(url);
            }

            if (sheet == null) {
                url = getClass().getResource(Settings.ENEMY_SPRITE_RESOURCE_FALLBACK);
                if (url != null) {
                    sheet = ImageIO.read(url);
                }
            }

            if (sheet == null) {
                java.io.File file = new java.io.File(Settings.ENEMY_SPRITE_FILE_PATH);
                if (file.exists()) {
                    sheet = ImageIO.read(file);
                }
            }

            if (sheet == null) {
                throw new RuntimeException("enemies.png not found");
            }

            int cellW = sheet.getWidth() / 2;
            int cellH = sheet.getHeight() / 2;

            enemySprites.put(SymbolType.VERTICAL_LINE, sheet.getSubimage(0, 0, cellW, cellH));
            enemySprites.put(SymbolType.V, sheet.getSubimage(cellW, 0, cellW, cellH));
            enemySprites.put(SymbolType.CIRCLE, sheet.getSubimage(0, cellH, cellW, cellH));
            enemySprites.put(SymbolType.Z, sheet.getSubimage(cellW, cellH, cellW, cellH));

            System.out.println("Loaded enemy sprite sheet successfully.");

        } catch (Exception ex) {
            System.out.println("Could not load enemy sprite sheet, fallback drawing will be used.");
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            repaint();
            return;
        }

        if (paused) {
            repaint();
            return;
        }

        long now = System.currentTimeMillis();
        double dt = (now - lastUpdateTime) / 1000.0;
        lastUpdateTime = now;

        difficulty.update(now - startTime);

        updatePlayer(dt);
        updateFreeze(now);
        updateBomb(now);
        updateSpawns(now);
        updateRingWarning(now);
        updateEnemies(dt);
        checkPlayerCollisions();

        repaint();
    }

    private long effectiveNow() {
        return paused ? pauseStartTime : System.currentTimeMillis();
    }

    private void activatePause() {
        if (gameOver || countingDown) return;
        paused = true;
        pauseStartTime = System.currentTimeMillis();
        repaint();
    }

    private void startCountdown() {
        countingDown = true;
        countdownValue = 3;
        repaint();

        countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            countdownValue--;
            if (countdownValue <= 0) {
                countdownTimer.stop();
                paused = false;
                countingDown = false;

                long pausedMs = System.currentTimeMillis() - pauseStartTime;
                totalPausedMs += pausedMs;
                freezeEndTime += pausedMs;
                freezeCooldownEndTime += pausedMs;

                if (activeBomb != null) {
                    activeBomb = new BombPickup(
                            activeBomb.getX(),
                            activeBomb.getY(),
                            activeBomb.getExpireAt() + pausedMs
                    );
                }

                lastUpdateTime = System.currentTimeMillis();
            }
            repaint();
        });
        countdownTimer.start();
    }

    private void activateFreeze() {
        long now = System.currentTimeMillis();
        if (now < freezeCooldownEndTime) return;

        frozen = true;
        freezeEndTime = now + FREEZE_DURATION_MS;
        freezeCooldownEndTime = now + FREEZE_COOLDOWN_MS;
        statusText = "FREEZE! All enemies frozen!";
    }

    private void updateFreeze(long now) {
        if (frozen && now >= freezeEndTime) {
            frozen = false;
        }
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

        if (vx < 0) {
            facingLeft = true;
        } else if (vx > 0) {
            facingLeft = false;
        }

        player.setX(clamp(
                player.getX() + vx * player.getSpeed() * dt,
                player.getRadius(),
                WIDTH - player.getRadius()
        ));
        player.setY(clamp(
                player.getY() + vy * player.getSpeed() * dt,
                player.getRadius(),
                HEIGHT - player.getRadius()
        ));
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
        SymbolType ringSymbol = SymbolType.random(random);

        double angle = random.nextDouble() * Math.PI * 2;
        double offset = 120 + random.nextDouble() * 120;

        double cx = clamp(player.getX() + Math.cos(angle) * offset, 120, WIDTH - 120);
        double cy = clamp(player.getY() + Math.sin(angle) * offset, 120, HEIGHT - 120);

        activeRingWarning = new RingWarning(
                cx,
                cy,
                difficulty.getRingRadius(),
                ringSymbol,
                now,
                now + difficulty.getRingWarningMs()
        );

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

            if (!frozen) {
                if (enemy.getMode() == EnemyMode.CHASE_PLAYER) {
                    moveToward(enemy, player.getX(), player.getY(), enemy.getSpeed(), dt);
                } else {
                    moveToward(enemy, enemy.getTargetX(), enemy.getTargetY(), enemy.getSpeed(), dt);

                    if (distance(enemy.getX(), enemy.getY(), enemy.getTargetX(), enemy.getTargetY()) < 10) {
                        it.remove();
                        continue;
                    }
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
        long now = System.currentTimeMillis();
        if (player.isInvincible(now)) return;

        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();
            if (distance(player.getX(), player.getY(), enemy.getX(), enemy.getY())
                    <= player.getRadius() + enemy.getRadius() - 4) {

                player.setHealth(player.getHealth() - 1);
                player.setInvincibleUntil(now + Settings.IFRAMES_DURATION_MS);

                addExplosion(enemy.getX(), enemy.getY());
                it.remove();

                if (player.getHealth() <= 0) {
                    statusText = "Game Over";
                    triggerGameOver();
                } else {
                    statusText = "Ouch! " + player.getHealth() + " heart(s) left!";
                }
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
            Enemy enemy = matches.get(i);
            addExplosion(enemy.getX(), enemy.getY());
            enemies.remove(enemy);
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
            Enemy enemy = enemies.remove(0);
            addExplosion(enemy.getX(), enemy.getY());
        }

        score += removed * 8;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawGrid(g2);
        drawRingWarning(g2);
        drawBomb(g2);
        drawExplosion(g2);
        drawPlayer(g2);
        drawEnemies(g2);
        drawHUD(g2);
        drawHearts(g2);
        drawFreezeHUD(g2);

        if (gameOver) {
            drawGameOver(g2);
        }

        if (paused) {
            drawPauseOverlay(g2);
        }

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(Settings.GRID);
        for (int x = 0; x < WIDTH; x += Settings.GRID_SIZE) g2.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += Settings.GRID_SIZE) g2.drawLine(0, y, WIDTH, y);
    }

    private void drawRingWarning(Graphics2D g2) {
        if (activeRingWarning == null) return;

        long now = System.currentTimeMillis();
        double progress = (now - activeRingWarning.getStartTime()) /
                (double) (activeRingWarning.getEndTime() - activeRingWarning.getStartTime());

        int alpha = 80 + (int) (100 * Math.abs(Math.sin(progress * Math.PI * 6)));
        g2.setColor(new Color(
                Settings.RING_WARNING.getRed(),
                Settings.RING_WARNING.getGreen(),
                Settings.RING_WARNING.getBlue(),
                Math.min(alpha, 180)
        ));
        g2.setStroke(new BasicStroke(4f));

        double r = activeRingWarning.getRadius();
        g2.draw(new Ellipse2D.Double(
                activeRingWarning.getCx() - r,
                activeRingWarning.getCy() - r,
                r * 2,
                r * 2
        ));
    }

    private void drawBomb(Graphics2D g2) {
        if (activeBomb == null) return;

        int x = (int) activeBomb.getX();
        int y = (int) activeBomb.getY();

        int size = Settings.BOMB_SIZE;

        if (bombGif != null && bombGif.getIconWidth() > 0) {
            g2.drawImage(
                    bombGif.getImage(),
                    x - size / 2,
                    y - size / 2,
                    size,
                    size,
                    this
            );
        } else {
            long now = System.currentTimeMillis();
            boolean blink = ((now / 180) % 2 == 0);

            g2.setColor(blink ? Settings.BOMB_A : Settings.BOMB_B);
            g2.fillOval(x - 14, y - 14, 28, 28);

            g2.setColor(Color.BLACK);
            g2.drawOval(x - 14, y - 14, 28, 28);
            g2.setFont(PixelFont.get(Settings.FONT_BOMB));
            g2.drawString("B", x - 5, y + 5);
        }
    }

    private void drawExplosion(Graphics2D g2) {
        if (explosions.isEmpty()) return;

        long now = System.currentTimeMillis();
        Iterator<ExplosionEffect> it = explosions.iterator();

        while (it.hasNext()) {
            ExplosionEffect explosion = it.next();

            if (now - explosion.startTime > Settings.EXPLOSION_DURATION_MS) {
                it.remove();
                continue;
            }

            int size = Settings.EXPLOSION_SIZE;

            if (explosionGif != null && explosionGif.getIconWidth() > 0) {
                g2.drawImage(
                        explosionGif.getImage(),
                        (int) explosion.x - size / 2,
                        (int) explosion.y - size / 2,
                        size,
                        size,
                        this
                );
            }
        }
    }

    private void drawPlayer(Graphics2D g2) {
        long now = System.currentTimeMillis();
        int cx = (int) player.getX();
        int cy = (int) player.getY();
        int size = Settings.PLAYER_DRAW_SIZE;

        boolean invincible = player.isInvincible(now);
        boolean moving = up || down || left || right;
        boolean dead = player.getHealth() <= 0;
        boolean hurt = invincible && !dead;

        ImageIcon currentGif;
        if (dead) {
            currentGif = playerDeadGif;
        } else if (hurt) {
            currentGif = (playerHurtGif != null) ? playerHurtGif : playerIdleGif;
        } else if (moving) {
            currentGif = (playerMoveGif != null) ? playerMoveGif : playerIdleGif;
        } else {
            currentGif = playerIdleGif;
        }

        // Blink effect during i-frames
        // Visible 80ms, hidden 80ms
        if (invincible && !dead) {
            boolean hideFrame = ((now / 80) % 2 == 0);
            if (hideFrame) {
                return;
            }
        }

        if (currentGif != null && currentGif.getIconWidth() > 0) {
            int drawX = cx - size / 2;
            int drawY = cy - size / 2;

            if (facingLeft) {
                g2.drawImage(currentGif.getImage(), drawX + size, drawY, -size, size, this);
            } else {
                g2.drawImage(currentGif.getImage(), drawX, drawY, size, size, this);
            }
        } else {
            int r = (int) player.getRadius();
            g2.setColor(hurt ? new Color(255, 80, 80) : Settings.TEXT_PRIMARY);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(Settings.PLAYER_OUTLINE);
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            drawEnemy(g2, enemy);
        }
    }

    private void drawEnemy(Graphics2D g2, Enemy enemy) {
        int size = Settings.ENEMY_DRAW_SIZE;
        int drawX = (int) enemy.getX() - size / 2;
        int drawY = (int) enemy.getY() - size / 2;

        BufferedImage sprite = enemySprites.get(enemy.getType());

        if (sprite != null) {
            g2.drawImage(sprite, drawX, drawY, size, size, null);
        } else {
            g2.setColor(enemy.getType().getFallbackColor());
            g2.fillOval(drawX, drawY, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(PixelFont.get(Settings.FONT_ENEMY_FALLBACK));
            String text = enemy.getType().getShortText();
            FontMetrics fm = g2.getFontMetrics();
            int tx = drawX + (size - fm.stringWidth(text)) / 2;
            int ty = drawY + ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, tx, ty);
        }
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Settings.HUD_BG);
        g2.fillRoundRect(
                Settings.HUD_X,
                Settings.HUD_Y,
                Settings.HUD_W,
                Settings.HUD_H,
                Settings.HUD_ARC,
                Settings.HUD_ARC
        );

        g2.setColor(Settings.TEXT_PRIMARY);

        double elapsed = (effectiveNow() - startTime - totalPausedMs) / 1000.0;

        g2.setFont(PixelFont.get(Settings.FONT_HUD));
        g2.drawString("Player: " + playerName, 24, 38);
        g2.drawString("Difficulty: " + difficulty.getDifficulty(), 24, 62);
        g2.drawString("Score: " + score, 24, 86);
        g2.drawString(String.format("Time: %.1fs", elapsed), 220, 38);
//        g2.drawString("Prediction: " + lastPrediction, 220, 62);

        g2.setFont(PixelFont.get(Settings.FONT_STATUS));
        g2.drawString(statusText, 24, 112);
    }

    private void drawHearts(Graphics2D g2) {
        int size  = Settings.HEART_SIZE;
        int gap   = 6;
        int total = Settings.PLAYER_MAX_HEALTH;

        int startX = Settings.HUD_X + Settings.HUD_W - total * (size + gap) + gap - 4;
        int y      = Settings.HUD_Y + (Settings.HUD_H - size) / 2;

        long now = System.currentTimeMillis();
        boolean invincible = player.isInvincible(now);

        for (int i = 0; i < total; i++) {
            int x = startX + i * (size + gap);
            boolean filled = i < player.getHealth();

            if (heartImage != null) {
                if (filled) {
                    if (invincible && i == player.getHealth() - 1) {
                        float pulse = (float)(0.75 + 0.25 * Math.abs(Math.sin(now / 100.0)));
                        int ps = (int)(size * pulse);
                        int offset = (size - ps) / 2;
                        g2.drawImage(heartImage, x + offset, y + offset, ps, ps, null);
                    } else {
                        g2.drawImage(heartImage, x, y, size, size, null);
                    }
                } else {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g2.drawImage(heartImage, x, y, size, size, null);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            } else {
                g2.setColor(filled ? new Color(220, 50, 50) : new Color(80, 40, 40));
                g2.fillOval(x,          y + size / 4, size / 2, size / 2);
                g2.fillOval(x + size/2, y + size / 4, size / 2, size / 2);
                int[] px = {x, x + size / 2, x + size};
                int[] py = {y + size / 2, y + size, y + size / 2};
                g2.fillPolygon(px, py, 3);
            }
        }
    }

    private void drawFreezeHUD(Graphics2D g2) {
        long now = effectiveNow();

        int boxX = Settings.HUD_X;
        int boxY = Settings.HUD_Y + Settings.HUD_H + 8;
        int boxW = 200;
        int boxH = 52;
        int arc  = Settings.HUD_ARC;

        g2.setColor(Settings.HUD_BG);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, arc, arc);

        g2.setFont(PixelFont.get(Settings.FONT_STATUS));

        if (frozen) {
            float pulse = (float)(0.6 + 0.4 * Math.abs(Math.sin(now / 150.0)));
            g2.setColor(new Color(0, (int)(200 * pulse), (int)(255 * pulse)));
            g2.drawString("Q  FREEZE  ACTIVE", boxX + 10, boxY + 20);

            double progress = 1.0 - (double)(freezeEndTime - now) / FREEZE_DURATION_MS;
            progress = Math.min(1.0, Math.max(0.0, progress));
            int barW = boxW - 20;

            g2.setColor(new Color(30, 30, 60));
            g2.fillRoundRect(boxX + 10, boxY + 28, barW, 14, 6, 6);

            g2.setColor(new Color(0, 200, 255));
            g2.fillRoundRect(boxX + 10, boxY + 28, (int)(barW * (1.0 - progress)), 14, 6, 6);

        } else if (now < freezeCooldownEndTime) {
            double remaining = (freezeCooldownEndTime - now) / 1000.0;
            g2.setColor(new Color(160, 160, 180));
            g2.drawString(String.format("Q  FREEZE  %.1fs", remaining), boxX + 10, boxY + 20);

            double progress = 1.0 - (double)(freezeCooldownEndTime - now) / FREEZE_COOLDOWN_MS;
            progress = Math.min(1.0, Math.max(0.0, progress));
            int barW = boxW - 20;

            g2.setColor(new Color(30, 30, 60));
            g2.fillRoundRect(boxX + 10, boxY + 28, barW, 14, 6, 6);

            g2.setColor(new Color(80, 100, 160));
            g2.fillRoundRect(boxX + 10, boxY + 28, (int)(barW * progress), 14, 6, 6);

        } else {
            g2.setColor(new Color(100, 220, 255));
            g2.drawString("Q  FREEZE  READY", boxX + 10, boxY + 20);

            int barW = boxW - 20;
            g2.setColor(new Color(0, 200, 255));
            g2.fillRoundRect(boxX + 10, boxY + 28, barW, 14, 6, 6);
        }
    }

    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;

        if (countingDown) {
            g2.setFont(PixelFont.get(120f));
            String num = String.valueOf(countdownValue);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(num);

            g2.setColor(new Color(100, 180, 255, 80));
            g2.drawString(num, cx - tw / 2 + 4, cy + fm.getAscent() / 2 + 4);

            g2.setColor(new Color(180, 230, 255));
            g2.drawString(num, cx - tw / 2, cy + fm.getAscent() / 2);

            g2.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
            String sub = "GET READY";
            fm = g2.getFontMetrics();
            g2.setColor(Settings.TEXT_SECONDARY);
            g2.drawString(sub, cx - fm.stringWidth(sub) / 2, cy + 80);

        } else {
            g2.setFont(PixelFont.get(Settings.FONT_GAME_OVER));
            String title = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Settings.TEXT_PRIMARY);
            g2.drawString(title, cx - fm.stringWidth(title) / 2, cy - 80);

            int btnW = 200;
            int btnH = 52;
            int gap = 20;

            int resumeX = cx - btnW / 2;
            int resumeY = cy - btnH / 2 - gap / 2 - btnH / 2;

            int quitX = cx - btnW / 2;
            int quitY = resumeY + btnH + gap;

            resumeButtonRect = new Rectangle(resumeX, resumeY, btnW, btnH);
            quitButtonRect   = new Rectangle(quitX,   quitY,   btnW, btnH);

            g2.setColor(new Color(60, 160, 100));
            g2.fillRoundRect(resumeX, resumeY, btnW, btnH, 14, 14);
            g2.setColor(new Color(100, 220, 140));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(resumeX, resumeY, btnW, btnH, 14, 14);

            g2.setFont(PixelFont.get(Settings.FONT_LABEL));
            fm = g2.getFontMetrics();
            String resumeText = "RESUME";
            g2.setColor(Color.WHITE);
            g2.drawString(resumeText,
                    resumeX + (btnW - fm.stringWidth(resumeText)) / 2,
                    resumeY + (btnH - fm.getHeight()) / 2 + fm.getAscent());

            g2.setColor(new Color(160, 50, 50));
            g2.fillRoundRect(quitX, quitY, btnW, btnH, 14, 14);
            g2.setColor(new Color(220, 100, 100));
            g2.drawRoundRect(quitX, quitY, btnW, btnH, 14, 14);

            String quitText = "QUIT & SAVE";
            g2.setColor(Color.WHITE);
            g2.drawString(quitText,
                    quitX + (btnW - fm.stringWidth(quitText)) / 2,
                    quitY + (btnH - fm.getHeight()) / 2 + fm.getAscent());

            g2.setFont(PixelFont.get(Settings.FONT_TINY));
            g2.setColor(Settings.TEXT_SECONDARY);
            String hint = "ESC to resume";
            fm = g2.getFontMetrics();
            g2.drawString(hint, cx - fm.stringWidth(hint) / 2, quitY + btnH + 30);
        }
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(Settings.OVERLAY_BG);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Settings.TEXT_PRIMARY);
        g2.setFont(PixelFont.get(Settings.FONT_GAME_OVER));
        String t1 = "GAME OVER";
        int w1 = g2.getFontMetrics().stringWidth(t1);
        g2.drawString(t1, (WIDTH - w1) / 2, HEIGHT / 2 - 20);

        g2.setFont(PixelFont.get(Settings.FONT_FINAL_SCORE));
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
            case KeyEvent.VK_Q -> activateFreeze();
            case KeyEvent.VK_ESCAPE -> {
                if (!gameOver) {
                    if (paused && !countingDown) {
                        startCountdown();
                    } else if (!paused) {
                        activatePause();
                    }
                }
            }
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

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;
        timer.stop();
        parentFrame.endGame(score);
    }

    private static class ExplosionEffect {
        double x;
        double y;
        long startTime;

        ExplosionEffect(double x, double y, long startTime) {
            this.x = x;
            this.y = y;
            this.startTime = startTime;
        }
    }

    private void addExplosion(double x, double y) {
        explosions.add(new ExplosionEffect(x, y, System.currentTimeMillis()));
    }
}