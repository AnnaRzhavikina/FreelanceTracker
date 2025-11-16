package com.freelance.controller;

import com.freelance.MainApp;
import com.freelance.model.Project;
import com.freelance.service.PdfExportService;
import com.freelance.service.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер окна списка проектов.
 * <p>
 * Управляет отображением списка проектов, их созданием,
 * редактированием, удалением и экспортом в PDF.
 * </p>
 */
public class ProjectsController {

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private TableColumn<Project, String> nameColumn;

    @FXML
    private TableColumn<Project, String> clientColumn;

    @FXML
    private TableColumn<Project, Double> rateColumn;

    @FXML
    private TableColumn<Project, Double> hoursColumn;

    @FXML
    private TableColumn<Project, String> statusColumn;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label totalHoursLabel;

    private final ProjectService projectService;
    private final PdfExportService pdfExportService;
    private ObservableList<Project> projectsList;

    /**
     * Конструктор по умолчанию.
     * Создает экземпляры сервисов для работы с данными.
     */
    public ProjectsController() {
        this.projectService = new ProjectService();
        this.pdfExportService = new PdfExportService(projectService);
    }

    /**
     * Инициализация контроллера.
     * Настраивает таблицу проектов и загружает данные.
     */
    @FXML
    public void initialize() {
        setupTable();
        loadProjects();
    }

    // Остальные методы остаются БЕЗ ИЗМЕНЕНИЙ
    // (setupTable, loadProjects, updateStatistics, handleNewProject,
    //  handleEdit, handleDelete, handleExportPdf, handleBack,
    //  showAlert, showInfo)

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        rateColumn.setCellFactory(column -> new TableCell<Project, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f ₽/ч", item));
            }
        });

        hoursColumn.setCellFactory(column -> new TableCell<Project, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.1f ч", item));
            }
        });
    }

    private void loadProjects() {
        List<Project> projects = projectService.getAllProjects();
        projectsList = FXCollections.observableArrayList(projects);
        projectsTable.setItems(projectsList);
        updateStatistics();
    }

    private void updateStatistics() {
        var profitability = projectService.calculateOverallProfitability();
        totalRevenueLabel.setText(String.format("%.2f ₽", profitability.get("totalRevenue")));
        totalHoursLabel.setText(String.format("%.1f ч", profitability.get("totalHours")));
    }

    @FXML
    private void handleNewProject() throws IOException {
        MainApp.loadScene("/fxml/project-form.fxml", "Новый проект");
    }

    @FXML
    private void handleEdit() throws IOException {
        Project selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MainApp.loadSceneWithProject("/fxml/project-form.fxml", "Редактировать проект", selected);
        } else {
            showAlert("Выберите проект", "Пожалуйста, выберите проект для редактирования.");
        }
    }

    @FXML
    private void handleDelete() {
        Project selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Подтверждение удаления");
            confirmation.setHeaderText("Удалить проект?");
            confirmation.setContentText("Вы уверены, что хотите удалить проект \"" + selected.getName() + "\"?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                projectService.deleteProject(selected.getId());
                loadProjects();
            }
        } else {
            showAlert("Выберите проект", "Пожалуйста, выберите проект для удаления.");
        }
    }

    @FXML
    private void handleExportPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет");
        fileChooser.setInitialFileName("freelance_report.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF файлы", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(MainApp.getPrimaryStage());
        if (file != null) {
            try {
                byte[] pdfBytes = pdfExportService.generateProjectReport();
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                }
                showInfo("Успешно", "Отчет сохранен в " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось сохранить PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() throws IOException {
        MainApp.loadScene("/fxml/main.fxml", "Трекер фриланс-проектов");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
