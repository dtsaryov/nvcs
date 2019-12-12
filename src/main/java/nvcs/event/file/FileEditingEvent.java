package nvcs.event.file;

/**
 * The event is fired when project file is being modified.
 */
public class FileEditingEvent {

    protected final String fileName;

    public FileEditingEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
