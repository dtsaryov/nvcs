package nvcs.ui.frame;

import nvcs.ui.component.VersionControl;
import nvcs.util.UIUtils;

import javax.swing.JPanel;

public class VcsPanel extends JPanel {

    public VcsPanel() {
        UIUtils.expandLayout(this);

        initVersionControl();
    }

    protected void initVersionControl() {
        VersionControl versionControl = new VersionControl();

        add(versionControl);

        UIUtils.expandComponent(versionControl, this);
    }
}
