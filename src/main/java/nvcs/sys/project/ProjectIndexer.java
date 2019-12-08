package nvcs.sys.project;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.ProjectIndexedEvent;
import nvcs.event.ProjectOpenedEvent;
import nvcs.sys.project.task.ProjectIndexingTask;

/**
 * A class is intended to import project structure.
 *
 * @see ProjectIndexingTask
 */
@SuppressWarnings("UnstableApiUsage")
public class ProjectIndexer {

    protected final EventBus eventBus;

    protected ProjectIndexingTask indexingTask;

    public ProjectIndexer() {
        this.eventBus = new EventBus();

        eventBus.register(this);
    }

    @Subscribe
    protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
        if (indexingTask != null
                && !indexingTask.isDone()) {
            indexingTask.cancel(true);
        }

        indexingTask = new ProjectIndexingTask(e.getProjectDir(), eventBus);
        indexingTask.execute();
    }

    @Subscribe
    protected void onIndexedEvent(ProjectIndexingTask.IndexedEvent e) {
        App.getInstance().getEventBus()
                .post(new ProjectIndexedEvent(e.getProject()));
    }
}
