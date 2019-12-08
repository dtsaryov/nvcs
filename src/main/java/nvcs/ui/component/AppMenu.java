package nvcs.ui.component;

import nvcs.ui.frame.MainFrame;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Application main menu.
 *
 * @see MainFrame
 */
public class AppMenu extends JMenuBar {

    protected final JMenu menu;

    public AppMenu() {
        menu = new JMenu("File");
        add(menu);
    }

    public void addItem(String caption, Runnable command) {
        menu.add(createItem(caption, command));
    }

    protected JMenuItem createItem(String caption, Runnable action) {
        JMenuItem item = new JMenuItem(caption);
        item.addActionListener(e -> action.run());
        return item;
    }
}
