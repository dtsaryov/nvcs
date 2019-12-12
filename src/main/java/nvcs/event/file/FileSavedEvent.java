package nvcs.event.file;

/**
 * The event is fired when project file is saved.
 */
public class FileSavedEvent {

    protected final String fileName;

    public FileSavedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
