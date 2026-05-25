package ui;

import io.FileManager;
import dsa.MyArrayList;
import models.HighScoreEntry;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class HighScoreWindow extends JDialog {
    private FileManager fileManager;
    private DefaultTableModel tableModel;

    public HighScoreWindow(JFrame parent, FileManager fileManager) {
        super(parent, "High Scores", true);
        this.fileManager = fileManager;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(26, 26, 46));
        setContentPane(main);

        JLabel title = new JLabel("🏆  HIGH SCORES  🏆", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(255, 200, 0));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));
        main.add(title, BorderLayout.NORTH);

        String[] cols = {"Rank", "Player", "Score", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (row == 0) c.setForeground(new Color(255, 215, 0));
                else if (row == 1) c.setForeground(new Color(192, 192, 192));
                else if (row == 2) c.setForeground(new Color(205, 127, 50));
                else c.setForeground(Color.WHITE);
                c.setBackground(row % 2 == 0 ? new Color(40, 40, 70) : new Color(30, 30, 55));
                return c;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(36);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(50, 50, 100));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(80, 80, 150));
        table.setGridColor(new Color(60, 60, 100));
        table.setBackground(new Color(30, 30, 55));

        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(30, 30, 55));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 150)));
        main.add(scroll, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBackground(new Color(26, 26, 46));

        JButton clearBtn = makeButton("Clear All", new Color(160, 40, 40));
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all high scores?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                fileManager.saveHighScoreEntries(new MyArrayList<>());
                loadScores();
            }
        });
        btnPanel.add(clearBtn);

        JButton backBtn = makeButton("Back", new Color(60, 80, 180));
        backBtn.addActionListener(e -> dispose());
        btnPanel.add(backBtn);

        main.add(btnPanel, BorderLayout.SOUTH);
        loadScores();
    }

    private void loadScores() {
        tableModel.setRowCount(0);
        try {
            MyArrayList<HighScoreEntry> entries = fileManager.loadHighScoreEntries();
            String[] medals = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < entries.size(); i++) {
                HighScoreEntry e = entries.get(i);
                String rank = (i < 3 ? medals[i] + " " : "") + (i + 1);
                tableModel.addRow(new Object[]{rank, e.getPlayerName(), e.getScore(), e.getDate()});
            }
            if (entries.isEmpty()) {
                tableModel.addRow(new Object[]{"—", "No scores yet!", "—", "—"});
            }
        } catch (Exception e) {
            System.out.println("Error loading scores for display: " + e.getMessage());
        }
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
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        btn.setPreferredSize(new Dimension(140, 42));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
