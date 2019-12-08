package nvcs.ui.component.vcs;

import nvcs.App;
import nvcs.model.FileStatus;
import nvcs.ui.component.adapter.AncestorAdapter;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.AncestorEvent;

@SuppressWarnings({"InnerClassMayBeStatic", "UnstableApiUsage"})
public class VersionControl extends JScrollPane {

    public VersionControl() {
        setViewportView(new VersionList());
    }

    class VersionList extends JList<FileStatus> {

        protected VersionListModel model;

        public VersionList() {
            model = new VersionListModel();
            setModel(model);

            addAncestorListener(new AncestorAdapter() {
                @Override
                public void ancestorAdded(AncestorEvent event) {
                    App.getInstance().getEventBus()
                            .register(model);
                }
            });
        }
    }
}
