package nvcs.ui.component;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.FileOpenedEvent;
import nvcs.ui.component.adapter.AncestorAdapter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.AncestorEvent;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("UnstableApiUsage")
public class Editor extends JTabbedPane {

    protected List<EditorTab> openedTabs = new ArrayList<>();

    public Editor() {
        addComponentAttachListener(() ->
                App.getInstance().getEventBus()
                        .register(Editor.this));
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileOpenedEvent(FileOpenedEvent e) {
        String filePath = e.getFilePath();
        String fileName = filePath.substring(
                filePath.lastIndexOf(File.separator) + 1);

        boolean alreadyOpened = openedTabs.stream()
                .anyMatch(tab ->
                        tab.getFileName().equals(fileName));

        if (alreadyOpened) {
            return;
        }

        File file = new File(filePath);

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            String fileContent = bufferedReader.lines()
                    .collect(Collectors.joining("\n"));

            invokeLater(() ->
                    addTab(file.getName(), fileContent));
        } catch (Exception ignored) {
        }
    }

    protected void addTab(String fileName, String fileContent) {
        int idx = getTabCount();

        EditorTab tab = new EditorTab(
                fileName,
                fileContent);
        tab.setTabCloseListener(this::removeTab);

        openedTabs.add(tab);

        addTab(fileName, tab);
        setTabComponentAt(idx, tab.getTabButton());
    }

    protected void removeTab(EditorTab tab) {
        int tabIndex = getTabIndex(tab);
        remove(tabIndex);

        openedTabs.remove(tab);
    }

    protected int getTabIndex(EditorTab tab) {
        for (int i = 0; i < getTabCount(); i++) {
            if (tab == getComponentAt(i)) {
                return i;
            }
        }
        return -1;
    }

    protected void addComponentAttachListener(Runnable listener) {
        addAncestorListener(new AncestorAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                listener.run();
            }
        });
    }

    /**
     * The component represents single editor tab.
     */
    class EditorTab extends JScrollPane {

        protected final String fileName;

        protected final TabButton tabButton;

        protected Consumer<EditorTab> tabCloseListener = null;

        public EditorTab(String fileName, String content) {
            this.fileName = fileName;

            setHorizontalScrollBarPolicy(
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            JTextArea textArea = new JTextArea(content);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            setViewportView(textArea);

            tabButton = new TabButton(fileName, () -> {
                if (tabCloseListener != null) {
                    tabCloseListener.accept(this);
                }
            });
        }

        public String getFileName() {
            return fileName;
        }

        public TabButton getTabButton() {
            return tabButton;
        }

        public void setTabCloseListener(Consumer<EditorTab> tabCloseListener) {
            this.tabCloseListener = tabCloseListener;
        }
    }

    /**
     * The component is used as tab caption.
     * <p>
     * Contains tab caption and close button.
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    class TabButton extends JPanel {

        protected final String tabCaption;

        TabButton(String tabCaption, Runnable closeListener) {
            super(new FlowLayout(FlowLayout.LEFT, 10, 0));

            this.tabCaption = tabCaption;

            add(new JLabel(tabCaption));

            JButton closeButton = new JButton("x");
            closeButton.addActionListener(e -> closeListener.run());
            add(closeButton);
        }
    }
}
