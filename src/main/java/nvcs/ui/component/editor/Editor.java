package nvcs.ui.component.editor;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.FileDeletedEvent;
import nvcs.event.FileOpenedEvent;
import nvcs.event.FileEditingEvent;
import nvcs.ui.component.adapter.AncestorAdapter;
import nvcs.util.IOUtils;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.AncestorEvent;
import java.util.ArrayList;
import java.util.List;

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

    protected EditorTab createTab(String filePath, String fileContent) {
        String fileName = IOUtils.getFileName(filePath);

        return new EditorTab(fileName, fileContent)
                .withSaveListener(content ->
                        IOUtils.saveFile(filePath, content))
                .withDeleteListener(tab -> {
                    int result = showDeleteFileDialog(tab.getFileName());
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            IOUtils.deleteFile(filePath);

                            App.getInstance().getEventBus()
                                    .post(new FileDeletedEvent(fileName));

                            invokeLater(() -> removeTab(tab));
                            break;
                        case JOptionPane.NO_OPTION:
                            break;
                    }
                })
                .withUpdateListener(tab ->
                        App.getInstance().getEventBus()
                                .post(new FileEditingEvent(
                                        tab.getFileName())))
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

    protected int showDeleteFileDialog(String fileName) {
        return JOptionPane.showOptionDialog(
                App.getInstance().getMainFrame(),
                "Do you really want to delete file: " + fileName,
                "Delete file",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                JOptionPane.NO_OPTION);
    }

    protected void addComponentAttachListener(Runnable listener) {
        addAncestorListener(new AncestorAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                listener.run();
            }
        });
    }
}
