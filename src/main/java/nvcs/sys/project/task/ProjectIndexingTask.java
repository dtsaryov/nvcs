package nvcs.sys.project.task;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import nvcs.event.project.ProjectIndexedEvent;
import nvcs.model.Project;
import nvcs.model.Project.ProjectNode;
import nvcs.sys.project.ProjectIndexer;

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
@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class ProjectIndexingTask extends SwingWorker<Project, Void> {

    protected static final Pattern PROJECT_NODES_REGEX = Pattern.compile("^[^.].+$");

    protected final EventBus eventBus;

    protected final String projectDir;

    protected Project project;

    public ProjectIndexingTask(String projectDir, EventBus eventBus) {
        checkArgument(!Strings.isNullOrEmpty(projectDir),
                "Unable to index project without its location");

        this.eventBus = eventBus;
        this.projectDir = projectDir;
    }

    @Override
    protected Project doInBackground() {
        File rootDir = new File(projectDir);

        ProjectNode rootNode = index(rootDir, null);

        project = new Project(rootNode, projectDir);

        return project;
    }

    protected ProjectNode index(File file, ProjectNode parentNode) {
        ProjectNode node = new ProjectNode(file.getName(), file.isDirectory(), parentNode);

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                List<ProjectNode> childNodes = Arrays.stream(children)
                        .filter(f -> PROJECT_NODES_REGEX.matcher(f.getName()).matches())
                        .map(f -> index(f, parentNode))
                        .collect(Collectors.toList());

                node.setChildren(childNodes);
            }
        }

        return node;
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            eventBus.post(new IndexedEvent(project));
        }
    }

    public class IndexedEvent {

        protected final Project project;

        public IndexedEvent(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }
    }
}
