package Project3_6481328.Menu;

import Project3_6481328.MainGame.Difficulty;
import Project3_6481328.MainGame.DinoDrawSurvivalGame;
import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;
import Project3_6481328.utils.AudioManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

public class WelcomeFrame extends JFrame {

    private JTextField nameField;
    private JComboBox<String> difficultyBox;
    private JLabel difficultyInfoLabel;

    private String selectedSkin = Settings.DEFAULT_PLAYER_SKIN;

    private final Map<String, JButton> skinButtons = new LinkedHashMap<>();
    private JLabel selectedSkinLabel;

    private final Color bg = Settings.BG;
    private final Color panelBg = Settings.PANEL_BG;
    private final Color inputBg = Settings.INPUT_BG;
    private final Color text = Settings.TEXT_PRIMARY;
    private final Color accent = Settings.ACCENT;

    private static final int WIDTH = Settings.GAME_WIDTH;
    private static final int HEIGHT = Settings.GAME_HEIGHT;

    public WelcomeFrame() {
        setTitle("Draw To Survive");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        AudioManager.playMusic(Settings.SFX_MENU_MUSIC);

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(24, 24, 10, 24));

        JLabel title = new JLabel("DRAW TO SURVIVE");
        title.setFont(PixelFont.get(Settings.FONT_TITLE_BIG));
        title.setForeground(text);

