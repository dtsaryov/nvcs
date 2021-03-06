package nvcs.sys.vcs.task;

import com.google.common.eventbus.EventBus;
import nvcs.model.FileStatus;
import nvcs.sys.vcs.VCS;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.SwingWorker;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The task is used to update project version control status.
 *
 * @see VCS
 */
@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class UpdateStatusTask extends SwingWorker<Set<FileStatus>, Void> {

    protected final EventBus vcsEventBus;

    protected final Git repository;

    protected Set<FileStatus> statuses;

    public UpdateStatusTask(Git repository, EventBus vcsEventBus) {
        this.vcsEventBus = vcsEventBus;
        this.repository = repository;
    }

    @Override
    protected Set<FileStatus> doInBackground() {
        try {
            Status repoStatus = repository.status()
                    .call();

            statuses = new HashSet<>();

            for (FileStatus.Status status : FileStatus.Status.values()) {
                statuses.addAll(getWithStatus(repoStatus, status));
            }

            return statuses;
        } catch (GitAPIException e) {
            throw new RuntimeException(
                    "Failed to get file statuses in repository: " + repository);
        }
    }

    @Override
    protected void done() {
        if (!isCancelled()) {
            vcsEventBus.post(new StatusesUpdatedEvent(statuses));
        }
    }

    protected Set<FileStatus> getWithStatus(Status repositoryStatus,
                                            FileStatus.Status status) {
        switch (status) {
            case UNTRACKED:
                return toFileStatus(repositoryStatus.getUntracked(), status);
            case ADDED:
                return toFileStatus(repositoryStatus.getAdded(), status);
            case CHANGED:
                return toFileStatus(repositoryStatus.getChanged(), status);
            case MODIFIED:
                return toFileStatus(repositoryStatus.getModified(), status);
            case REMOVED:
                return toFileStatus(repositoryStatus.getRemoved(), status);
            case MISSING:
                return toFileStatus(repositoryStatus.getMissing(), status);
            default:
                throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }

    protected Set<FileStatus> toFileStatus(Set<String> files, FileStatus.Status status) {
        return files.stream()
                .map(file -> FileStatus.of(file, status))
                .collect(Collectors.toSet());
    }

    public class StatusesUpdatedEvent {

        protected final Set<FileStatus> statuses;

        public StatusesUpdatedEvent(Set<FileStatus> statuses) {
            this.statuses = statuses;
        }

        public Set<FileStatus> getStatuses() {
            return Collections.unmodifiableSet(statuses);
        }
    }
}
