package nvcs.event.project;

/**
 * The event is fired when the project directory is opened.
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
