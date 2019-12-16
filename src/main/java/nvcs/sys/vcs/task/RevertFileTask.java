package nvcs.sys.vcs.task;

import com.google.common.eventbus.EventBus;
import nvcs.sys.vcs.VCS;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.SwingWorker;

/**
 * The task is used to revert project file.
 *
 * @see VCS
 */
@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class RevertFileTask extends SwingWorker<Boolean, Void> {

    protected final String filePath;

    protected final Git repository;

    protected final EventBus vcsEventBus;

    public RevertFileTask(String filePath, Git repository, EventBus vcsEventBus) {
        this.filePath = filePath;
        this.repository = repository;
        this.vcsEventBus = vcsEventBus;
    }

    @Override
    protected Boolean doInBackground() {
        boolean result = true;

        try {
            repository.checkout()
                    .addPath(filePath)
                    .call();
        } catch (GitAPIException ignored) {
            result = false;
        }

        return result;
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            vcsEventBus.post(new FileRevertedEvent(filePath));
        }
    }

    /**
     * The event is used to notify {@link VCS} that file reverted.
     */
    public class FileRevertedEvent {

        protected final String filePath;

        public FileRevertedEvent(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
