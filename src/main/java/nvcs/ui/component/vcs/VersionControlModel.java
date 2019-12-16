package nvcs.ui.component.vcs;

import com.google.common.eventbus.Subscribe;
import nvcs.event.file.FileClosedEvent;
import nvcs.event.file.FileEditingEvent;
import nvcs.event.file.FileSavedEvent;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.event.vcs.FileRevertedEvent;
import nvcs.event.vcs.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;

import javax.annotation.Nullable;
import javax.swing.DefaultListModel;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
class VersionControlModel extends DefaultListModel<FileStatus> {

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectOpened(ProjectOpenedEvent e) {
        clear();
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onVcsStatusUpdated(VcsStatusIndexedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();
        Map<String, FileStatus> dirtyStatuses = collectDirtyStatuses();

        Set<FileStatus> currentStatuses = new HashSet<>();

        for (FileStatus status : statuses) {
            FileStatus dirtyStatus = dirtyStatuses.get(status.getFilePath());
            if (dirtyStatus == null
                    || dirtyStatus.getStatus() != status.getStatus()) {
                currentStatuses.add(status);
            }
        }

        currentStatuses.addAll(dirtyStatuses.values());

        clear();

        currentStatuses.stream()
                .sorted(Comparator.comparing(FileStatus::getFilePath))
                .forEach(s -> add(getSize(), s));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileEditing(FileEditingEvent e) {
        String fileName = e.getFileName();

        FileStatus currentStatus = getCurrentStatus(fileName);

        if (currentStatus != null) {
            set(indexOf(currentStatus), currentStatus.dirty());
        } else {
            add(getSize(), new FileStatus(
                    fileName,
                    FileStatus.Status.MODIFIED,
                    true));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileReverted(FileRevertedEvent e) {
        String revertedFilePath = e.getFilePath();
        removeDirtyStatus(revertedFilePath);
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileSaved(FileSavedEvent e) {
        String fileName = e.getFileName();
        for (int i = 0; i < getSize(); i++) {
            FileStatus fileStatus = get(i);
            if (fileName.equals(fileStatus.getFileName())) {
                set(i, fileStatus.clean());
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileClosed(FileClosedEvent e) {
        String revertedFilePath = e.getFileName();
        removeDirtyStatus(revertedFilePath);
    }

    protected void removeDirtyStatus(String revertedFilePath) {
        for (int i = 0; i < getSize(); i++) {
            FileStatus fileStatus = get(i);
            if (revertedFilePath.equals(fileStatus.getFilePath())) {
                remove(i);
                break;
            }
        }
    }

    @Nullable
    protected FileStatus getCurrentStatus(String fileName) {
        for (int i = 0; i < getSize(); i++) {
            FileStatus fileStatus = get(i);
            if (fileStatus.getFilePath()
                    .startsWith(fileName)) {
                return fileStatus;
            }
        }
        return null;
    }

    protected Map<String, FileStatus> collectDirtyStatuses() {
        Map<String, FileStatus> dirtyStatuses = new HashMap<>();
        for (int i = 0; i < getSize(); i++) {
            FileStatus fileStatus = get(i);
            if (fileStatus.isDirty()) {
                dirtyStatuses.put(fileStatus.getFilePath(), fileStatus);
            }
        }
        return dirtyStatuses;
    }
}
