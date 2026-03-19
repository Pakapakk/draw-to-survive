package Project3_6481328.MainGame;

import Project3_6481328.Menu.ScoreManager;
import Project3_6481328.Menu.WelcomeFrame;
import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import java.awt.*;

public class MagicTouchSurvivalGame extends JFrame {

    private final Difficulty selectedDifficulty;
    private final String playerName;

    public MagicTouchSurvivalGame(String playerName, Difficulty selectedDifficulty) {
        super("Magic Touch Survival");

        this.playerName = playerName;
        this.selectedDifficulty = selectedDifficulty;

        GamePanel gamePanel = new GamePanel(this, playerName, selectedDifficulty);
        AutoPredictDrawPanel drawPanel = new AutoPredictDrawPanel(Settings.DRAW_PANEL_SIZE, Settings.DRAW_PANEL_SIZE, gamePanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(Settings.RIGHT_PANEL_WIDTH, Settings.GAME_HEIGHT));
        rightPanel.setBackground(Settings.INPUT_BG);

        JLabel guide = new JLabel(
                "<html><div style='padding:8px'>" +
                        "<b>Controls</b><br>" +
                        "Move: WASD / Arrow Keys<br>" +
                        "Draw on the canvas to cast spell<br><br>" +
                        "<b>Bomb</b><br>" +
                        "Walk onto it to kill 5 closest enemies<br><br>" +
                        "<b>Difficulty</b><br>" +
                        selectedDifficulty +
                        "</div></html>"
        );
        guide.setForeground(Color.WHITE);
        guide.setFont(PixelFont.get(Settings.FONT_SMALL));

        rightPanel.add(guide, BorderLayout.NORTH);
        rightPanel.add(drawPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void endGame(int score) {
        ScoreManager.saveScore(playerName, score, selectedDifficulty.toString());

        SwingUtilities.invokeLater(() -> {
            String message = """
                Game Over!

                Player: %s
                Difficulty: %s
                Score: %d
                """.formatted(playerName, selectedDifficulty, score);

            String[] options = {"Restart", "Main Menu"};

            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            dispose();

            if (choice == 0) {
                new MagicTouchSurvivalGame(playerName, selectedDifficulty);
            } else {
                new WelcomeFrame();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MagicTouchSurvivalGame("Player", Difficulty.NORMAL)
        );
    }
}