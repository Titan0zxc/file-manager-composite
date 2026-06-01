package org.example.filemanager.composite;

import org.example.filemanager.component.FileSystemItem;
import org.example.filemanager.exception.FileSystemException;
import org.example.filemanager.leaf.FileItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Контейнер паттерна Компоновщик — папка.
 * Может содержать как файлы (листья), так и другие папки (контейнеры).
 */
public class FolderItem implements FileSystemItem {

    private final String name;
    private final List<FileSystemItem> children = new ArrayList<>();

    public FolderItem(String name) {
        if (name == null || name.isBlank()) {
            throw new FileSystemException("Имя папки не может быть пустым");
        }
        this.name = name;
    }

    /**
     * Добавляет дочерний элемент в папку.
     * @throws FileSystemException если элемент null или уже существует с таким именем
     */
    public void add(FileSystemItem item) {
        if (item == null) {
            throw new FileSystemException("Нельзя добавить null в папку \"" + name + "\"");
        }
        boolean duplicate = children.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(item.getName()));
        if (duplicate) {
            throw new FileSystemException(
                "Элемент с именем \"" + item.getName() + "\" уже существует в папке \"" + name + "\""
            );
        }
        children.add(item);
    }

    /**
     * Удаляет дочерний элемент по имени.
     * @throws FileSystemException если элемент не найден
     */
    public void remove(String itemName) {
        boolean removed = children.removeIf(
                c -> c.getName().equalsIgnoreCase(itemName)
        );
        if (!removed) {
            throw new FileSystemException(
                "Элемент \"" + itemName + "\" не найден в папке \"" + name + "\""
            );
        }
    }

    /** Возвращает неизменяемый список дочерних элементов */
    public List<FileSystemItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Рекурсивно вычисляет суммарный размер всех файлов в папке.
     */
    @Override
    public long getSize() {
        return children.stream()
                .mapToLong(FileSystemItem::getSize)
                .sum();
    }

    @Override
    public String getDisplayName() {
        return "📁 " + name + "  [" + FileItem.formatSize(getSize()) + "]";
    }

    @Override
    public boolean isFolder() {
        return true;
    }
}
