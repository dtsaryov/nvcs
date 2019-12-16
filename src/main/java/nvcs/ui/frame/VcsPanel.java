package nvcs.ui.frame;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.vcs.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;
import nvcs.ui.component.vcs.VersionControl;
import nvcs.util.UIUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static nvcs.util.Messages.showMessage;
import static nvcs.util.UIUtils.onShow;

@SuppressWarnings("UnstableApiUsage")
public class VcsPanel extends JPanel {

    protected final VersionControl versionControl;

    protected JButton refreshButton = null;
    protected JButton revertButton = null;

    public VcsPanel() {
        UIUtils.expandLayout(this);

        initButtonsPanel();

        versionControl = initVersionControl();

        onShow(this, () ->
                App.getInstance().getEventBus()
                        .register(VcsPanel.this));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onVcsStatusIndexed(VcsStatusIndexedEvent e) {
        refreshButton.setEnabled(true);
        revertButton.setEnabled(true);
    }

    protected void initButtonsPanel() {
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(refreshButton = createRefreshButton());
        buttonsPanel.add(revertButton = createRevertButton());

        add(buttonsPanel, "wrap");
    }

    protected JButton createRefreshButton() {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Triggers VCS status refresh");
        refreshButton.setEnabled(false);
        refreshButton.addActionListener(e -> refreshVcsStatus());
        return refreshButton;
    }

    protected JButton createRevertButton() {
        JButton revertButton = new JButton("Revert");
        revertButton.setToolTipText("Triggers last snapshot of the selected file");
        revertButton.setEnabled(false);
        revertButton.addActionListener(e -> revertFile());
        return revertButton;
    }

    protected void refreshVcsStatus() {
        App.getInstance().getVcs()
                .refreshStatus();
    }

    protected void revertFile() {
        FileStatus versionedFile = versionControl.getSelectedValue();
        if (versionedFile == null) {
            showMessage("Please select some file to revert");
            return;
        }

        if (!fileCanBeReverted(versionedFile.getStatus())) {
            showMessage("Unsupported operation");
            return;
        }

        versionControl.clearSelection();

        App.getInstance().getVcs()
                .revertFile(versionedFile.getFilePath());
    }

    protected boolean fileCanBeReverted(FileStatus.Status fileStatus) {
        return FileStatus.Status.MODIFIED == fileStatus
                || FileStatus.Status.MISSING == fileStatus;
    }

    protected VersionControl initVersionControl() {
        JScrollPane scrollPane = new JScrollPane();

        VersionControl versionControl = new VersionControl();
        scrollPane.setViewportView(versionControl);

        add(scrollPane);

        UIUtils.expandComponent(scrollPane, this);

        return versionControl;
    }
}
