package nvcs.ui.frame;

import nvcs.App;
import nvcs.model.FileStatus;
import nvcs.ui.component.vcs.VersionControl;
import nvcs.util.UIUtils;

import javax.swing.*;

public class VcsPanel extends JPanel {

    protected final VersionControl versionControl;

    public VcsPanel() {
        UIUtils.expandLayout(this);

        initButtonsPanel();

        versionControl = initVersionControl();
    }

    protected void initButtonsPanel() {
        JPanel buttonsPanel = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e ->
                App.getInstance()
                        .getVcs()
                        .updateStatus());

        buttonsPanel.add(refreshBtn);

        JButton revertBtn = new JButton("Revert");
        revertBtn.addActionListener(e -> {
            FileStatus versionedFile = versionControl.getSelectedValue();
            if (versionedFile.getStatus() == FileStatus.Status.MODIFIED
                    || versionedFile.getStatus() == FileStatus.Status.MISSING) {
                App.getInstance().getVcs()
                        .revertFile(versionedFile.getFileName());
            }
        });
        buttonsPanel.add(revertBtn);

        add(buttonsPanel, "wrap");
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
