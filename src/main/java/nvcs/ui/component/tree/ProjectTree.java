package nvcs.ui.component.tree;

import nvcs.App;
import nvcs.event.file.FileOpenedEvent;
import nvcs.model.Project;
import nvcs.util.UIUtils;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Stack;
import java.util.function.Consumer;

import static nvcs.util.UIUtils.onShow;

@SuppressWarnings("UnstableApiUsage")
public class ProjectTree extends JTree {

    public ProjectTree() {
        UIUtils.expandLayout(this);

        TreeModel model = new ProjectTreeModel();
        setModel(model);

        onShow(this, () ->
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

        FileOpenedEvent event = new FileOpenedEvent(getFilePath(node, project));

        App.getInstance().getEventBus()
                .post(event);
    }

    protected String getFilePath(DefaultMutableTreeNode node, Project project) {
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

        return filePath.toString();
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
}
