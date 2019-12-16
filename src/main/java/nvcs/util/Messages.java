package nvcs.util;

import nvcs.App;

import javax.swing.JOptionPane;

/**
 * Helper class to show messages.
 */
public final class Messages {

    private Messages() {
    }

    /**
     * Shows message dialog via {@link JOptionPane}.
     *
     * @param message message to show
     */
    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(
                App.getInstance().getMainFrame(),
                message);
    }
}
