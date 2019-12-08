package nvcs.ui.frame;

import nvcs.App;
import nvcs.ui.component.vcs.VersionControl;
import nvcs.util.UIUtils;

import javax.swing.JButton;
import javax.swing.JPanel;

public class VcsPanel extends JPanel {

    public VcsPanel() {
        UIUtils.expandLayout(this);

        initButtonsPanel();
        initVersionControl();
    }

    protected void initButtonsPanel() {
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e ->
                App.getInstance()
                        .getVcs()
                        .updateStatus());
        add(refreshBtn);
    }

    protected void initVersionControl() {
        VersionControl versionControl = new VersionControl();

        add(versionControl);

        UIUtils.expandComponent(versionControl, this);
    }
}
