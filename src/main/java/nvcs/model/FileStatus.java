package nvcs.model;

/**
 * Class represents current status of file in repository.
 */
public class FileStatus {

    protected final String fileName;
    protected final Status status;

    public FileStatus(String fileName, Status status) {
        this.fileName = fileName;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public Status getStatus() {
        return status;
    }

    public static FileStatus of(String fileName, Status status) {
        return new FileStatus(fileName, status);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", fileName, status.name().toLowerCase());
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
