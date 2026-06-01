package org.example.filemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FileManagerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                FileManagerApp.class.getResource("file-manager-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 780, 560);
        stage.setTitle("File Manager — Composite Pattern");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
