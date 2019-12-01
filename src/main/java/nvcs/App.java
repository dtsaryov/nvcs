package nvcs;

import nvcs.ui.frame.MainFrame;

import javax.swing.JFrame;
import javax.swing.UIManager;

import static javax.swing.SwingUtilities.invokeLater;

public class App {

    public static final int EXIT_STATUS = 0;

    protected static App app;

    protected JFrame mainFrame;

    /**
     * Main entry point.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        invokeLater(() -> {
            app = new App();
            app.show();
        });
    }

    public App() {
        initAppearance();

        mainFrame = new MainFrame();
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    protected void initAppearance() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}
