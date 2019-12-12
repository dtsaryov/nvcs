package nvcs.ui.frame;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.vcs.VcsStatusIndexedEvent;
import nvcs.model.FileStatus;
import nvcs.ui.component.adapter.AncestorAdapter;
import nvcs.ui.component.vcs.VersionControl;
import nvcs.util.UIUtils;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

@SuppressWarnings("UnstableApiUsage")
public class VcsPanel extends JPanel {

    protected final VersionControl versionControl;

    protected JButton refreshButton = null;
    protected JButton revertButton = null;

    public VcsPanel() {
        UIUtils.expandLayout(this);

        initButtonsPanel();

        versionControl = initVersionControl();

        addAncestorListener(new AncestorAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                App.getInstance().getEventBus()
                        .register(VcsPanel.this);
            }
        });
    }

    @Subscribe
    protected void onVcsStatusIndexed(VcsStatusIndexedEvent e) {
        refreshButton.setEnabled(true);
        revertButton.setEnabled(true);
    }

    protected void initButtonsPanel() {
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(createRefreshButton());
        buttonsPanel.add(createRevertButton());

        add(buttonsPanel, "wrap");
    }

    protected JButton createRefreshButton() {
        refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Triggers VCS status refresh");
        refreshButton.setEnabled(false);

        refreshButton.addActionListener(e ->
                App.getInstance().getVcs()
                        .updateStatus());

        return refreshButton;
    }

    protected JButton createRevertButton() {
        revertButton = new JButton("Revert");
        revertButton.setToolTipText("Triggers last snapshot of the selected file");
        revertButton.setEnabled(false);

        revertButton.addActionListener(e -> {
            FileStatus versionedFile = versionControl.getSelectedValue();
            if (versionedFile != null) {
                if (versionedFile.getStatus() == FileStatus.Status.MODIFIED
                        || versionedFile.getStatus() == FileStatus.Status.MISSING) {
                    App.getInstance().getVcs()
                            .revertFile(versionedFile.getFileName());
                } else {
                    JOptionPane.showMessageDialog(App.getInstance().getMainFrame(),
                            "Unsupported operation");
                }
            } else {
                JOptionPane.showMessageDialog(App.getInstance().getMainFrame(),
                        "Please select some file to revert");
            }
        });

        return revertButton;
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
