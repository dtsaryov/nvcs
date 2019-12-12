package nvcs.ui.component.vcs;

import com.google.common.eventbus.Subscribe;
import nvcs.event.file.FileEditingEvent;
import nvcs.event.file.FileSavedEvent;
import nvcs.event.vcs.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
class VersionListModel extends DefaultListModel<FileStatus> {

    @Subscribe
    protected void onVcsStatusUpdatedEvent(VcsStatusIndexedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();
        Map<String, FileStatus> dirtyStatuses = collectDirtyStatuses();

        clear();

        Stream.concat(dirtyStatuses.values().stream(),
                statuses.stream().filter(fs -> !dirtyStatuses.containsKey(fs.getFileName())))
                .sorted(Comparator.comparing(FileStatus::getFileName))
                .forEach(fs -> add(getSize(), fs));
    }

    @Subscribe
    protected void onFileUpdatedEvent(FileEditingEvent e) {
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

    @Nullable
    protected FileStatus getCurrentStatus(String fileName) {
        for (int i = 0; i < getSize(); i++) {
            FileStatus fileStatus = get(i);
            if (fileStatus.getFileName()
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
                dirtyStatuses.put(fileStatus.getFileName(), fileStatus);
            }
        }
        return dirtyStatuses;
    }
}
