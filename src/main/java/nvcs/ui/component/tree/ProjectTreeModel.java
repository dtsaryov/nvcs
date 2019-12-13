package nvcs.ui.component.tree;

import com.google.common.eventbus.Subscribe;
import nvcs.event.file.FileDeletedEvent;
import nvcs.event.project.ProjectIndexedEvent;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.model.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

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
    protected void onProjectOpened(ProjectOpenedEvent e) {
        setRoot(new DefaultMutableTreeNode("Importing..."));
    }

    @Subscribe
    protected void onProjectIndexedEvent(ProjectIndexedEvent e) {
        this.project = e.getProject();

        invokeLater(() ->
                setRoot(buildTree(this.project.getRoot())));
    }

    @Subscribe
    protected void onFileDeletedEvent(FileDeletedEvent e) {
        deleteNode(((DefaultMutableTreeNode) getRoot()), e.getFileName());
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

    protected void deleteNode(DefaultMutableTreeNode parentNode, String fileName) {
        List<DefaultMutableTreeNode> folders = new ArrayList<>();

        boolean deleted = false;

        for (int i = 0; i < parentNode.getChildCount(); i++) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) parentNode.getChildAt(i);

            if (fileName.equals(node.getUserObject())) {
                parentNode.remove(i);

                deleted = true;

                invokeLater(() ->
                        reload(parentNode));

                break;
            }

            if (node.getAllowsChildren()) {
                folders.add(node);
            }
        }

        if (!deleted) {
            folders.forEach(f ->
                    deleteNode(f, fileName));
        }
    }
}
