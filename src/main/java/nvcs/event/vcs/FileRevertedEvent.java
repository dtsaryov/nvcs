package nvcs.event.vcs;

public class FileRevertedEvent {

    protected final String filePath;

    public FileRevertedEvent(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
