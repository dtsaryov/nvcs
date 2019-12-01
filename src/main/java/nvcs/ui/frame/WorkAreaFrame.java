package nvcs.ui.frame;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import nvcs.ui.component.Editor;
import nvcs.ui.component.ProjectTree;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import static javax.swing.SwingUtilities.invokeLater;

public class WorkAreaFrame extends JPanel {

    public WorkAreaFrame() {
        initLayout();
    }

    protected void initLayout() {
        LC layoutConstraints = new LC();
        layoutConstraints.width("100%");
        layoutConstraints.height("100%");

        MigLayout layoutManager = new MigLayout(layoutConstraints);
        setLayout(layoutManager);

        initSplitPane(layoutManager);
    }

    protected void initSplitPane(MigLayout layoutManager) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(split);

        CC splitConstraints = new CC();
        splitConstraints.width("100%");
        splitConstraints.height("100%");

        layoutManager.setComponentConstraints(split, splitConstraints);

        split.setLeftComponent(new ProjectTree());
        split.setRightComponent(new Editor());

        invokeLater(() ->
                split.setDividerLocation(0.2d));
    }
}
