package nvcs.ui.component;

import nvcs.App;
import nvcs.event.ProjectOpenedEvent;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("UnstableApiUsage")
public class AppMenu extends JMenuBar {

    public AppMenu() {
        JMenu fileMenu = new JMenu("File");

        fileMenu.add(createMenuItem("Open", this::openProject));
        fileMenu.add(createMenuItem("Exit", this::exit));

        add(fileMenu);
    }

    protected void openProject() {
        JFileChooser chooser = new JFileChooser(".");

        chooser.setDialogTitle("Choose project directory");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setApproveButtonText("Select");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        App app = App.getInstance();

        int result = chooser.showOpenDialog(app.getMainFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            String projectDir = chooser.getSelectedFile()
                    .getAbsolutePath();

            app.getEventBus()
                    .post(new ProjectOpenedEvent(projectDir));
        }
    }

    protected void exit() {
        System.exit(App.EXIT_STATUS);
    }

    protected JMenuItem createMenuItem(String caption, Runnable action) {
        JMenuItem item = new JMenuItem(caption);
        item.addActionListener(e -> action.run());
        return item;
    }
}
