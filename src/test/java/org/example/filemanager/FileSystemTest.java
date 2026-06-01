package org.example.filemanager;

import org.example.filemanager.composite.FolderItem;
import org.example.filemanager.exception.FileSystemException;
import org.example.filemanager.leaf.FileItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {

    private FolderItem root;
    private FolderItem docs;
    private FileItem file1;
    private FileItem file2;

    @BeforeEach
    void setUp() {
        root  = new FolderItem("root");
        docs  = new FolderItem("docs");
        file1 = new FileItem("readme.txt", 1024);
        file2 = new FileItem("report.pdf", 2048);
    }

    // ─── getSize() ────────────────────────────────────────────────────

    @Test
    @DisplayName("getSize файла возвращает точный размер")
    void testFileSize() {
        assertEquals(1024, file1.getSize());
    }

    @Test
    @DisplayName("getSize пустой папки равен 0")
    void testEmptyFolderSize() {
        assertEquals(0, docs.getSize());
    }

    @Test
    @DisplayName("getSize папки = сумма размеров дочерних файлов")
    void testFolderSizeSum() {
        docs.add(file1);
        docs.add(file2);
        assertEquals(1024 + 2048, docs.getSize());
    }

    @Test
    @DisplayName("getSize рекурсивно считает вложенные папки")
    void testNestedFolderSize() {
        docs.add(file1);   // 1024
        docs.add(file2);   // 2048
        root.add(docs);
        root.add(new FileItem("extra.txt", 512));
        // root = docs(3072) + extra(512) = 3584
        assertEquals(3584, root.getSize());
    }

    // ─── Исключения: FileItem ──────────────────────────────────────────

    @Test
    @DisplayName("FileItem: пустое имя → FileSystemException")
    void testFileEmptyName() {
        assertThrows(FileSystemException.class, () -> new FileItem("", 100));
    }

    @Test
    @DisplayName("FileItem: null имя → FileSystemException")
    void testFileNullName() {
        assertThrows(FileSystemException.class, () -> new FileItem(null, 100));
    }

    @Test
    @DisplayName("FileItem: отрицательный размер → FileSystemException")
    void testFileNegativeSize() {
        FileSystemException ex = assertThrows(
                FileSystemException.class,
                () -> new FileItem("file.txt", -1)
        );
        assertTrue(ex.getMessage().contains("отрицательным"));
    }

    // ─── Исключения: FolderItem ────────────────────────────────────────

    @Test
    @DisplayName("FolderItem: пустое имя → FileSystemException")
    void testFolderEmptyName() {
        assertThrows(FileSystemException.class, () -> new FolderItem(""));
    }

    @Test
    @DisplayName("FolderItem: добавление null → FileSystemException")
    void testAddNull() {
        FileSystemException ex = assertThrows(
                FileSystemException.class,
                () -> root.add(null)
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    @DisplayName("FolderItem: дублирование имени → FileSystemException")
    void testAddDuplicate() {
        root.add(file1);
        FileSystemException ex = assertThrows(
                FileSystemException.class,
                () -> root.add(new FileItem("readme.txt", 500))
        );
        assertTrue(ex.getMessage().contains("уже существует"));
    }

    @Test
    @DisplayName("FolderItem: удаление несуществующего → FileSystemException")
    void testRemoveNotFound() {
        FileSystemException ex = assertThrows(
                FileSystemException.class,
                () -> root.remove("nonexistent.txt")
        );
        assertTrue(ex.getMessage().contains("не найден"));
    }

    @Test
    @DisplayName("FolderItem: успешное удаление уменьшает размер")
    void testRemoveUpdatesSize() {
        root.add(file1); // 1024
        root.add(file2); // 2048
        root.remove("readme.txt");
        assertEquals(2048, root.getSize());
    }

    // ─── isFolder() ────────────────────────────────────────────────────

    @Test
    @DisplayName("FileItem.isFolder() возвращает false")
    void testFileIsNotFolder() {
        assertFalse(file1.isFolder());
    }

    @Test
    @DisplayName("FolderItem.isFolder() возвращает true")
    void testFolderIsFolder() {
        assertTrue(root.isFolder());
    }
}
