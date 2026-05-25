package ui;

import io.FileManager;
import sound.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class SettingsWindow extends JDialog {
    private FileManager fileManager;
    private SoundManager soundManager;
    private Properties current;

    private JCheckBox soundCheckbox;
    private JSlider musicVolume, effectsVolume, fallSpeed, spawnInterval, startingLives;
    private JComboBox<String> difficultyCombo;

    public SettingsWindow(JFrame parent, FileManager fileManager, SoundManager soundManager) {
        super(parent, "Settings", true);
        this.fileManager = fileManager;
        this.soundManager = soundManager;
        this.current = fileManager.loadSettings();

        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(26, 26, 46));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        setContentPane(new JScrollPane(main));

        JLabel title = new JLabel("⚙  SETTINGS");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(150, 180, 255));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(15));

        soundCheckbox = addCheckBox(main, "Enable Sound Effects",
                Boolean.parseBoolean(current.getProperty("soundEnabled", "true")));
        main.add(Box.createVerticalStrut(10));

        musicVolume = addSlider(main, "Music Volume", 0, 100,
                intProp("musicVolume", 70));
        effectsVolume = addSlider(main, "Effects Volume", 0, 100,
                intProp("effectsVolume", 80));
        fallSpeed = addSlider(main, "Fall Speed Base (1-10)", 1, 10,
                intProp("fallSpeedBase", 4));
        spawnInterval = addSlider(main, "Spawn Interval × 100ms (5-30)", 5, 30,
                intProp("spawnInterval", 15));
        startingLives = addSlider(main, "Starting Lives (3-10)", 3, 10,
                intProp("startingLives", 5));

        main.add(Box.createVerticalStrut(10));
        difficultyCombo = addCombo(main, "Difficulty",
                new String[]{"Easy", "Normal", "Hard"},
                current.getProperty("difficulty", "Normal"));

        main.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = makeButton("Save", new Color(0, 140, 70));
        saveBtn.addActionListener(e -> saveAndClose());
        btnRow.add(saveBtn);

        JButton resetBtn = makeButton("Reset", new Color(180, 100, 0));
        resetBtn.addActionListener(e -> resetDefaults());
        btnRow.add(resetBtn);

        JButton cancelBtn = makeButton("Cancel", new Color(120, 40, 40));
        cancelBtn.addActionListener(e -> dispose());
        btnRow.add(cancelBtn);

        main.add(btnRow);
    }

    private JCheckBox addCheckBox(JPanel panel, String label, boolean checked) {
        JCheckBox cb = new JCheckBox(label, checked);
        cb.setFont(new Font("Arial", Font.PLAIN, 15));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cb);
        return cb;
    }

    private JSlider addSlider(JPanel panel, String label, int min, int max, int val) {
        JLabel lbl = new JLabel(label + ": " + val);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);

        JSlider sl = new JSlider(min, max, Math.min(max, Math.max(min, val)));
        sl.setOpaque(false);
        sl.setForeground(new Color(150, 180, 255));
        sl.setMaximumSize(new Dimension(400, 40));
        sl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sl.addChangeListener(e -> lbl.setText(label + ": " + sl.getValue()));
        panel.add(sl);
        panel.add(Box.createVerticalStrut(5));
        return sl;
    }

    private JComboBox<String> addCombo(JPanel panel, String label, String[] opts, String sel) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);

        JComboBox<String> cb = new JComboBox<>(opts);
        cb.setSelectedItem(sel);
        cb.setFont(new Font("Arial", Font.PLAIN, 14));
        cb.setMaximumSize(new Dimension(200, 36));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cb);
        return cb;
    }

    private void saveAndClose() {
        Properties p = new Properties();
        p.setProperty("soundEnabled", String.valueOf(soundCheckbox.isSelected()));
        p.setProperty("musicVolume", String.valueOf(musicVolume.getValue()));
        p.setProperty("effectsVolume", String.valueOf(effectsVolume.getValue()));
        p.setProperty("fallSpeedBase", String.valueOf(fallSpeed.getValue()));
        p.setProperty("spawnInterval", String.valueOf(spawnInterval.getValue()));
        p.setProperty("startingLives", String.valueOf(startingLives.getValue()));
        p.setProperty("difficulty", (String) difficultyCombo.getSelectedItem());
        fileManager.saveSettings(p);
        if (soundManager != null) {
            soundManager.setVolume(effectsVolume.getValue() / 100f);
            if (!soundCheckbox.isSelected()) soundManager.toggleMute();
        }
        JOptionPane.showMessageDialog(this, "Settings saved!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void resetDefaults() {
        soundCheckbox.setSelected(true);
        musicVolume.setValue(70);
        effectsVolume.setValue(80);
        fallSpeed.setValue(4);
        spawnInterval.setValue(15);
        startingLives.setValue(5);
        difficultyCombo.setSelectedItem("Normal");
    }

    private int intProp(String key, int def) {
        try { return Integer.parseInt(current.getProperty(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }

    private JButton makeButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
