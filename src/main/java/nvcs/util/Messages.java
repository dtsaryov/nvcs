package nvcs.util;

import nvcs.App;

import javax.swing.JOptionPane;

/**
 * Helper class to show messages.
 */
public final class Messages {

    private Messages() {
    }

    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(
                App.getInstance().getMainFrame(),
                message);
    }
}
