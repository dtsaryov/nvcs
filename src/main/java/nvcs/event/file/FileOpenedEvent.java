package nvcs.event.file;

/**
 * The event is fired when project file is opened.
 * <p>
 * Contains file path in {@link FileOpenedEvent#getFilePath()}.
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
