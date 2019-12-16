package nvcs.event.file;

import nvcs.ui.component.editor.Editor;

/**
 * The event is fired when project file is deleted in {@link Editor}.
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
