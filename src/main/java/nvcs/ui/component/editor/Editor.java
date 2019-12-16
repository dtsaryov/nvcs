package nvcs.ui.component.editor;

import com.google.common.eventbus.Subscribe;
import nvcs.App;
import nvcs.event.file.FileClosedEvent;
import nvcs.event.file.FileDeletedEvent;
import nvcs.event.file.FileOpenedEvent;
import nvcs.event.file.FileEditingEvent;
import nvcs.event.file.FileSavedEvent;
import nvcs.event.project.ProjectOpenedEvent;
import nvcs.event.vcs.FileRevertedEvent;
import nvcs.util.IOUtils;

import javax.annotation.Nullable;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.swing.SwingUtilities.invokeLater;
import static nvcs.util.UIUtils.onShow;

@SuppressWarnings("UnstableApiUsage")
public class Editor extends JTabbedPane {

    protected List<EditorTab> openedTabs = new ArrayList<>();

    protected String projectDir = null;

    public Editor() {
        onShow(this, () ->
                App.getInstance().getEventBus()
                        .register(Editor.this));
    }

    @Subscribe
    protected void onProjectOpened(ProjectOpenedEvent e) {
        projectDir = e.getProjectDir();

        new ArrayList<>(openedTabs)
                .forEach(this::removeTab);
    }

    @SuppressWarnings("unused")
    @Subscribe
    protected void onFileOpened(FileOpenedEvent e) {
        String filePath = e.getFilePath();

        if (isTabOpened(IOUtils.getFileName(filePath))) {
            return;
        }

        openFile(filePath);
    }

    protected void openFile(String filePath) {
        String fileContent = IOUtils.loadFile(filePath);

        invokeLater(() ->
                addTab(createTab(filePath, fileContent)));
    }

    @Subscribe
    protected void onFileReverted(FileRevertedEvent e) {
        String fileName = IOUtils.getFileName(e.getFilePath());

        EditorTab openedTab = getTab(fileName);
        if (openedTab != null) {
            removeTab(openedTab);

            openFile(projectDir + File.separator + e.getFilePath());
        }
    }

    protected EditorTab createTab(String filePath, String fileContent) {
        String fileName = IOUtils.getFileName(filePath);

        return new EditorTab(fileName, fileContent)
                .withUpdateListener(this::onTabUpdate)
                .withSaveListener(content ->
                        onTabSave(filePath, fileName, content))
                .withDeleteListener(tab ->
                        onTabDelete(filePath, fileName, tab))
                .withCloseListener(tab ->
                        onTabClose(filePath, fileName, tab));
    }

    protected void addTab(EditorTab editorTab) {
        int newTabIdx = getTabCount();

        openedTabs.add(editorTab);

        addTab(editorTab.getFileName(), editorTab);
        setTabComponentAt(newTabIdx, editorTab.getTabButton());

        setSelectedIndex(newTabIdx);
    }

    protected void removeTab(EditorTab tab) {
        int tabIndex = getTabIndex(tab);
        remove(tabIndex);

        openedTabs.remove(tab);
    }

    protected boolean isTabOpened(String fileName) {
        return openedTabs.stream()
                .anyMatch(tab ->
                        Objects.equals(tab.getFileName(), fileName));
    }

    @Nullable
    protected EditorTab getTab(String fileName) {
        for (EditorTab openedTab : openedTabs) {
            if (fileName.equals(openedTab.getFileName())) {
                return openedTab;
            }
        }
        return null;
    }

    protected int getTabIndex(EditorTab tab) {
        for (int i = 0; i < getTabCount(); i++) {
            if (tab == getComponentAt(i)) {
                return i;
            }
        }
        return -1;
    }

    protected void onTabClose(String filePath, String fileName, EditorTab tab) {
        if (!tab.isModified()) {
            invokeLater(() ->
                    removeTab(tab));
            return;
        }

        int result = showUnsavedChangesDialog();
        switch (result) {
            case JOptionPane.YES_OPTION: {
                IOUtils.saveFile(filePath, tab.getFileContent());

                App.getInstance().getEventBus()
                        .post(new FileSavedEvent(fileName));

                invokeLater(() ->
                        removeTab(tab));
                break;
            }
            case JOptionPane.NO_OPTION: {
                invokeLater(() -> {
                    App.getInstance().getEventBus()
                            .post(new FileClosedEvent(fileName));

                    removeTab(tab);
                });
            }
        }
    }

    protected void onTabUpdate(EditorTab tab) {
        App.getInstance().getEventBus()
                .post(new FileEditingEvent(
                        tab.getFileName()));
    }

    protected void onTabDelete(String filePath, String fileName, EditorTab tab) {
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
    }

    protected void onTabSave(String filePath, String fileName, String content) {
        IOUtils.saveFile(filePath, content);

        App.getInstance().getEventBus()
                .post(new FileSavedEvent(fileName));
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
}
