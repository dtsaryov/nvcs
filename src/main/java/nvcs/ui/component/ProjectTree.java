package nvcs.ui.component;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.FileOpenedEvent;
import nvcs.event.ProjectIndexedEvent;
import nvcs.event.ProjectOpenedEvent;
import nvcs.model.Project;
import nvcs.model.Project.ProjectNode;
import nvcs.ui.component.adapter.AncestorAdapter;
import nvcs.util.UIUtils;

import javax.swing.JTree;
import javax.swing.event.AncestorEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Stack;
import java.util.function.Consumer;

@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class ProjectTree extends JTree {

    public ProjectTree() {
        UIUtils.expandLayout(this);

        TreeModel model = new ProjectTreeModel();
        setModel(model);

        addComponentAttachListener(() ->
                App.getInstance().getEventBus()
                        .register(model));

        addDoubleClickListener(this::handleDoubleClick);
    }

    protected void handleDoubleClick(DefaultMutableTreeNode node) {
        if (node.getAllowsChildren()) {
            // ignore folders
            return;
        }

        Project project = ((ProjectTreeModel) getModel())
                .getProject();

        StringBuilder filePath = new StringBuilder(project.getProjectDir())
                .append(File.separator);

        Stack<String> paths = new Stack<>();

        while (node.getParent() != null) {
            paths.push((String) node.getUserObject());
            node = (DefaultMutableTreeNode) node.getParent();
        }
        while (!paths.empty()) {
            filePath.append(paths.pop())
                    .append(!paths.empty()
                            ? File.separator
                            : "");
        }

        App.getInstance().getEventBus()
                .post(new FileOpenedEvent(filePath.toString()));
    }

    protected void addComponentAttachListener(Runnable listener) {
        addAncestorListener(new AncestorAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                listener.run();
            }
        });
    }

    protected void addDoubleClickListener(Consumer<DefaultMutableTreeNode> listener) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selRow = getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 2) {
                        DefaultMutableTreeNode node =
                                (DefaultMutableTreeNode) getLastSelectedPathComponent();

                        listener.accept(node);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unused")
    class ProjectTreeModel extends DefaultTreeModel {

        protected Project project;

        public ProjectTreeModel() {
            this(new DefaultMutableTreeNode("No project imported"));
        }

        public ProjectTreeModel(TreeNode root) {
            super(root);
        }

        public Project getProject() {
            return project;
        }

        @Subscribe
        protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
            setRoot(new DefaultMutableTreeNode("Importing..."));
        }

        @Subscribe
        protected void onProjectIndexedEvent(ProjectIndexedEvent e) {
            this.project = e.getProject();
            setRoot(buildTree(this.project.getRoot()));
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
