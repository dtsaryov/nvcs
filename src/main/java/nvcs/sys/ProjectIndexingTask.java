package nvcs.sys;

import com.google.common.base.Strings;
import nvcs.App;
import nvcs.event.ProjectIndexedEvent;
import nvcs.model.Project;
import nvcs.model.Project.ProjectNode;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Background task that traverses through project and builds its simplified model.
 * <p>
 * Fires {@link ProjectIndexedEvent} when indexing is finished.
 *
 * @see ProjectIndexer
 */
@SuppressWarnings("UnstableApiUsage")
class ProjectIndexingTask extends SwingWorker<Project, Void> {

    protected static final Pattern PROJECT_NODES_REGEX = Pattern.compile("^[^.].+$");

    protected final String projectDir;

    protected Project indexedProject;

    ProjectIndexingTask(String projectDir) {
        checkArgument(!Strings.isNullOrEmpty(projectDir),
                "Unable to index project without its location");

        this.projectDir = projectDir;
    }

    @Override
    protected Project doInBackground() {
        File rootDir = new File(projectDir);

        ProjectNode rootNode = new ProjectNode(rootDir.getName(), true, null);

        indexChildren(rootDir, rootNode);

        indexedProject = new Project(rootNode, projectDir);

        return indexedProject;
    }

    protected ProjectNode index(File file, ProjectNode parent) {
        ProjectNode node = new ProjectNode(
                file.getName(),
                file.isDirectory(),
                parent);

        indexChildren(file, node);

        return node;
    }

    protected void indexChildren(File file, ProjectNode node) {
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles((dir, name) ->
                    PROJECT_NODES_REGEX.matcher(name).matches());

            if (childFiles != null) {
                List<ProjectNode> children = Arrays.stream(childFiles)
                        .parallel()
                        .map(f -> index(f, node))
                        .collect(Collectors.toList());

                node.setChildren(children);
            }
        }
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            App.getInstance().getEventBus()
                    .post(new ProjectIndexedEvent(indexedProject));
        }
    }
}
