package nvcs;

import nvcs.ui.AppMenu;
import nvcs.ui.MainFrame;

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
        init();
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    protected void init() {
        initUI();
    }

    protected void initAppearance() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    protected void initUI() {
        initAppearance();

        mainFrame = new MainFrame();

        initMenu();
    }

    protected void initMenu() {
        if (mainFrame == null) {
            throw new IllegalStateException("Unable to init menu because the main frame is null");
        }
        mainFrame.setJMenuBar(new AppMenu());
    }
}
