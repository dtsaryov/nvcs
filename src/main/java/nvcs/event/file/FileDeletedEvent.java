package nvcs.event.file;

/**
 * The event is fired when project file is deleted.
 */
public class FileDeletedEvent {

    protected final String fileName;

    public FileDeletedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
