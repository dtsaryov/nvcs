package nvcs.model;

/**
 * Class represents current status of file in repository.
 */
public class FileStatus {

    protected final String filePath;
    protected final Status status;

    protected boolean dirty;

    public FileStatus(String filePath, Status status) {
        this(filePath, status, false);
    }

    public FileStatus(String filePath, Status status, boolean dirty) {
        this.filePath = filePath;
        this.status = status;
        this.dirty = dirty;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        int slashIdx = filePath.lastIndexOf('/');
        return slashIdx > 0
                ? filePath.substring(slashIdx + 1)
                : filePath;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isDirty() {
        return dirty;
    }

    public FileStatus dirty() {
        return new FileStatus(filePath, status, true);
    }

    public FileStatus clean() {
        return new FileStatus(filePath, status, false);
    }

    public static FileStatus of(String fileName, Status status) {
        return new FileStatus(fileName, status);
    }

    @Override
    public String toString() {
        return String.format("%s%s (%s)",
                filePath,
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
