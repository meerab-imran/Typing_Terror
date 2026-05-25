import ui.MainWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow();
            } catch (Exception e) {
                System.err.println("Fatal error launching game: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to launch Typing Terror:\n" + e.getMessage(),
                        "Launch Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
