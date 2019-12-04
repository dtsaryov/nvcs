package nvcs.ui.component.adapter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * An abstract adapter class for receiving document events.
 *
 * @see DocumentListener
 */
public abstract class DocumentAdapter implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
