package nvcs.event.file;

import nvcs.ui.component.editor.Editor;

/**
 * The event is fired when file is closed in {@link Editor}.
 */
public class FileClosedEvent {

    protected final String fileName;

    public FileClosedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
