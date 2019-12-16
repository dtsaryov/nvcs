package nvcs.sys.vcs.task;

import com.google.common.eventbus.EventBus;
import nvcs.sys.vcs.VCS;
import org.eclipse.jgit.api.Git;

import javax.swing.SwingWorker;
import java.io.File;
import java.io.IOException;

/**
 * The task is used to import project git repository.
 *
 * @see VCS
 */
@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class OpenRepoTask extends SwingWorker<Git, Void> {

    protected final EventBus vcsEventBus;

    protected final String projectPath;

    protected Git repository;

    public OpenRepoTask(String projectPath, EventBus vcsEventBus) {
        this.projectPath = projectPath;
        this.vcsEventBus = vcsEventBus;
    }

    @Override
    protected Git doInBackground() {
        File projectDir = new File(projectPath);
        try {
            return repository = Git.open(projectDir);
        } catch (IOException ignored) {
            throw new RuntimeException("Failed to open Git repository: " + projectPath);
        }
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            vcsEventBus.post(
                    new RepositoryOpenedEvent(repository));
        }
    }

    public class RepositoryOpenedEvent {

        protected final Git repository;

        public RepositoryOpenedEvent(Git repository) {
            this.repository = repository;
        }

        public Git getRepository() {
            return repository;
        }
    }
}
