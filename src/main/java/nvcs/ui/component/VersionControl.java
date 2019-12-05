package nvcs.ui.component;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.VcsStatusUpdatedEvent;
import nvcs.model.FileStatus;
import nvcs.ui.component.adapter.AncestorAdapter;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.AncestorEvent;
import java.util.Set;

@SuppressWarnings("ALL")
public class VersionControl extends JScrollPane {

    public VersionControl() {
        setViewportView(new VersionList());
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class VersionList extends JList<FileStatus> {

        protected VersionModel model;

        public VersionList() {
            model = new VersionModel();
            setModel(model);

            addAncestorListener(new AncestorAdapter() {
                @Override
                public void ancestorAdded(AncestorEvent event) {
                    App.getInstance().getEventBus()
                            .register(model);
                }
            });
        }

        class VersionModel extends DefaultListModel<FileStatus> {

            @Subscribe
            protected void onVcsStatusUpdatedEvent(VcsStatusUpdatedEvent e) {
                Set<FileStatus> statuses = e.getStatuses();

                clear();

                int idx = 0;
                for (FileStatus fileStatus : statuses) {
                    add(idx++, fileStatus);
                }
            }
        }
    }
}
