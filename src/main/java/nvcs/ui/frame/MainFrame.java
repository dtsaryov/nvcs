package nvcs.ui.frame;

import nvcs.ui.component.AppMenu;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.Dimension;

import static javax.swing.SwingUtilities.invokeLater;

public class MainFrame extends JFrame {

    protected static final Dimension DEFAULT_SIZE = new Dimension(1600, 900);
    protected static final double MAIN_SPLIT_DEFAULT_POS = 0.7d;

    public MainFrame() {
        setTitle("nvcs");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(DEFAULT_SIZE);

        initMenu();
        initLayout();
    }

    protected void initMenu() {
        setJMenuBar(new AppMenu());
    }

    protected void initLayout() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        split.setTopComponent(new WorkAreaFrame());
        // TODO: #3
        split.setBottomComponent(new JLabel("VCS Pane"));

        setContentPane(split);

        invokeLater(() ->
                split.setDividerLocation(MAIN_SPLIT_DEFAULT_POS));
    }
}
