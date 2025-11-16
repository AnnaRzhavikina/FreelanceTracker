package com.freelance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.freelance.model.Project;

import java.io.IOException;

/**
 * Главный класс приложения для трекинга фриланс-проектов.
 * <p>
 * Этот класс инициализирует JavaFX приложение и загружает главное окно.
 * Контроллеры создаются автоматически JavaFX Framework.
 * </p>
 */
public class MainApp extends Application {

    private static Stage primaryStage;
    private static Project editingProject;

    /**
     * Главный метод запуска приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Возвращает главную сцену приложения.
     *
     * @return главный Stage приложения
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Возвращает проект для редактирования.
     *
     * @return проект для редактирования или null
     */
    public static Project getEditingProject() {
        return editingProject;
    }

    /**
     * Метод инициализации JavaFX приложения.
     * Загружает главное окно из FXML файла и устанавливает сцену.
     *
     * @param stage главная сцена приложения
     * @throws IOException если не удается загрузить FXML файл
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Трекер фриланс-проектов");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();
    }

    /**
     * Загружает новую сцену из указанного FXML файла.
     *
     * @param fxmlPath путь к FXML файлу
     * @param title заголовок окна
     * @throws IOException если не удается загрузить FXML файл
     */
    public static void loadScene(String fxmlPath, String title) throws IOException {
        editingProject = null;

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }

    /**
     * Загружает новую сцену с передачей данных проекта для редактирования.
     *
     * @param fxmlPath путь к FXML файлу
     * @param title заголовок окна
     * @param project проект для редактирования
     * @throws IOException если не удается загрузить FXML файл
     */
    public static void loadSceneWithProject(String fxmlPath, String title, Project project) throws IOException {
        editingProject = project;

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }
}
