package nvcs.ui.component;

import nvcs.App;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class AppMenu extends JMenuBar {

    public AppMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createExitAction());

        add(fileMenu);
    }

    protected JMenuItem createExitAction() {
        JMenuItem exitMenuAction = new JMenuItem("Exit");

        exitMenuAction.addActionListener(e ->
                System.exit(App.EXIT_STATUS));

        return exitMenuAction;
    }
}
