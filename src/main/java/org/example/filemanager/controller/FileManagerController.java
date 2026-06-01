package org.example.filemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.filemanager.component.FileSystemItem;
import org.example.filemanager.composite.FolderItem;
import org.example.filemanager.exception.FileSystemException;
import org.example.filemanager.leaf.FileItem;

import java.util.ResourceBundle;

public class FileManagerController {

    @FXML private TreeView<FileSystemItem> treeView;
    @FXML private Label labelTotalSize;
    @FXML private TextField fieldName;
    @FXML private TextField fieldSize;
    @FXML private HBox rowSize;
    @FXML private ToggleGroup typeGroup;
    @FXML private RadioButton radioFile;
    @FXML private RadioButton radioFolder;
    @FXML private TextArea logArea;
    @FXML private Button btnAdd;
    @FXML private Button btnDelete;

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("org.example.filemanager.strings");

        // Применяем тексты из ресурсного файла
        btnAdd.setText(bundle.getString("btn.add"));
        btnDelete.setText(bundle.getString("btn.delete"));
        radioFile.setText(bundle.getString("radio.file"));
        radioFolder.setText(bundle.getString("radio.folder"));

        // Показываем/скрываем поле размера в зависимости от типа
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            rowSize.setVisible(newVal == radioFile);
            rowSize.setManaged(newVal == radioFile);
        });

        buildSampleTree();
        // Отображаем имя элемента вместо toString()
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(FileSystemItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
            }
        });
    }

    /** Строит демонстрационное дерево при старте */
    private void buildSampleTree() {
        FolderItem root = new FolderItem("Мой компьютер");

        FolderItem documents = new FolderItem("Документы");
        documents.add(new FileItem("отчёт.docx",   45_000));
        documents.add(new FileItem("диплом.pdf",  2_400_000));
        documents.add(new FileItem("заметки.txt",     1_200));

        FolderItem photos = new FolderItem("Фото");
        photos.add(new FileItem("отпуск.jpg",  3_500_000));
        photos.add(new FileItem("семья.jpg",   4_100_000));

        FolderItem projects = new FolderItem("Проекты");
        FolderItem lab = new FolderItem("book-library-dao");
        lab.add(new FileItem("pom.xml",      2_048));
        lab.add(new FileItem("README.md",    5_120));
        projects.add(lab);

        root.add(documents);
        root.add(photos);
        root.add(projects);
        root.add(new FileItem("notes.txt", 512));

        TreeItem<FileSystemItem> rootItem = buildTreeItem(root);
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        updateTotalSize();

        treeView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> updateTotalSize()
        );
    }

    /** Рекурсивно строит TreeItem из FileSystemItem */
    private TreeItem<FileSystemItem> buildTreeItem(FileSystemItem item) {
        TreeItem<FileSystemItem> treeItem = new TreeItem<>(item);
        treeItem.setExpanded(true);
        if (item instanceof FolderItem folder) {
            for (FileSystemItem child : folder.getChildren()) {
                treeItem.getChildren().add(buildTreeItem(child));
            }
        }
        return treeItem;
    }

    @FXML
    public void onAdd() {
        String name = fieldName.getText().trim();
        if (name.isEmpty()) {
            showError(bundle.getString("error.emptyName"));
            return;
        }

        // Находим выбранную папку
        TreeItem<FileSystemItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
        TreeItem<FileSystemItem> targetItem = findTargetFolder(selectedItem);

        if (targetItem == null) {
            showError(bundle.getString("error.selectFolder"));
            return;
        }

        FolderItem targetFolder = (FolderItem) targetItem.getValue();

        try {
            FileSystemItem newItem;
            if (radioFile.isSelected()) {
                long size = parseSize();
                newItem = new FileItem(name, size);
            } else {
                newItem = new FolderItem(name);
            }

            targetFolder.add(newItem);
            targetItem.getChildren().add(buildTreeItem(newItem));
            targetItem.setExpanded(true);

            // Обновляем отображение всех предков (размер изменился)
            refreshTree();
            log(bundle.getString("log.added") + ": " + newItem.getDisplayName()
                    + " → " + targetFolder.getName());
            fieldName.clear();
            fieldSize.clear();

        } catch (FileSystemException e) {
            showError(e.getMessage());
            log(bundle.getString("log.error") + ": " + e.getMessage());
        }
    }

    @FXML
    public void onDelete() {
        TreeItem<FileSystemItem> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getParent() == null) {
            showError(bundle.getString("error.selectItem"));
            return;
        }

        TreeItem<FileSystemItem> parentItem = selected.getParent();
        FolderItem parentFolder = (FolderItem) parentItem.getValue();
        FileSystemItem toDelete = selected.getValue();

        try {
            parentFolder.remove(toDelete.getName());
            parentItem.getChildren().remove(selected);
            refreshTree();
            log(bundle.getString("log.deleted") + ": " + toDelete.getDisplayName());
        } catch (FileSystemException e) {
            showError(e.getMessage());
        }
    }

    /** Находит ближайшую папку — либо выбранный элемент, либо его родитель */
    private TreeItem<FileSystemItem> findTargetFolder(TreeItem<FileSystemItem> item) {
        if (item == null) return treeView.getRoot();
        if (item.getValue().isFolder()) return item;
        return item.getParent();
    }

    /** Парсит размер из текстового поля */
    private long parseSize() {
        String text = fieldSize.getText().trim();
        if (text.isEmpty()) return 0;
        try {
            long val = Long.parseLong(text);
            if (val < 0) throw new FileSystemException(bundle.getString("error.negativeSize"));
            return val;
        } catch (NumberFormatException e) {
            throw new FileSystemException(bundle.getString("error.invalidSize"));
        }
    }

    /** Обновляет корень дерева для пересчёта размеров */
    private void refreshTree() {
        FileSystemItem rootVal = treeView.getRoot().getValue();
        TreeItem<FileSystemItem> newRoot = buildTreeItem(rootVal);
        newRoot.setExpanded(true);
        treeView.setRoot(newRoot);
        updateTotalSize();
    }

    private void updateTotalSize() {
        if (treeView.getRoot() != null) {
            long total = treeView.getRoot().getValue().getSize();
            labelTotalSize.setText(bundle.getString("label.total") + ": "
                    + FileItem.formatSize(total));
        }
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(bundle.getString("dialog.warning"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
