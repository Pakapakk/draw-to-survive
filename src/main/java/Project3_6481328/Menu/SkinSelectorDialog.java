package Project3_6481328.Menu;

import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class SkinSelectorDialog extends JDialog {

    private String selectedSkin;

    public SkinSelectorDialog(JFrame parent, String currentSkin) {
        super(parent, "Select Skin", true);
        this.selectedSkin = currentSkin;

        setSize(520, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(Settings.BG);
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(main);

        JLabel title = new JLabel("SELECT PLAYER SKIN", SwingConstants.CENTER);
        title.setForeground(Settings.TEXT_PRIMARY);
        title.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        main.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setBackground(Settings.BG);

        for (String skin : Settings.AVAILABLE_PLAYER_SKINS) {
            grid.add(createSkinButton(skin));
        }

        main.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        bottom.setBackground(Settings.BG);

        JButton okBtn = new JButton("CONFIRM");
        JButton cancelBtn = new JButton("CANCEL");

        styleButton(okBtn);
        styleButton(cancelBtn);

        okBtn.addActionListener(e -> dispose());
        cancelBtn.addActionListener(e -> {
            selectedSkin = currentSkin;
            dispose();
        });

        bottom.add(okBtn);
        bottom.add(cancelBtn);

        main.add(bottom, BorderLayout.SOUTH);
    }

    private JButton createSkinButton(String skinName) {
        JButton button = new JButton(capitalize(skinName));
        button.setLayout(new BorderLayout());
        button.setBackground(Settings.INPUT_BG);
        button.setForeground(Settings.TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setFont(PixelFont.get(Settings.FONT_SMALL));

        String previewPath = Settings.getPlayerIdlePath(skinName);
        ImageIcon preview = null;

        File f = new File(previewPath);
        if (f.exists()) {
            preview = new ImageIcon(previewPath);
        }

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setOpaque(false);

        if (preview != null && preview.getIconWidth() > 0) {
            Image scaled = preview.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setText("NO PREVIEW");
            imageLabel.setForeground(Settings.TEXT_SECONDARY);
        }

        JLabel nameLabel = new JLabel(capitalize(skinName), SwingConstants.CENTER);
        nameLabel.setForeground(Settings.TEXT_PRIMARY);
        nameLabel.setFont(PixelFont.get(Settings.FONT_SMALL));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(imageLabel, BorderLayout.CENTER);
        content.add(nameLabel, BorderLayout.SOUTH);

        button.add(content, BorderLayout.CENTER);
        updateButtonBorder(button, skinName);

        button.addActionListener(e -> {
            selectedSkin = skinName;

            Container parent = button.getParent();
            for (Component c : parent.getComponents()) {
                if (c instanceof JButton b) {
                    updateButtonBorder(b, b.getText().toLowerCase());
                }
            }

            updateButtonBorder(button, skinName);
        });

        return button;
    }

    private void updateButtonBorder(JButton button, String skinName) {
        if (skinName.equalsIgnoreCase(selectedSkin)) {
            button.setBorder(BorderFactory.createLineBorder(Settings.ACCENT, 3));
        } else {
            button.setBorder(BorderFactory.createLineBorder(Settings.CARD_BORDER, 2));
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(Settings.ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(PixelFont.get(Settings.FONT_INPUT));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public String getSelectedSkin() {
        return selectedSkin;
    }
}