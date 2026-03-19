package Project3_6481328.Menu;

import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ScoreboardFrame extends JFrame {

    private final Color bg = Settings.BG;
    private final Color panelBg = Settings.PANEL_BG;
    private final Color text = Settings.TEXT_PRIMARY;
    private final Color subText = Settings.TEXT_SECONDARY;

    private DefaultListModel<ScoreEntry> listModel;
    private JList<ScoreEntry> scoreList;

    private static final int WIDTH = Settings.GAME_WIDTH;
    private static final int HEIGHT = Settings.GAME_HEIGHT;

    public ScoreboardFrame() {
        setTitle("Draw To Survive - Scoreboard");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadScores();

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bg);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel title = new JLabel("ARCANE SCOREBOARD");
        title.setForeground(text);
        title.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));

        JLabel subtitle = new JLabel("Top hunters ranked by survival score");
        subtitle.setForeground(subText);
        subtitle.setFont(PixelFont.get(Settings.FONT_SMALL));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(bg);
        box.add(title);
        box.add(Box.createVerticalStrut(5));
        box.add(subtitle);

        header.add(box, BorderLayout.WEST);

        JLabel icon = new JLabel("✦");
        icon.setForeground(Settings.SCORE_COLOR);
        icon.setFont(new Font("Serif", Font.BOLD, 40));
        header.add(icon, BorderLayout.EAST);

        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(bg);
        center.setBorder(new EmptyBorder(10, 25, 15, 25));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(panelBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Settings.CARD_BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        listModel = new DefaultListModel<>();
        scoreList = new JList<>(listModel);
        scoreList.setCellRenderer(new ScoreboardCellRenderer());
        scoreList.setBackground(Settings.ROW_B);
        scoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(scoreList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(scrollPane, BorderLayout.CENTER);
        center.add(card, BorderLayout.CENTER);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(bg);
        footer.setBorder(new EmptyBorder(0, 25, 20, 25));

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        styleButton(refreshButton, Settings.BUTTON_BLUE);
        styleButton(closeButton, Settings.BUTTON_PURPLE);

        refreshButton.addActionListener(e -> loadScores());
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(closeButton);

        JLabel tip = new JLabel("Scores: name • score • difficulty • time");
        tip.setForeground(subText);
        tip.setFont(PixelFont.get(Settings.FONT_TINY));

        footer.add(tip, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);

        return footer;
    }

    private void loadScores() {
        listModel.clear();

        List<ScoreEntry> scores = ScoreManager.loadScoreEntries();

        if (scores.isEmpty()) {
            listModel.addElement(new ScoreEntry("No scores yet", 0, "-", "-"));
            return;
        }

        int limit = Math.min(10, scores.size());
        for (int i = 0; i < limit; i++) {
            listModel.addElement(scores.get(i));
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(PixelFont.get(Settings.FONT_SMALL));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
    }
}