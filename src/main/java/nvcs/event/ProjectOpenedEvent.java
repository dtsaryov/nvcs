package nvcs.event;

/**
 * An event that is fired when the project directory is opened.
 * <p>
 * Contains project root directory in {@link ProjectOpenedEvent#getProjectDir()}.
 */
public class ProjectOpenedEvent {

    protected final String projectDir;

    public ProjectOpenedEvent(String projectDir) {
        this.projectDir = projectDir;
    }

    public String getProjectDir() {
        return projectDir;
    }
}
