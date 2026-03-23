package Project3_6481328.MainGame;

import Project3_6481328.knn.PredictionResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class AutoPredictDrawPanel extends JPanel {

    private final BufferedImage image;
    private final Graphics2D g2d;

    private int lastX, lastY;

    //private final JLabel resultLabel;
    private final SpellRecognizer recognizer;
    private final GamePanel gamePanel;

    // === SAME LOGIC AS YOUR TEST ===
    private volatile long lastDrawTime = 0;
    private volatile boolean hasPendingStroke = false;
    private volatile boolean isDrawing = false;

    private static final long AUTO_PREDICT_DELAY_MS = 0;

    public AutoPredictDrawPanel(int width, int height, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.recognizer = new SpellRecognizer();

        setLayout(new BorderLayout());
        setOpaque(false);

//        resultLabel = new JLabel("Prediction: -", SwingConstants.CENTER);
//        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        resultLabel.setForeground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(20, 20, 20));
        //top.add(resultLabel, BorderLayout.CENTER);

        DrawSurface surface = new DrawSurface(width, height);

        add(top, BorderLayout.NORTH);
        add(surface, BorderLayout.CENTER);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();

        clear();

        // =========================
        // MOUSE HANDLER
        // =========================
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();

                // draw dot for quick taps
                g2d.fillOval(lastX - 6, lastY - 6, 12, 12);

                isDrawing = true;
                hasPendingStroke = true;
                lastDrawTime = System.currentTimeMillis();

                surface.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                g2d.drawLine(lastX, lastY, x, y);

                lastX = x;
                lastY = y;

                isDrawing = true;
                hasPendingStroke = true;
                lastDrawTime = System.currentTimeMillis();

                surface.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDrawing = false;
                hasPendingStroke = true;
                lastDrawTime = System.currentTimeMillis();
            }
        };

        surface.addMouseListener(mouseHandler);
        surface.addMouseMotionListener(mouseHandler);

        // =========================
        // BACKGROUND THREAD
        // =========================
        Thread watcher = new Thread(new AutoPredictRunnable(surface));
        watcher.setDaemon(true);
        watcher.start();
    }

    // =========================
    // CORE LOGIC
    // =========================

    private class AutoPredictRunnable implements Runnable {
        private final JComponent repaintTarget;

        public AutoPredictRunnable(JComponent repaintTarget) {
            this.repaintTarget = repaintTarget;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                long now = System.currentTimeMillis();

                if (hasPendingStroke && !isDrawing &&
                        now - lastDrawTime >= AUTO_PREDICT_DELAY_MS) {

                    hasPendingStroke = false;

                    SwingUtilities.invokeLater(() -> {
                        if (!isBlank()) {
                            predictAndSendToGame();
                            clear();
                            repaintTarget.repaint();
                        }
                    });
                }
            }
        }
    }

    private void predictAndSendToGame() {
        BufferedImage copy = getCopy();

        PredictionResult result = recognizer.recognizeDetailed(copy);
        String label = result.getLabel();

//        resultLabel.setText(
//                "Prediction: " + label +
//                        " | d=" + String.format("%.3f", result.getDistance())
//        );

        gamePanel.castSpell(label);
    }

    // =========================
    // UTIL
    // =========================

    private void clear() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    private boolean isBlank() {
        int white = Color.WHITE.getRGB();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) != white) {
                    return false;
                }
            }
        }
        return true;
    }

    private BufferedImage getCopy() {
        BufferedImage copy = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                image.getType()
        );
        copy.setData(image.getData());
        return copy;
    }

    private class DrawSurface extends JPanel {
        public DrawSurface(int w, int h) {
            setPreferredSize(new Dimension(w, h));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }
}