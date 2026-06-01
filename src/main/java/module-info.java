module org.example.filemanager {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.filemanager to javafx.fxml;
    opens org.example.filemanager.controller to javafx.fxml;
    opens org.example.filemanager.component to javafx.base;
    opens org.example.filemanager.composite to javafx.base;
    opens org.example.filemanager.leaf to javafx.base;

    exports org.example.filemanager;
}
