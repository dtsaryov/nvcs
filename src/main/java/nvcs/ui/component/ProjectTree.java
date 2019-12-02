package nvcs.ui.component;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import nvcs.App;
import nvcs.event.ProjectIndexedEvent;
import nvcs.event.ProjectOpenedEvent;
import nvcs.model.Project.ProjectNode;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class ProjectTree extends JTree {

    protected final TreeModel model;

    protected boolean subscribed = false;

    public ProjectTree() {
        initLayout();

        model = new ProjectTreeModel();
        setModel(model);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!subscribed) {
                    subscribed = true;

                    App.getInstance().getEventBus()
                            .register(model);
                }
            }
        });
    }

    protected void initLayout() {
        LC layoutConstraints = new LC();
        layoutConstraints.width("100%");
        layoutConstraints.height("100%");

        MigLayout layoutManager = new MigLayout(layoutConstraints);
        setLayout(layoutManager);
    }

    class ProjectTreeModel extends DefaultTreeModel {

        public ProjectTreeModel() {
            this(new DefaultMutableTreeNode("No project imported"));
        }

        public ProjectTreeModel(TreeNode root) {
            super(root);
        }

        @SuppressWarnings("unused")
        @Subscribe
        protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
            setRoot(new DefaultMutableTreeNode("Importing..."));
        }

        @SuppressWarnings("unused")
        @Subscribe
        protected void onProjectIndexedEvent(ProjectIndexedEvent e) {
            ProjectNode projectRoot = e.getProject()
                    .getRoot();
            setRoot(buildTree(projectRoot));
        }

        protected DefaultMutableTreeNode buildTree(ProjectNode projectNode) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(
                    projectNode.getName(),
                    projectNode.isDirectory());

            if (projectNode.isDirectory()) {
                projectNode.getChildren()
                        .parallelStream()
                        .map(this::buildTree)
                        .forEach(treeNode::add);
            }

            return treeNode;
        }
    }
}
