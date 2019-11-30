package nvcs.ui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    protected static final Dimension DEFAULT_SIZE = new Dimension(1600, 900);

    public MainFrame() {
        setTitle("nvcs");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(DEFAULT_SIZE);
    }
}
