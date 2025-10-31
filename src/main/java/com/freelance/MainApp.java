package com.freelance;

import com.freelance.controller.MainController;
import com.freelance.controller.ProjectFormController;
import com.freelance.controller.ProjectsController;
import com.freelance.dao.ProjectDAO;
import com.freelance.dao.TimeEntryDAO;
import com.freelance.dao.impl.SQLiteProjectDAO;
import com.freelance.dao.impl.SQLiteTimeEntryDAO;
import com.freelance.model.Project;
import com.freelance.service.PdfExportService;
import com.freelance.service.ProjectService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Главный класс приложения для трекинга фриланс-проектов.
 * <p>
 * Этот класс инициализирует JavaFX приложение, управляет зависимостями
 * и загружает главное окно. Использует controller factory паттерн для
 * dependency injection в контроллеры.
 * </p>
 *
 */
public class MainApp extends Application {

    private static Stage primaryStage;
    private static MainApp instance;
    
    private final ProjectDAO projectDAO;
    private final TimeEntryDAO timeEntryDAO;
    private final ProjectService projectService;
    private final PdfExportService pdfExportService;
    
    private Project editingProject;

    /**
     * Конструктор по умолчанию.
     * Инициализирует DAO и сервисы для dependency injection.
     */
    public MainApp() {
        this.projectDAO = new SQLiteProjectDAO();
        this.timeEntryDAO = new SQLiteTimeEntryDAO();
        this.projectService = new ProjectService(projectDAO, timeEntryDAO);
        this.pdfExportService = new PdfExportService(projectService);
        instance = this;
    }

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
        loader.setControllerFactory(createControllerFactory());
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
     * Загружает новую сцену из указанного FXML файла (статический метод).
     *
     * @param fxmlPath путь к FXML файлу
     * @param title заголовок окна
     * @throws IOException если не удается загрузить FXML файл
     */
    public static void loadScene(String fxmlPath, String title) throws IOException {
        if (instance == null) {
            throw new IllegalStateException("Application not initialized");
        }
        
        instance.editingProject = null;
        
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        loader.setControllerFactory(instance.createControllerFactory());
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
        if (instance == null) {
            throw new IllegalStateException("Application not initialized");
        }
        
        instance.editingProject = project;
        
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        loader.setControllerFactory(instance.createControllerFactory());
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }

    /**
     * Создает controller factory для dependency injection.
     * <p>
     * Factory определяет, какой контроллер создать и инжектит необходимые зависимости.
     * </p>
     *
     * @return Callback для создания контроллеров с зависимостями
     */
    private Callback<Class<?>, Object> createControllerFactory() {
        return controllerClass -> {
            if (controllerClass == MainController.class) {
                return new MainController(projectService);
            } else if (controllerClass == ProjectsController.class) {
                return new ProjectsController(projectService, pdfExportService);
            } else if (controllerClass == ProjectFormController.class) {
                ProjectFormController controller = new ProjectFormController(projectService);
                if (editingProject != null) {
                    controller.setEditingProject(editingProject);
                }
                return controller;
            }
            
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
            }
        };
    }
}
