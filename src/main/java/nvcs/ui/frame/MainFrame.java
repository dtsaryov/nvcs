package nvcs.ui.frame;

import nvcs.App;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.ui.component.AppMenu;
import nvcs.util.UIUtils;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * Application root frame.
 */
@SuppressWarnings("UnstableApiUsage")
public class MainFrame extends JFrame {

    protected static final double MAIN_SPLIT_DEFAULT_POS = 0.7d;

    protected final JFileChooser fileChooser;

    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(UIUtils.DEFAULT_SIZE);
        setTitle("nvcs");

        fileChooser = createFileChooser();

        initMenu();
        initLayout();
    }

    protected JFileChooser createFileChooser() {
        JFileChooser chooser = new JFileChooser(".");

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setApproveButtonText("Select");
        chooser.setDialogTitle("Choose project directory");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        return chooser;
    }

    protected void initMenu() {
        AppMenu menu = new AppMenu();

        menu.addItem("Open", this::openProject);
        menu.addItem("Exit", this::exit);

        setJMenuBar(menu);
    }

    protected void initLayout() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        split.setTopComponent(new WorkAreaFrame());
        split.setBottomComponent(new VcsFrame());

        setContentPane(split);

        invokeLater(() ->
                split.setDividerLocation(MAIN_SPLIT_DEFAULT_POS));
    }

    protected void openProject() {
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String projectDir = fileChooser.getSelectedFile()
                    .getAbsolutePath();

            App.getInstance().getEventBus()
                    .post(new ProjectOpenedEvent(projectDir));
        }
    }

    protected void exit() {
        System.exit(App.EXIT_STATUS);
    }
}
