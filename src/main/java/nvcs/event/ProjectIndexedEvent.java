package nvcs.event;

import nvcs.model.Project;

/**
 * The event is fired when the project is indexed.
 * <p>
 * Contains project structure in {@link ProjectIndexedEvent#getProject()}.
 */
public class ProjectIndexedEvent {

    protected final Project project;

    public ProjectIndexedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
