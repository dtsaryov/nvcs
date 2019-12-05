package nvcs.sys.vcs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.ProjectOpenedEvent;
import nvcs.event.VcsStatusUpdatedEvent;
import nvcs.model.FileStatus;
import nvcs.sys.vcs.task.OpenRepoTask;
import nvcs.sys.vcs.task.UpdateStatusTask;
import org.eclipse.jgit.api.Git;

import java.util.Collections;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class VCS {

    protected final EventBus vcsEventBus;

    protected Git repository = null;

    protected OpenRepoTask openRepoTask = null;
    protected UpdateStatusTask updateStatusTask = null;

    public VCS() {
        vcsEventBus = new EventBus();

        vcsEventBus.register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
        openRepository(e.getProjectDir());
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onRepositoryOpenedEvent(RepositoryOpenedEvent e) {
        repository = e.getRepository();

        updateStatus();
    }

    @Subscribe
    protected void onStatusesUpdatedEvent(StatusesUpdatedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();

        App.getInstance().getEventBus()
                .post(new VcsStatusUpdatedEvent(statuses));
    }

    protected void openRepository(String projectPath) {
        if (openRepoTask != null
                && !openRepoTask.isDone()) {
            openRepoTask.cancel(true);
        }

        openRepoTask = new OpenRepoTask(projectPath, vcsEventBus);
        openRepoTask.execute();
    }

    protected void updateStatus() {
        if (updateStatusTask != null
                && !updateStatusTask.isDone()) {
            updateStatusTask.cancel(true);
        }

        updateStatusTask = new UpdateStatusTask(repository, vcsEventBus);
        updateStatusTask.execute();
    }

    public static class RepositoryOpenedEvent {

        protected final Git repository;

        public RepositoryOpenedEvent(Git repository) {
            this.repository = repository;
        }

        public Git getRepository() {
            return repository;
        }
    }

    public static class StatusesUpdatedEvent {

        protected final Set<FileStatus> statuses;

        public StatusesUpdatedEvent(Set<FileStatus> statuses) {
            this.statuses = statuses;
        }

        public Set<FileStatus> getStatuses() {
            return Collections.unmodifiableSet(statuses);
        }
    }
}
