package nvcs.sys.vcs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.FileDeletedEvent;
import nvcs.event.ProjectOpenedEvent;
import nvcs.event.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;
import nvcs.sys.vcs.task.OpenRepoTask;
import nvcs.sys.vcs.task.UpdateStatusTask;
import org.eclipse.jgit.api.Git;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class VCS {

    protected final EventBus eventBus;

    protected Git repository = null;

    protected OpenRepoTask openRepoTask = null;
    protected UpdateStatusTask updateStatusTask = null;

    public VCS() {
        eventBus = new EventBus();
        eventBus.register(this);
    }

    public void updateStatus() {
        if (updateStatusTask != null
                && !updateStatusTask.isDone()) {
            updateStatusTask.cancel(true);
        }

        updateStatusTask = new UpdateStatusTask(repository, eventBus);
        updateStatusTask.execute();
    }

    @Subscribe
    protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
        openRepository(e.getProjectDir());
    }

    @Subscribe
    protected void onRepositoryOpenedEvent(OpenRepoTask.RepositoryOpenedEvent e) {
        repository = e.getRepository();

        updateStatus();
    }

    @Subscribe
    protected void onStatusesUpdatedEvent(UpdateStatusTask.StatusesUpdatedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();

        App.getInstance().getEventBus()
                .post(new VcsStatusIndexedEvent(statuses));
    }

    @Subscribe
    protected void onFileDeletedEvent(FileDeletedEvent e) {
        updateStatus();
    }

    protected void openRepository(String projectPath) {
        if (openRepoTask != null
                && !openRepoTask.isDone()) {
            openRepoTask.cancel(true);
        }

        openRepoTask = new OpenRepoTask(projectPath, eventBus);
        openRepoTask.execute();
    }
}
