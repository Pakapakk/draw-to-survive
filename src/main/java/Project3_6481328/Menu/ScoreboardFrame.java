package Project3_6481328.Menu;

import Project3_6481328.utils.AudioManager;
import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ScoreboardFrame extends JFrame {

    private final Color bg = Settings.BG;
    private final Color panelBg = Settings.PANEL_BG;
    private final Color text = Settings.TEXT_PRIMARY;
    private final Color subText = Settings.TEXT_SECONDARY;
    private final Color accent = Settings.ACCENT;

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
        header.setBorder(new EmptyBorder(24, 24, 10, 24));

        JLabel title = new JLabel("SCOREBOARD");
        title.setForeground(text);
        title.setFont(PixelFont.get(Settings.FONT_TITLE_BIG));

        JLabel subtitle = new JLabel("TOP RANKS BY SURVIVAL SCORE");
        subtitle.setForeground(subText);
        subtitle.setFont(PixelFont.get(Settings.FONT_SMALL));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(bg);
        box.add(title);
        box.add(Box.createVerticalStrut(6));
        box.add(subtitle);

        header.add(box, BorderLayout.WEST);

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
        footer.setBorder(new EmptyBorder(0, 24, 22, 24));

        JButton refreshButton = new JButton("REFRESH");
        JButton closeButton = new JButton("CLOSE");

        styleInteractiveButton(refreshButton);
        styleInteractiveButton(closeButton);

        refreshButton.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            loadScores();
        });

        closeButton.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(closeButton);

//        JLabel tip = new JLabel("SCORES: NAME • SCORE • DIFFICULTY • TIME");
//        tip.setForeground(subText);
//        tip.setFont(PixelFont.get(Settings.FONT_TINY));

//        footer.add(tip, BorderLayout.WEST);
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

    private void styleInteractiveButton(JButton button) {
        Color normalBg = accent;
        Color hoverBg = new Color(
                Math.min(accent.getRed() + 20, 255),
                Math.min(accent.getGreen() + 20, 255),
                Math.min(accent.getBlue() + 20, 255)
        );

        button.setBackground(normalBg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(PixelFont.get(Settings.FONT_INPUT));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalBg, 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 2),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBg, 2),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }
        });
    }
}