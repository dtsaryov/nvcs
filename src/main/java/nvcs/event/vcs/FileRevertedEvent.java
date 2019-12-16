package nvcs.event.vcs;

import nvcs.sys.vcs.VCS;

/**
 * The event is fired when project file is reverted by {@link VCS}.
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
