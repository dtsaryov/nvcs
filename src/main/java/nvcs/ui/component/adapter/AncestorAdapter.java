package nvcs.ui.component.adapter;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * An abstract adapter class for receiving component events.
 *
 * @see AncestorListener
 */
public abstract class AncestorAdapter implements AncestorListener {

    @Override
    public void ancestorAdded(AncestorEvent event) {
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
    }
}
