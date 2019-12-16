package nvcs.event.file;

/**
 * The event is fired when project file is opened.
 */
public class FileOpenedEvent {

    protected final String filePath;

    public FileOpenedEvent(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
