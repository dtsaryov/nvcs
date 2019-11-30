package nvcs;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class App {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame window = new JFrame("nvcs");
        window.setSize(400, 300);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Welcome to nvcs");
        window.add(label);

        window.setVisible(true);
    }
}
