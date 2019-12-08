package nvcs;

import com.google.common.eventbus.EventBus;
import nvcs.sys.project.ProjectIndexer;
import nvcs.sys.vcs.VCS;
import nvcs.ui.frame.MainFrame;

import javax.swing.JFrame;
import javax.swing.UIManager;

import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("UnstableApiUsage")
public class App {

    public static final int EXIT_STATUS = 0;

    protected static App app;

    protected final EventBus eventBus;

    protected final JFrame mainFrame;

    protected final ProjectIndexer projectIndexer;

    protected final VCS vcs;

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

    public static App getInstance() {
        return app;
    }

    public App() {
        initAppearance();

        eventBus = new EventBus();

        mainFrame = new MainFrame();

        projectIndexer = new ProjectIndexer();
        eventBus.register(projectIndexer);

        vcs = new VCS();
        eventBus.register(vcs);
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    protected void initAppearance() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}
