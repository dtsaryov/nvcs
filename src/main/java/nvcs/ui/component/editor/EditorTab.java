package nvcs.ui.component.editor;

import nvcs.ui.component.adapter.DocumentAdapter;
import nvcs.util.IOUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import java.awt.FlowLayout;
import java.util.function.Consumer;

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

    protected Consumer<String> saveListener;
    protected Consumer<EditorTab> closeListener;
    protected Consumer<EditorTab> updateListener;

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
        this.closeListener = tabCloseListener;
        return this;
    }

    public EditorTab withSaveListener(Consumer<String> saveListener) {
        this.saveListener = saveListener;
        return this;
    }

    public EditorTab withUpdateListener(Consumer<EditorTab> updateListener) {
        this.updateListener = updateListener;
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

        if (updateListener != null) {
            updateListener.accept(this);
        }
    }

    protected void onSave() {
        setModified(false);

        fileContent = textArea.getText();

        if (saveListener != null) {
            saveListener.accept(fileContent);
        }
    }

    protected void onClose() {
        if (closeListener != null) {
            closeListener.accept(this);
        }
    }

    protected void setModified(boolean modified) {
        this.modified = modified;

        tabButton.setModified(modified);
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
