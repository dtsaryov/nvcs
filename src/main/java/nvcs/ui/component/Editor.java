package nvcs.ui.component;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.FileOpenedEvent;
import nvcs.ui.component.adapter.AncestorAdapter;
import nvcs.ui.component.adapter.DocumentAdapter;
import nvcs.util.IOUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.AncestorEvent;
import javax.swing.event.DocumentEvent;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

        if (isTabOpened(IOUtils.getFileName(filePath))) {
            return;
        }

        String fileContent = IOUtils.loadFile(filePath);

        invokeLater(() ->
                addTab(createTab(filePath, fileContent)));
    }

    protected void addTab(EditorTab editorTab) {
        int tabIdx = getTabCount();

        openedTabs.add(editorTab);

        addTab(editorTab.getFileName(), editorTab);
        setTabComponentAt(tabIdx, editorTab.getTabButton());
    }

    protected void removeTab(EditorTab tab) {
        int tabIndex = getTabIndex(tab);
        remove(tabIndex);

        openedTabs.remove(tab);
    }

    protected EditorTab createTab(String filePath, String fileContent) {
        String fileName = IOUtils.getFileName(filePath);

        return new EditorTab(fileName, fileContent)
                .withSaveListener(content ->
                        IOUtils.saveFile(filePath, content))
                .withCloseListener(tab -> {
                    if (!tab.isModified()) {
                        invokeLater(() -> removeTab(tab));
                        return;
                    }

                    int result = showUnsavedChangesDialog();
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            IOUtils.saveFile(filePath, tab.getFileContent());
                            invokeLater(() -> removeTab(tab));
                            break;
                        case JOptionPane.NO_OPTION:
                            invokeLater(() -> removeTab(tab));
                            break;
                        case JOptionPane.CANCEL_OPTION:
                    }
                });
    }

    protected boolean isTabOpened(String fileName) {
        return openedTabs.stream()
                .anyMatch(tab ->
                        tab.getFileName().equals(fileName));
    }

    protected int getTabIndex(EditorTab tab) {
        for (int i = 0; i < getTabCount(); i++) {
            if (tab == getComponentAt(i)) {
                return i;
            }
        }
        return -1;
    }

    protected int showUnsavedChangesDialog() {
        return JOptionPane.showOptionDialog(
                App.getInstance().getMainFrame(),
                "There are unsaved changes in the file. Do you want to save it?",
                "File is modified",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                JOptionPane.YES_OPTION);
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

        protected final String filePath;
        protected final String fileName;

        protected String fileContent;
        protected boolean modified = false;

        protected final JTextArea textArea;
        protected final TabButton tabButton;

        protected Consumer<String> fileSaveListener = null;
        protected Consumer<EditorTab> tabCloseListener = null;

        public EditorTab(String filePath, String fileContent) {
            this.filePath = filePath;
            this.fileName = IOUtils.getFileName(filePath);
            this.fileContent = fileContent;

            setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

            textArea = createTextArea(fileContent);
            setViewportView(textArea);

            tabButton = new TabButton(fileName, this::onSave, this::onClose);
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileContent() {
            return modified
                    ? textArea.getText()
                    : fileContent;
        }

        public boolean isModified() {
            return modified;
        }

        public TabButton getTabButton() {
            return tabButton;
        }

        public EditorTab withCloseListener(Consumer<EditorTab> tabCloseListener) {
            this.tabCloseListener = tabCloseListener;
            return this;
        }

        public EditorTab withSaveListener(Consumer<String> saveListener) {
            this.fileSaveListener = saveListener;
            return this;
        }

        protected JTextArea createTextArea(String fileContent) {
            JTextArea textArea = new JTextArea(fileContent);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            textArea.getDocument()
                    .addDocumentListener(new DocumentAdapter() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            onUpdate();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            onUpdate();
                        }
                    });

            return textArea;
        }

        protected void onUpdate() {
            boolean modified = !fileContent.equals(textArea.getText());

            setModified(modified);
        }

        protected void onSave() {
            setModified(false);

            fileContent = textArea.getText();

            if (fileSaveListener != null) {
                fileSaveListener.accept(fileContent);
            }
        }

        protected void onClose() {
            if (tabCloseListener != null) {
                tabCloseListener.accept(this);
            }
        }

        protected void setModified(boolean modified) {
            this.modified = modified;

            tabButton.setModified(modified);
        }
    }

    /**
     * The component is used as tab caption.
     * <p>
     * Contains tab caption and close button.
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    class TabButton extends JPanel {

        protected static final String MODIFIED_SUFFIX = " *";

        protected final JLabel captionLabel;

        protected boolean modified = false;

        TabButton(String tabCaption, Runnable saveListener, Runnable closeListener) {
            super(new FlowLayout(FlowLayout.LEFT, 10, 0));

            captionLabel = new JLabel(tabCaption);

            add(captionLabel);

            JButton saveButton = new JButton("s");
            saveButton.setToolTipText("Save file");
            saveButton.addActionListener(e -> saveListener.run());

            add(saveButton);

            JButton closeButton = new JButton("x");
            closeButton.setToolTipText("Close file");
            closeButton.addActionListener(e -> closeListener.run());

            add(closeButton);
        }

        public void setModified(boolean modified) {
            if (this.modified != modified) {
                this.modified = modified;

                String text = captionLabel.getText();

                if (modified) {
                    captionLabel.setText(text + MODIFIED_SUFFIX);
                } else {
                    captionLabel.setText(text.replace(MODIFIED_SUFFIX, ""));
                }
            }
        }
    }
}
