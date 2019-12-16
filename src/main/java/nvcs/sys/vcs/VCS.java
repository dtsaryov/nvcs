package nvcs.sys.vcs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.file.FileClosedEvent;
import nvcs.event.file.FileDeletedEvent;
import nvcs.event.file.FileSavedEvent;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.event.vcs.FileRevertedEvent;
import nvcs.event.vcs.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;
import nvcs.sys.vcs.task.OpenRepoTask;
import nvcs.sys.vcs.task.RevertFileTask;
import nvcs.sys.vcs.task.UpdateStatusTask;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.Set;

import static nvcs.util.Messages.showMessage;

@SuppressWarnings("UnstableApiUsage")
public class VCS {

    protected static final String GIT_FOLDER = ".git";

    protected final EventBus eventBus;

    protected Git repository = null;

    protected OpenRepoTask openRepoTask = null;
    protected UpdateStatusTask updateStatusTask = null;

    public VCS() {
        eventBus = new EventBus();
        eventBus.register(this);
    }

    /**
     * Refreshes opened project version control status.
     */
    public void refreshStatus() {
        if (updateStatusTask != null
                && !updateStatusTask.isDone()) {
            updateStatusTask.cancel(true);
        }

        updateStatusTask = new UpdateStatusTask(repository, eventBus);
        updateStatusTask.execute();
    }

    /**
     * Reverts the given project file.
     *
     * @param filePath name of file to revert
     */
    public void revertFile(String filePath) {
        new RevertFileTask(filePath, repository, eventBus)
                .execute();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectOpened(ProjectOpenedEvent e) {
        String projectDirPath = e.getProjectDir();

        if (hasGitFolder(projectDirPath)) {
            openRepository(projectDirPath);
        } else {
            showMessage("Project is not under version control");
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileSaved(FileSavedEvent e) {
        refreshStatus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileDeleted(FileDeletedEvent e) {
        refreshStatus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileClosed(FileClosedEvent e) {
        refreshStatus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onRepositoryOpened(OpenRepoTask.RepositoryOpenedEvent e) {
        repository = e.getRepository();

        refreshStatus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onStatusesUpdated(UpdateStatusTask.StatusesUpdatedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();

        App.getInstance().getEventBus()
                .post(new VcsStatusIndexedEvent(statuses));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileReverted(RevertFileTask.FileRevertedEvent e) {
        App.getInstance().getEventBus()
                .post(new FileRevertedEvent(e.getFilePath()));

        refreshStatus();
    }

    /**
     * Checks the project is under version control
     *
     * @param projectDirPath project directory path
     * @return true if .git folder is found or false otherwise
     */
    protected boolean hasGitFolder(String projectDirPath) {
        File projectDir = new File(projectDirPath);

        File[] files = projectDir.listFiles((dir, name) ->
                GIT_FOLDER.equals(name));

        return files != null
                && files.length == 1;
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
