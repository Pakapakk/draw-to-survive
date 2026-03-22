package Project3_6481328.MainGame;

import Project3_6481328.Menu.ScoreManager;
import Project3_6481328.Menu.WelcomeFrame;
import Project3_6481328.utils.AudioManager;
import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import java.awt.*;

public class DinoDrawSurvivalGame extends JFrame {

    private final String playerName;
    private final Difficulty selectedDifficulty;
    private final String selectedSkin;

    private final GamePanel gamePanel;
    private final AutoPredictDrawPanel drawPanel;

    public DinoDrawSurvivalGame(String playerName, Difficulty selectedDifficulty, String selectedSkin) {
        super("Draw To Survive");

        this.playerName = playerName;
        this.selectedDifficulty = selectedDifficulty;
        this.selectedSkin = (selectedSkin == null || selectedSkin.isBlank())
                ? Settings.DEFAULT_PLAYER_SKIN
                : selectedSkin;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Settings.BG);

        gamePanel = new GamePanel(this, playerName, selectedDifficulty, this.selectedSkin);
        drawPanel = new AutoPredictDrawPanel(Settings.DRAW_PANEL_SIZE, Settings.DRAW_PANEL_SIZE, gamePanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(Settings.RIGHT_PANEL_WIDTH, Settings.GAME_HEIGHT));
        rightPanel.setBackground(Settings.PANEL_BG);

        // ===== GUIDE =====
        JLabel guide = new JLabel(
                "<html><div style='padding:12px; color:white;'>" +
                        "<b>Controls</b><br>" +
                        "Move: WASD / Arrow Keys<br>" +
                        "Draw on the canvas to cast spell<br><br>" +
                        "<b>Bomb</b><br>" +
                        "Walk onto it to kill 5 closest enemies<br><br>" +
                        "<b>Freeze</b><br>" +
                        "Press Q to freeze enemies<br><br>" +
                        "<b>Pause</b><br>" +
                        "Press ESC to pause/resume<br><br>" +
                        "<b>Skin</b><br>" +
                        this.selectedSkin.toUpperCase() +
                        "</div></html>"
        );
        guide.setForeground(Settings.TEXT_PRIMARY);
        guide.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ===== DRAW SECTION (label + panel together) =====
        JPanel drawContainer = new JPanel(new BorderLayout());
        drawContainer.setBackground(Settings.GAME_BG);

        // Label
        JLabel topLabel = new JLabel("DRAW HERE!!", SwingConstants.CENTER);
        topLabel.setForeground(Settings.ACCENT);
        topLabel.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        topLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel bottomLabel = new JLabel("DRAW HERE!!", SwingConstants.CENTER);
        bottomLabel.setForeground(Settings.ACCENT);
        bottomLabel.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        bottomLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        // Add to container
        drawContainer.add(topLabel, BorderLayout.NORTH);
        drawContainer.add(drawPanel, BorderLayout.CENTER);
        drawContainer.add(bottomLabel, BorderLayout.SOUTH);

        // ===== ADD TO RIGHT PANEL =====
        rightPanel.add(guide, BorderLayout.NORTH);
        rightPanel.add(drawContainer, BorderLayout.SOUTH);

        add(gamePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        AudioManager.playMusic(Settings.SFX_GAME_MUSIC);

        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    public void endGame(int score) {
        ScoreManager.saveScore(playerName, score, selectedDifficulty.toString());
        AudioManager.stopMusic();

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
                new DinoDrawSurvivalGame(playerName, selectedDifficulty, selectedSkin);
            } else {
                new WelcomeFrame();
            }
        });
    }
}