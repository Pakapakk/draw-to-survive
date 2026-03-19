package Project3_6481328.Menu;

import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ScoreboardCellRenderer extends JPanel implements ListCellRenderer<ScoreEntry> {

    private final JLabel rankLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel scoreLabel = new JLabel();
    private final JLabel metaLabel = new JLabel();

    public ScoreboardCellRenderer() {
        setLayout(new BorderLayout(12, 6));
        setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        rankLabel.setFont(PixelFont.get(Settings.FONT_INPUT));
        rankLabel.setForeground(Settings.RANK_COLOR);

        nameLabel.setFont(PixelFont.get(Settings.FONT_INPUT));
        nameLabel.setForeground(Settings.TEXT_PRIMARY);

        metaLabel.setFont(PixelFont.get(Settings.FONT_TINY));

        left.add(rankLabel);
        left.add(Box.createVerticalStrut(4));
        left.add(nameLabel);
        left.add(Box.createVerticalStrut(4));
        left.add(metaLabel);

        scoreLabel.setFont(PixelFont.get(Settings.FONT_SCORE_BIG));
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        add(left, BorderLayout.CENTER);
        add(scoreLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends ScoreEntry> list,
            ScoreEntry value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        rankLabel.setText("#" + (index + 1));
        nameLabel.setText(value.getName());
        scoreLabel.setText(String.valueOf(value.getScore()));

        Color diffColor;
        switch (value.getDifficulty()) {
            case "Easy" -> diffColor = Settings.EASY_COLOR;
            case "Hard" -> diffColor = Settings.HARD_COLOR;
            default -> diffColor = Settings.NORMAL_COLOR;
        }

        metaLabel.setText(value.getDifficulty() + " • " + value.getDateTime());
        metaLabel.setForeground(diffColor);

        scoreLabel.setForeground(Settings.SCORE_COLOR);

        if (isSelected) {
            setBackground(Settings.SELECTED_ROW);
        } else {
            setBackground(index % 2 == 0 ? Settings.ROW_A : Settings.ROW_B);
        }

        return this;
    }
}