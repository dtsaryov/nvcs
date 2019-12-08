package nvcs.ui.frame;

import nvcs.ui.component.editor.Editor;
import nvcs.ui.component.tree.ProjectTree;
import nvcs.util.UIUtils;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import static javax.swing.SwingUtilities.invokeLater;

public class WorkAreaFrame extends JPanel {

    public WorkAreaFrame() {
        UIUtils.expandLayout(this);

        initSplitPane();
    }

    protected void initSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        split.setLeftComponent(new ProjectTree());
        split.setRightComponent(new Editor());

        add(split);

        UIUtils.expandComponent(split, this);

        invokeLater(() ->
                split.setDividerLocation(0.2d));
    }
}
