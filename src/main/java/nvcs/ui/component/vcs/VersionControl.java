package nvcs.ui.component.vcs;

import nvcs.App;
import nvcs.model.FileStatus;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import static nvcs.util.UIUtils.onShow;

@SuppressWarnings("UnstableApiUsage")
public class VersionControl extends JList<FileStatus> {

    protected final VersionControlModel model;

    public VersionControl() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        model = new VersionControlModel();
        setModel(model);

        onShow(this, () ->
                App.getInstance().getEventBus()
                        .register(model));
    }
}
