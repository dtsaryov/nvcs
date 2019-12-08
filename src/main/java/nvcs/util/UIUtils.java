package nvcs.util;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper class that has utility methods for UI.
 */
public final class UIUtils {

    /**
     * Defines default app window size.
     */
    public static final Dimension DEFAULT_SIZE = new Dimension(1600, 900);

    private UIUtils() {
    }

    /**
     * Sets 100% width and height for the given {@code layout}.
     *
     * @param layout layout to expand
     */
    public static void expandLayout(Container layout) {
        checkNotNull(layout, "Null is passed as layout to expand");

        LC layoutConstraints = new LC()
                .width("100%")
                .height("100%");

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
        checkNotNull(component, "Null is passed as component to expand");
        checkNotNull(layout, "Null is passed as parent layout of component to expand");

        LayoutManager layoutManager = layout.getLayout();

        checkArgument(layoutManager instanceof MigLayout,
                "Not suitable layout manager");
        checkArgument(component.getParent() == layout,
                "Passed component is not a child of passed layout");

        CC componentConstraints = new CC()
                .width("100%")
                .height("100%");

        ((MigLayout) layoutManager)
                .setComponentConstraints(component, componentConstraints);
    }
}
