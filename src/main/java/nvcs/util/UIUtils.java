package nvcs.util;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper class that has utility methods for UI.
 */
public final class UIUtils {

    private UIUtils() {
    }

    /**
     * Sets 100% width and height for the given {@code layout}.
     *
     * @param layout layout to expand
     */
    public static void expandLayout(Container layout) {
        LC layoutConstraints = new LC();
        layoutConstraints.width("100%");
        layoutConstraints.height("100%");

        MigLayout layoutManager = new MigLayout(layoutConstraints);

        layout.setLayout(layoutManager);
    }

    /**
     * Sets 100% width and height for the given {@code component} inside {@code layout}.
     *
     * @param component component to expand
     * @param layout    component parent layout
     */
    public static void expandComponent(Component component, Container layout) {
        LayoutManager layoutManager = layout.getLayout();

        checkArgument(layoutManager instanceof MigLayout,
                "Not suitable layout manager");
        checkArgument(component.getParent() == layout,
                "Passed component is not a child of passed layout");

        CC componentConstraints = new CC();
        componentConstraints.width("100%");
        componentConstraints.height("100%");

        ((MigLayout) layoutManager)
                .setComponentConstraints(component, componentConstraints);
    }
}
