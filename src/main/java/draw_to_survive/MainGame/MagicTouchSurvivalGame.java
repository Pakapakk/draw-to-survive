package draw_to_survive.MainGame;

import javax.swing.*;
import java.awt.*;

public class MagicTouchSurvivalGame extends JFrame {

    public MagicTouchSurvivalGame() {
        super("Magic Touch Survival");

        GamePanel gamePanel = new GamePanel();
        AutoPredictDrawPanel drawPanel = new AutoPredictDrawPanel(260, 260, gamePanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 700));
        rightPanel.setBackground(new Color(20, 20, 20));

        JLabel guide = new JLabel(
                "<html><div style='padding:8px'>" +
                        "<b>Controls</b><br>" +
                        "Move: WASD / Arrow Keys<br>" +
                        "Draw on the canvas to cast spell<br><br>" +
                        "<b>Bomb</b><br>" +
                        "Walk onto it to kill 5 closest symbols<br><br>" +
                        "<b>Ring Event</b><br>" +
                        "Special symbols spawn in a circle and rush inward" +
                        "</div></html>"
        );
        guide.setForeground(Color.WHITE);

        rightPanel.add(guide, BorderLayout.NORTH);
        rightPanel.add(drawPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MagicTouchSurvivalGame::new);
    }
}