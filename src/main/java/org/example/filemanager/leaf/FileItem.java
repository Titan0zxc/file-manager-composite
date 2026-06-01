package org.example.filemanager.leaf;

import org.example.filemanager.component.FileSystemItem;
import org.example.filemanager.exception.FileSystemException;

/**
 * Лист паттерна Компоновщик — простой файл.
 * Не может содержать дочерних элементов.
 */
public class FileItem implements FileSystemItem {

    private final String name;
    private final long size; // в байтах

    public FileItem(String name, long size) {
        if (name == null || name.isBlank()) {
            throw new FileSystemException("Имя файла не может быть пустым");
        }
        if (size < 0) {
            throw new FileSystemException("Размер файла не может быть отрицательным: " + size);
        }
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getDisplayName() {
        return "📄 " + name + "  [" + formatSize(size) + "]";
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    /** Форматирует размер в удобочитаемый вид */
    public static String formatSize(long bytes) {
        if (bytes < 1024)       return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
