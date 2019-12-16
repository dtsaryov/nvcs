package nvcs.event.project;

import nvcs.model.Project;

/**
 * The event is fired when the project is indexed.
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
