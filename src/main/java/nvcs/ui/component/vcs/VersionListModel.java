package nvcs.ui.component.vcs;

import com.google.common.eventbus.Subscribe;
import nvcs.event.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;

import javax.swing.DefaultListModel;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
class VersionListModel extends DefaultListModel<FileStatus> {

    @Subscribe
    protected void onVcsStatusUpdatedEvent(VcsStatusIndexedEvent e) {
        Set<FileStatus> statuses = e.getStatuses();

        clear();

        int idx = 0;
        for (FileStatus fileStatus : statuses) {
            add(idx++, fileStatus);
        }
    }
}
