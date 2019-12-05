package nvcs.event;

import nvcs.model.FileStatus;

import java.util.Collections;
import java.util.Set;

public class VcsStatusUpdatedEvent {

    protected final Set<FileStatus> statuses;

    public VcsStatusUpdatedEvent(Set<FileStatus> statuses) {
        this.statuses = statuses;
    }

    public Set<FileStatus> getStatuses() {
        return Collections.unmodifiableSet(statuses);
    }
}
