package Project3_6481328.Menu;

import Project3_6481328.MainGame.Difficulty;
import Project3_6481328.MainGame.MagicTouchSurvivalGame;
import Project3_6481328.utils.PixelFont;
import Project3_6481328.utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WelcomeFrame extends JFrame {

    private JTextField nameField;
    private JTextArea instructionArea;
    private JComboBox<String> difficultyBox;
    private JLabel difficultyInfoLabel;

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

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(30, 0, 10, 0));

        JLabel title = new JLabel("DRAW TO SURVIVE");
        title.setFont(PixelFont.get(Settings.FONT_TITLE_BIG));
        title.setForeground(text);

        panel.add(title);
        return panel;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 24));
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(buildLeft());
        panel.add(buildRight());

        return panel;
    }

    private JPanel buildLeft() {
        JPanel panel = createCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(label("PLAYER NAME"));
        nameField = new JTextField(15);
        styleTextField(nameField);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    setTitle("Draw To Survive");
                } else {
                    setTitle("Draw To Survive - " + name);
                }
            }
        });

        panel.add(nameField);
        panel.add(Box.createVerticalStrut(28));

        panel.add(label("DIFFICULTY"));
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Normal", "Hard", "Brutal", "Impossible"});
        styleComboBox(difficultyBox);
        difficultyBox.addActionListener(e -> updateDifficultyInfo());

        difficultyInfoLabel = new JLabel();
        difficultyInfoLabel.setFont(PixelFont.get(Settings.FONT_INPUT));
        difficultyInfoLabel.setBorder(new EmptyBorder(10, 2, 0, 0));
        difficultyInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(difficultyBox);
        panel.add(difficultyInfoLabel);

        updateDifficultyInfo();

        return panel;
    }

    private JPanel buildRight() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout());

        instructionArea = new JTextArea(
                "HOW TO PLAY:\n\n" +
                        "- MOVE USING WASD OR ARROW KEYS\n\n" +
                        "- DRAW THE CORRECT SYMBOL TO DESTROY MATCHING ENEMIES\n\n" +
                        "- PICK UP BOMBS TO DESTROY THE 5 CLOSEST ENEMIES\n\n" +
                        "- WATCH OUT FOR RING ATTACKS\n\n" +
                        "- SURVIVE AS LONG AS POSSIBLE AND GET A HIGH SCORE"
        );
        instructionArea.setEditable(false);
        instructionArea.setLineWrap(true);
        instructionArea.setWrapStyleWord(true);
        instructionArea.setBackground(panelBg);
        instructionArea.setForeground(text);
        instructionArea.setFont(PixelFont.get(Settings.FONT_INPUT));
        instructionArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(instructionArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(panelBg);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(10, 10, 22, 10));

        JButton startBtn = new JButton("START GAME");
        JButton scoreBtn = new JButton("SCOREBOARD");

        styleButton(startBtn);
        styleButton(scoreBtn);

        startBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ENTER YOUR NAME!");
                return;
            }

            Difficulty selectedDifficulty = getSelectedDifficulty();
            new MagicTouchSurvivalGame(name, selectedDifficulty);
            dispose();
        });

        scoreBtn.addActionListener(e -> new ScoreboardFrame());

        panel.add(startBtn);
        panel.add(Box.createHorizontalStrut(14));
        panel.add(scoreBtn);
        panel.add(Box.createHorizontalStrut(24));

        JLabel creditLabel = new JLabel("PAKAPAK TANADOL JIMIN STEVEN");
        creditLabel.setForeground(text);
        creditLabel.setFont(PixelFont.get(Settings.FONT_SMALL));
        panel.add(creditLabel);

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
        comboBox.setBackground(inputBg);
        comboBox.setForeground(text);
        comboBox.setFont(PixelFont.get(Settings.FONT_INPUT));
        comboBox.setBorder(BorderFactory.createLineBorder(Settings.INPUT_BORDER, 2));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleButton(JButton button) {
        button.setBackground(accent);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(PixelFont.get(Settings.FONT_INPUT));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    }
}