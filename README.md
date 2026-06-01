# File Manager — Composite Pattern

Лабораторная работа №2 — паттерн **Компоновщик (Composite)**.  
Десктопное JavaFX-приложение — визуальный файловый менеджер.

---

## Содержание

- [Описание](#описание)
- [Функционал](#функционал)
- [Архитектура](#архитектура)
- [Диаграммы](#диаграммы)
- [Запуск](#запуск)
- [Тесты](#тесты)

---

## Описание

Приложение визуализирует иерархическую файловую систему.  
Паттерн **Компоновщик** позволяет работать с файлами и папками через единый интерфейс `FileSystemItem` — клиентский код не различает лист и контейнер.

---

## Функционал

| Действие | Описание |
|----------|----------|
| Просмотр | Дерево файлов и папок с размерами |
| Добавление файла | Имя + размер в байтах |
| Добавление папки | Вложенные папки любой глубины |
| Удаление | Удаление выбранного элемента |
| Подсчёт размера | Папка рекурсивно суммирует размеры дочерних элементов |
| Ресурсный файл | Тексты кнопок и сообщений из `strings.properties` |

---

## Архитектура

```
src/main/java/org/example/filemanager/
├── component/
│   └── FileSystemItem.java      # Интерфейс — компонент паттерна
├── leaf/
│   └── FileItem.java            # Лист — простой файл
├── composite/
│   └── FolderItem.java          # Контейнер — папка с дочерними элементами
├── exception/
│   └── FileSystemException.java # Исключения файловой системы
├── controller/
│   └── FileManagerController.java
└── FileManagerApp.java          # Точка входа JavaFX
```

---

## Диаграммы

### Диаграмма классов (Composite)

```plantuml
@startuml
skinparam classAttributeIconSize 0

interface FileSystemItem {
    + getName(): String
    + getSize(): long
    + getDisplayName(): String
    + isFolder(): boolean
}

class FileItem {
    - name: String
    - size: long
    + getName(): String
    + getSize(): long
    + getDisplayName(): String
    + isFolder(): boolean
    + {static} formatSize(bytes: long): String
}

class FolderItem {
    - name: String
    - children: List<FileSystemItem>
    + add(item: FileSystemItem): void
    + remove(name: String): void
    + getChildren(): List<FileSystemItem>
    + getName(): String
    + getSize(): long
    + getDisplayName(): String
    + isFolder(): boolean
}

class FileSystemException {
    + FileSystemException(message: String)
}

class FileManagerController {
    - treeView: TreeView
    - bundle: ResourceBundle
    + initialize(): void
    + onAdd(): void
    + onDelete(): void
}

class FileManagerApp {
    + start(stage: Stage): void
    + main(args: String[]): void
}

FileItem      ..|> FileSystemItem
FolderItem    ..|> FileSystemItem
FolderItem    o-->  FileSystemItem : children
FolderItem    ..>   FileSystemException : throws
FileItem      ..>   FileSystemException : throws
FileManagerController --> FolderItem
FileManagerController --> FileItem
FileManagerApp --> FileManagerController
@enduml
```

---

### Use Case

```plantuml
@startuml
left to right direction
skinparam actorStyle awesome

actor "Пользователь" as user

rectangle "File Manager" {
    usecase "Просмотр дерева"      as UC1
    usecase "Добавить файл"        as UC2
    usecase "Добавить папку"       as UC3
    usecase "Удалить элемент"      as UC4
    usecase "Просмотр размера"     as UC5
}

user --> UC1
user --> UC2
user --> UC3
user --> UC4
UC1 .> UC5 : <<include>>
UC2 .> UC5 : <<include>>
@enduml
```

---

### Контекстная диаграмма

```plantuml
@startuml
actor "Пользователь" as user

rectangle "File Manager\n[JavaFX Desktop]" as system

file "strings.properties" as res

user   --> system : добавить / удалить элемент
system --> user   : дерево файлов, размеры
res    --> system : тексты интерфейса
@enduml
```

---

### Структура паттерна Компоновщик

```plantuml
@startuml
package "Паттерн Компоновщик" {
    interface Component {
        + getSize(): long
        + getName(): String
    }
    class Leaf {
        Файл (FileItem)
    }
    class Composite {
        Папка (FolderItem)
        + add(Component)
        + remove(String)
        + getChildren()
    }
    Composite o--> "0..*" Component : children
    Leaf      ..|> Component
    Composite ..|> Component
}
@enduml
```

---

## Запуск

### Через Maven

```bash
mvn javafx:run
```

### Run Configuration в IntelliJ IDEA

Main class:
```
org.example.filemanager.FileManagerApp
```

---

## Тесты

```bash
mvn test
```

Тест-класс `FileSystemTest` покрывает:

| Группа | Тесты |
|--------|-------|
| `getSize()` | файл, пустая папка, сумма, рекурсия |
| Исключения `FileItem` | пустое имя, null имя, отрицательный размер |
| Исключения `FolderItem` | пустое имя, добавление null, дубликат, удаление несуществующего |
| `isFolder()` | файл → false, папка → true |

---

## Критерии оценки

- [x] Индивидуальный дизайн (вариант 4 — файловый менеджер)
- [x] Обработка исключений в `FolderItem` и `FileItem`
- [x] Использование ресурсного файла `strings.properties`
- [x] Unit-тесты (JUnit 5)
- [x] Паттерн Компоновщик: единый интерфейс для файла и папки
