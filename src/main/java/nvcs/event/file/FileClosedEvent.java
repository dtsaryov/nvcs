package nvcs.event.file;

public class FileClosedEvent {

    protected final String fileName;

    public FileClosedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