        JButton exitBtn = new JButton("EXIT GAME");
        styleInteractiveButton(exitBtn);
        exitBtn.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            dispose();
            System.exit(0);
        });

        panel.add(title, BorderLayout.WEST);
        panel.add(exitBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 24));
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(buildLeft());
        panel.add(buildRightSkinPanel());

        return panel;
    }

    private JPanel buildLeft() {
        JPanel panel = createCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(label("PLAYER NAME"));
        nameField = new JTextField(15);
        styleTextField(nameField);
        panel.add(nameField);

        panel.add(Box.createVerticalStrut(28));

        panel.add(label("DIFFICULTY"));
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Normal", "Hard", "Brutal", "Impossible"});
        styleComboBox(difficultyBox);
        difficultyBox.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            updateDifficultyInfo();
        });

        difficultyInfoLabel = new JLabel();
        difficultyInfoLabel.setFont(PixelFont.get(Settings.FONT_INPUT));
        difficultyInfoLabel.setBorder(new EmptyBorder(10, 2, 0, 0));
        difficultyInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(difficultyBox);
        panel.add(difficultyInfoLabel);

        updateDifficultyInfo();

        panel.add(Box.createVerticalStrut(28));

        panel.add(label("CURRENT SKIN"));
        selectedSkinLabel = new JLabel(selectedSkin.toUpperCase());
        selectedSkinLabel.setForeground(Settings.ACCENT);
        selectedSkinLabel.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        selectedSkinLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedSkinLabel.setBorder(new EmptyBorder(6, 2, 0, 0));
        panel.add(selectedSkinLabel);

        panel.add(Box.createVerticalGlue());

        // Volume sliders
        panel.add(label("MUSIC VOLUME"));
        JSlider musicSlider = createVolumeSlider((int)(AudioManager.getMusicVolume() * 100));
        musicSlider.addChangeListener(e ->
                AudioManager.setMusicVolume(musicSlider.getValue() / 100f));
        panel.add(musicSlider);

        panel.add(Box.createVerticalStrut(14));

        panel.add(label("SFX VOLUME"));
        JSlider sfxSlider = createVolumeSlider((int)(AudioManager.getSfxVolume() * 100));
        sfxSlider.addChangeListener(e -> {
            AudioManager.setSfxVolume(sfxSlider.getValue() / 100f);
            if (!sfxSlider.getValueIsAdjusting()) {
                AudioManager.playSfx(Settings.SFX_BUTTON);
            }
        });
        panel.add(sfxSlider);

        panel.add(Box.createVerticalStrut(14));

        JButton instructionsBtn = new JButton("GAME INSTRUCTIONS");
        styleInteractiveButton(instructionsBtn);
        instructionsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsBtn.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            showInstructionsPopup();
        });
        panel.add(instructionsBtn);

        return panel;
    }

    private JPanel buildRightSkinPanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("SELECT PLAYER SKIN", SwingConstants.CENTER);
        title.setForeground(text);
        title.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setBackground(panelBg);

        skinButtons.clear();
        for (String skin : Settings.AVAILABLE_PLAYER_SKINS) {
            JButton skinButton = createSkinCardButton(skin);
            skinButtons.put(skin, skinButton);
            grid.add(skinButton);
        }

        refreshSkinSelectionUI();

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(panelBg);
        scrollPane.setBackground(panelBg);

        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel footer = new JLabel("CLICK A SKIN TO APPLY IT IMMEDIATELY", SwingConstants.CENTER);
        footer.setForeground(Settings.TEXT_SECONDARY);
        footer.setFont(PixelFont.get(Settings.FONT_SMALL));
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createSkinCardButton(String skinName) {
        JButton button = new JButton(capitalize(skinName));
        button.setFocusPainted(false);
        button.setBackground(inputBg);
        button.setForeground(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setIconTextGap(6);
        button.setFont(PixelFont.get(Settings.FONT_SMALL));
        button.setPreferredSize(new Dimension(125, 105));

        String previewPath = Settings.getPlayerIdlePath(skinName);

        ImageIcon staticPreview = loadStaticSkinPreview(previewPath, 42, 42);
        ImageIcon animatedPreview = loadAnimatedSkinPreview(previewPath, 42, 42);

        if (staticPreview != null) {
            button.setIcon(staticPreview);
        } else {
            System.out.println("Missing static skin preview: " + previewPath);
        }

        button.addActionListener(e -> {
            selectedSkin = skinName;
            selectedSkinLabel.setText(selectedSkin.toUpperCase());
            AudioManager.playSfx(Settings.SFX_BUTTON);
            refreshSkinSelectionUI();
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!skinName.equalsIgnoreCase(selectedSkin)) {
                    button.setBackground(new Color(34, 40, 58));
                    button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }

                if (animatedPreview != null) {
                    button.setIcon(animatedPreview);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (staticPreview != null) {
                    button.setIcon(staticPreview);
                }
                refreshSkinSelectionUI();
            }
        });

        return button;
    }

    private void refreshSkinSelectionUI() {
        for (Map.Entry<String, JButton> entry : skinButtons.entrySet()) {
            String skin = entry.getKey();
            JButton button = entry.getValue();

            if (skin.equalsIgnoreCase(selectedSkin)) {
                button.setBorder(BorderFactory.createLineBorder(Settings.ACCENT, 3));
                button.setBackground(new Color(40, 32, 68));
            } else {
                button.setBorder(BorderFactory.createLineBorder(Settings.CARD_BORDER, 2));
                button.setBackground(inputBg);
            }
        }
    }

    private void showInstructionsPopup() {
        JDialog dialog = new JDialog(this, "Game Instructions", true);
        dialog.setSize(560, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(Settings.BG);
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        dialog.setContentPane(main);

        JLabel title = new JLabel("HOW TO PLAY", SwingConstants.CENTER);
        title.setForeground(Settings.TEXT_PRIMARY);
        title.setFont(PixelFont.get(Settings.FONT_TITLE_MEDIUM));
        main.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea(
                "CONTROLS:\n\n" +
                        "- MOVE USING WASD OR ARROW KEYS\n" +
                        "- PRESS ESC TO PAUSE / RESUME\n" +
                        "- PRESS Q TO FREEZE ALL ENEMIES FOR 1 SECOND\n\n" +
                        "GAMEPLAY:\n\n" +
                        "- DRAW THE CORRECT SYMBOL TO DESTROY MATCHING ENEMIES\n" +
                        "- PICK UP BOMBS TO DESTROY THE 5 CLOSEST ENEMIES\n" +
                        "- WATCH OUT FOR RING ATTACKS\n" +
                        "- SURVIVE AS LONG AS POSSIBLE\n" +
                        "- GET THE HIGHEST SCORE YOU CAN"
        );
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Settings.PANEL_BG);
        area.setForeground(Settings.TEXT_PRIMARY);
        area.setFont(PixelFont.get(Settings.FONT_INPUT));
        area.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(Settings.CARD_BORDER, 2));
        scrollPane.getViewport().setBackground(Settings.PANEL_BG);

        main.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Settings.BG);

        JButton closeBtn = new JButton("CLOSE");
        styleInteractiveButton(closeBtn);
        closeBtn.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            dialog.dispose();
        });

        bottom.add(closeBtn);
        main.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(10, 10, 22, 10));

        JButton startBtn = new JButton("START GAME");
        JButton scoreBtn = new JButton("SCOREBOARD");

        styleInteractiveButton(startBtn);
        styleInteractiveButton(scoreBtn);

        startBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ENTER YOUR NAME!");
                return;
            }

            AudioManager.playSfx(Settings.SFX_BUTTON);
            AudioManager.stopMusic();

            Difficulty selectedDifficulty = getSelectedDifficulty();
            new DinoDrawSurvivalGame(name, selectedDifficulty, selectedSkin);
            dispose();
        });

        scoreBtn.addActionListener(e -> {
            AudioManager.playSfx(Settings.SFX_BUTTON);
            new ScoreboardFrame();
        });

        panel.add(startBtn);
        panel.add(Box.createHorizontalStrut(14));
        panel.add(scoreBtn);

        return panel;
    }

    private void updateDifficultyInfo() {
        String selected = (String) difficultyBox.getSelectedItem();

        if ("Easy".equals(selected)) {
            difficultyInfoLabel.setText("RELAXED • MORE BOMBS • SLOWER ENEMIES");
            difficultyInfoLabel.setForeground(Settings.EASY_COLOR);
        } else if ("Hard".equals(selected)) {
            difficultyInfoLabel.setText("FAST ENEMIES • LESS HELP • HIGH INTENSITY");
            difficultyInfoLabel.setForeground(Settings.HARD_COLOR);
        } else if ("Brutal".equals(selected)) {
            difficultyInfoLabel.setText("RELENTLESS • RARE BOMBS • VERY FAST");
            difficultyInfoLabel.setForeground(Settings.BRUTAL_COLOR);
        } else if ("Impossible".equals(selected)) {
            difficultyInfoLabel.setText("OVERWHELMING • GOOD LUCK");
            difficultyInfoLabel.setForeground(Settings.IMPOSSIBLE_COLOR);
        } else {
            difficultyInfoLabel.setText("BALANCED • RECOMMENDED");
            difficultyInfoLabel.setForeground(Settings.NORMAL_COLOR);
        }
    }

    private Difficulty getSelectedDifficulty() {
        String selected = (String) difficultyBox.getSelectedItem();

        if ("Easy".equalsIgnoreCase(selected)) {
            return Difficulty.EASY;
        } else if ("Hard".equalsIgnoreCase(selected)) {
            return Difficulty.HARD;
        } else if ("Brutal".equalsIgnoreCase(selected)) {
            return Difficulty.BRUTAL;
        } else if ("Impossible".equalsIgnoreCase(selected)) {
            return Difficulty.IMPOSSIBLE;
        } else {
            return Difficulty.NORMAL;
        }
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(panelBg);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        return panel;
    }

    private JLabel label(String textValue) {
        JLabel label = new JLabel(textValue);
        label.setForeground(text);
        label.setFont(PixelFont.get(Settings.FONT_LABEL));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, Settings.TEXTFIELD_HEIGHT));
        field.setBackground(inputBg);
        field.setForeground(text);
        field.setCaretColor(text);
        field.setFont(PixelFont.get(Settings.FONT_INPUT));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Settings.INPUT_BORDER, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Settings.COMBOBOX_HEIGHT));
        comboBox.setFont(PixelFont.get(Settings.FONT_INPUT));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(Color.BLACK);
        comboBox.setOpaque(true);
        comboBox.setFocusable(false);
        comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        comboBox.setBorder(BorderFactory.createLineBorder(Settings.INPUT_BORDER, 2));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                label.setFont(PixelFont.get(Settings.FONT_INPUT));
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(6, 10, 6, 10));

                // index == -1 means the closed/selected display
                if (index == -1) {
                    label.setBackground(Color.BLACK);
                    label.setForeground(Color.WHITE);
                } else if (isSelected) {
                    label.setBackground(Settings.ACCENT);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Settings.INPUT_BG);
                    label.setForeground(Settings.TEXT_PRIMARY);
                }

                return label;
            }
        });

        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Force the selected-display area to always be your dark color
                g.setColor(Color.BLACK);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int w = getWidth();
                        int h = getHeight();

                        int size = 6;

                        int[] x = {
                                w / 2 - size,
                                w / 2 + size,
                                w / 2
                        };

                        int[] y = {
                                h / 2 - 2,
                                h / 2 - 2,
                                h / 2 + size
                        };

                        g2.setColor(Color.WHITE);
                        g2.fillPolygon(x, y, 3);

                        g2.dispose();
                    }
                };

                button.setBackground(Color.BLACK);
                button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Settings.INPUT_BORDER));
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                return button;
            }
        });
    }

    private JSlider createVolumeSlider(int initialValue) {
        JSlider slider = new JSlider(0, 100, initialValue);
        slider.setBackground(panelBg);
        slider.setForeground(text);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setFocusable(false);
        slider.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        Dimension sliderSize = new Dimension(Integer.MAX_VALUE, 32);
        slider.setMaximumSize(sliderSize);
        slider.setPreferredSize(sliderSize);
        slider.setMinimumSize(new Dimension(120, 32));

        slider.setPaintTrack(true);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(false);
        slider.setOpaque(true);
        slider.setDoubleBuffered(true);

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            private final int TRACK_HEIGHT = 6;
            private final int THUMB_SIZE = 16;

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(panelBg);
                g2.fillRect(0, 0, c.getWidth(), c.getHeight());
                super.paint(g2, c);
                g2.dispose();
            }

            @Override
            public void paintFocus(Graphics g) {
            }

            @Override
            public void paintTicks(Graphics g) {
            }

            @Override
            public void paintLabels(Graphics g) {
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(THUMB_SIZE, THUMB_SIZE);
            }

            @Override
            public void setThumbLocation(int x, int y) {
                Rectangle oldRect = new Rectangle(thumbRect);
                super.setThumbLocation(x, y);

                Rectangle union = oldRect.union(thumbRect);
                slider.repaint(union.x - 4, union.y - 4, union.width + 8, union.height + 8);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int trackX = trackRect.x;
                int trackY = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
                int trackW = trackRect.width;
                int arc = TRACK_HEIGHT;

                g2.setColor(Settings.INPUT_BG);
                g2.fillRoundRect(trackX, trackY, trackW, TRACK_HEIGHT, arc, arc);

                int thumbCenterX = thumbRect.x + thumbRect.width / 2;
                int fillW = Math.max(0, thumbCenterX - trackX);

                g2.setColor(Settings.ACCENT);
                g2.fillRoundRect(trackX, trackY, fillW, TRACK_HEIGHT, arc, arc);

                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int x = thumbRect.x;
                int y = thumbRect.y;
                int w = thumbRect.width;
                int h = thumbRect.height;

                g2.setColor(Settings.ACCENT);
                g2.fillOval(x, y, w, h);

                g2.setColor(Settings.TEXT_PRIMARY);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, w, h);

                g2.dispose();
            }
        });

        return slider;
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
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
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
                        BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBg, 2),
                        BorderFactory.createEmptyBorder(10, 18, 10, 18)
                ));
            }
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private ImageIcon loadSkinPreview(String path) {
        try {
            File file = new File(path);
            System.out.println("Trying preview file path: " + path);

            if (!file.exists()) {
                System.out.println("File does not exist: " + path);
                return null;
            }

            Image image = javax.imageio.ImageIO.read(file);
            if (image == null) {
                System.out.println("ImageIO could not read: " + path);
                return null;
            }

            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon loadStaticSkinPreview(String path, int width, int height) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }

            Image image = javax.imageio.ImageIO.read(file);
            if (image == null) {
                return null;
            }

            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon loadAnimatedSkinPreview(String path, int width, int height) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }

            ImageIcon gifIcon = new ImageIcon(path);
            if (gifIcon.getIconWidth() <= 0 || gifIcon.getIconHeight() <= 0) {
                return null;
            }

            Image scaled = gifIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}