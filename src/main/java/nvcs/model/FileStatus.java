package nvcs.model;

/**
 * Class represents current status of file in repository.
 */
public class FileStatus {

    protected final String fileName;
    protected final Status status;

    protected boolean dirty;

    public FileStatus(String fileName, Status status) {
        this(fileName, status, false);
    }

    public FileStatus(String fileName, Status status, boolean dirty) {
        this.fileName = fileName;
        this.status = status;
        this.dirty = dirty;
    }

    public String getFileName() {
        return fileName;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isDirty() {
        return dirty;
    }

    public FileStatus dirty() {
        return new FileStatus(fileName, status, true);
    }

    public static FileStatus of(String fileName, Status status) {
        return new FileStatus(fileName, status);
    }

    @Override
    public String toString() {
        return String.format("%s%s (%s)",
                fileName,
                dirty ? " *" : "",
                status.name().toLowerCase());
    }

    /**
     * Defines repository file status.
     */
    public enum Status {

        /**
         * File is not in index.
         */
        UNTRACKED,
        /**
         * New file added to index.
         */
        ADDED,
        /**
         * File is modified and added to stage.
         */
        CHANGED,
        /**
         * File is changed, not added to stage.
         */
        MODIFIED,
        /**
         * File is removed from index.
         */
        REMOVED,
        /**
         * File is removed from file system.
         */
        MISSING
    }
}
