package nvcs.ui.component.tree;

import com.google.common.eventbus.Subscribe;
import nvcs.event.file.FileDeletedEvent;
import nvcs.event.project.ProjectIndexedEvent;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.event.vcs.FileRevertedEvent;
import nvcs.model.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("UnstableApiUsage")
class ProjectTreeModel extends DefaultTreeModel {

    protected Project project;

    public ProjectTreeModel() {
        this(new ModelNode("No project imported"));
    }

    public ProjectTreeModel(TreeNode root) {
        super(root);
    }

    public Project getProject() {
        return project;
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectOpened(ProjectOpenedEvent e) {
        setRoot(new ModelNode("Importing..."));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectIndexedEvent(ProjectIndexedEvent e) {
        this.project = e.getProject();

        invokeLater(() ->
                setRoot(buildTree(this.project.getRoot())));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileDeletedEvent(FileDeletedEvent e) {
        deleteNode(((ModelNode) getRoot()), e.getFileName());
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileReverted(FileRevertedEvent e) {
        revertNode(((ModelNode) getRoot()), e.getFilePath());
    }

    protected ModelNode buildTree(Project.ProjectNode projectNode) {
        ModelNode treeNode = new ModelNode(
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

    protected void deleteNode(ModelNode parentNode, String fileName) {
        List<ModelNode> folders = new ArrayList<>();

        boolean deleted = false;

        for (int i = 0; i < parentNode.getChildCount(); i++) {
            ModelNode node =
                    (ModelNode) parentNode.getChildAt(i);

            if (!node.getAllowsChildren()
                    && fileName.equals(node.getUserObject())) {
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

    protected void revertNode(ModelNode parentNode, String filePath) {
        int slashIdx = filePath.indexOf('/');

        if (slashIdx > 0) {
            String dirName = filePath.substring(0, slashIdx);

            for (int i = 0; i < parentNode.getChildCount(); i++) {
                ModelNode node =
                        (ModelNode) parentNode.getChildAt(i);

                if (node.getAllowsChildren()
                        && dirName.equals(node.getUserObject())) {
                    revertNode(node, filePath.substring(slashIdx + 1));
                    break;
                }
            }
        } else {
            if (!parentNode.hasChild(new ModelNode(filePath))) {
                addLeaf(parentNode, filePath);
            }
        }
    }

    protected void addLeaf(ModelNode parentNode, String nodeName) {
        ModelNode leaf = new ModelNode(nodeName);

        List<ModelNode> children = Collections.list(parentNode.children());
        children.add(leaf);

        children.sort(Comparator.comparing(ModelNode::getUserObject));

        parentNode.removeAllChildren();
        children.forEach(parentNode::add);

        invokeLater(() ->
                reload(parentNode));
    }
    
    static class ModelNode extends DefaultMutableTreeNode {

        public ModelNode(String userObject) {
            this(userObject, false);
        }

        public ModelNode(Object userObject, boolean allowsChildren) {
            super(userObject, allowsChildren);
        }

        @Override
        public String getUserObject() {
            return (String) super.getUserObject();
        }

        public boolean hasChild(ModelNode node) {
            for (ModelNode child : Collections.list(children())) {
                if (Objects.equals(node.getUserObject(), child.getUserObject())) {
                    return true;
                }
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Enumeration<ModelNode> children() {
            return super.children();
        }
    }
}
