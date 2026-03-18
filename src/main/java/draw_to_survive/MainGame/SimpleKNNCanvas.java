package draw_to_survive.MainGame;

import draw_to_survive.knn.PredictionResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SimpleKNNCanvas extends JFrame {

    private final DrawPanel drawPanel;
    private final JLabel resultLabel;
    private final SpellRecognizer recognizer;

    // Auto-predict control
    private volatile long lastDrawTime = 0;
    private volatile boolean hasPendingStroke = false;
    private volatile boolean isDrawing = false;

    private static final long AUTO_PREDICT_DELAY_MS = 300; // adjust if needed

    public SimpleKNNCanvas() {
        super("Magic Touch - Spell Test");

        recognizer = new SpellRecognizer();

        drawPanel = new DrawPanel(320, 320);
        resultLabel = new JLabel("Prediction: -");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));

//        JButton clearButton = new JButton("Clear");
//        clearButton.addActionListener(e -> {
//            drawPanel.clear();
//            resultLabel.setText("Prediction: -");
//            hasPendingStroke = false;
//            isDrawing = false;
//        });

        JPanel bottomPanel = new JPanel();
//        bottomPanel.add(clearButton);
        bottomPanel.add(resultLabel);

        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Start background watcher thread
        Thread autoPredictThread = new Thread(new AutoPredictRunnable());
        autoPredictThread.setDaemon(true);
        autoPredictThread.start();
    }

    private void predictDrawingAndClear() {
        BufferedImage drawing = drawPanel.getImage();

        PredictionResult result = recognizer.recognizeDetailed(drawing);

        resultLabel.setText(
                "Prediction: " + result.getLabel() +
                        " | distance=" + String.format("%.3f", result.getDistance())
        );

        drawPanel.clear();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleKNNCanvas::new);
    }

    private class AutoPredictRunnable implements Runnable {
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
                        if (!drawPanel.isBlank()) {
                            predictDrawingAndClear();
                        }
                    });
                }
            }
        }
    }

    private class DrawPanel extends JPanel {

        private final BufferedImage image;
        private final Graphics2D g2d;
        private int lastX;
        private int lastY;

        public DrawPanel(int width, int height) {
            setPreferredSize(new Dimension(width, height));

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2d = image.createGraphics();

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastX = e.getX();
                    lastY = e.getY();

                    isDrawing = true;
                    hasPendingStroke = true;
                    lastDrawTime = System.currentTimeMillis();
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

                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isDrawing = false;
                    hasPendingStroke = true;
                    lastDrawTime = System.currentTimeMillis();
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        public BufferedImage getImage() {
            BufferedImage copy = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    image.getType()
            );
            copy.setData(image.getData());
            return copy;
        }

        public void clear() {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            repaint();
        }

        public boolean isBlank() {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (image.getRGB(x, y) != Color.WHITE.getRGB()) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }
}