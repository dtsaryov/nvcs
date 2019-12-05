package nvcs.sys.project;

import com.google.common.eventbus.Subscribe;
import nvcs.event.ProjectOpenedEvent;

/**
 * A class is intended to import project structure.
 * <p>
 *
 * @see ProjectIndexingTask
 */
@SuppressWarnings({"UnstableApiUsage", "InnerClassMayBeStatic"})
public class ProjectIndexer {

    protected ProjectIndexingTask indexingTask;

    @SuppressWarnings("unused")
    @Subscribe
    protected void onProjectOpenedEvent(ProjectOpenedEvent e) {
        if (indexingTask != null
                && !indexingTask.isDone()) {
            indexingTask.cancel(true);
        }

        indexingTask = new ProjectIndexingTask(e.getProjectDir());
        indexingTask.execute();
    }
}
