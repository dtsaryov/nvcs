package nvcs.ui.component;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class ProjectTree extends JTree {

    public ProjectTree() {
        LC layoutConstraints = new LC();
        layoutConstraints.width("100%");
        layoutConstraints.height("100%");

        MigLayout layoutManager = new MigLayout(layoutConstraints);
        setLayout(layoutManager);

        // TODO: #5
        setModel(generateMockData());
    }

    protected TreeModel generateMockData() {
        return new DefaultTreeModel(
                new DefaultMutableTreeNode("Root"));
    }
}
