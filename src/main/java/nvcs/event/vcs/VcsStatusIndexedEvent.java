package nvcs.event.vcs;

import nvcs.model.FileStatus;

import java.util.Collections;
import java.util.Set;

/**
 * The event is fired when project files status is indexed.
 * <p>
 * Contains current file statuses in {@link VcsStatusIndexedEvent#getStatuses()}.
 *
 * @see FileStatus
 */
public class VcsStatusIndexedEvent {

    protected final Set<FileStatus> statuses;

    public VcsStatusIndexedEvent(Set<FileStatus> statuses) {
        this.statuses = statuses;
    }

    public Set<FileStatus> getStatuses() {
        return Collections.unmodifiableSet(statuses);
    }
}
