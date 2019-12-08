package nvcs.ui.component.tree;

import com.google.common.eventbus.Subscribe;
import nvcs.event.ProjectIndexedEvent;
import nvcs.event.ProjectOpenedEvent;
import nvcs.model.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("UnstableApiUsage")
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

        invokeLater(() ->
                setRoot(buildTree(this.project.getRoot())));
    }

    protected DefaultMutableTreeNode buildTree(Project.ProjectNode projectNode) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(
                projectNode.getName(),
                projectNode.isDirectory());

        if (projectNode.isDirectory()) {
            projectNode.getChildren()
                    .stream()
                    .map(this::buildTree)
                    .forEach(treeNode::add);
        }

        return treeNode;
    }
}
