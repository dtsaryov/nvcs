package nvcs.sys.vcs.task;

import com.google.common.eventbus.EventBus;
import nvcs.sys.vcs.VCS.RepositoryOpenedEvent;
import org.eclipse.jgit.api.Git;

import javax.swing.SwingWorker;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
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
            repository = Git.open(projectDir);

            return repository;
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
}
