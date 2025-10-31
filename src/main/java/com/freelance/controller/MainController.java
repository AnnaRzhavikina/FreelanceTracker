package com.freelance.controller;

import com.freelance.MainApp;
import com.freelance.service.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;

/**
 * Контроллер главного экрана приложения.
 * <p>
 * Отображает панель управления с общей статистикой по проектам,
 * рентабельностью и загруженностью по неделям.
 * </p>
 *
 */
public class MainController {

    @FXML
    private Label totalProjectsLabel;

    @FXML
    private Label activeProjectsLabel;

    @FXML
    private Label completedProjectsLabel;

    @FXML
    private Label totalRevenueCardLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label totalHoursLabel;

    @FXML
    private Label avgRateLabel;

    @FXML
    private VBox workloadContainer;

    private final ProjectService projectService;

    /**
     * Конструктор с инъекцией зависимости ProjectService.
     *
     * @param projectService сервис для работы с проектами
     */
    public MainController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Инициализация контроллера.
     * Автоматически вызывается после загрузки FXML.
     */
    @FXML
    public void initialize() {
        loadStatistics();
    }

    /**
     * Загружает и отображает статистику проектов.
     */
    private void loadStatistics() {
        Map<String, Object> stats = projectService.getProjectStatistics();
        Map<String, Object> profitability = projectService.calculateOverallProfitability();

        totalProjectsLabel.setText(String.valueOf(stats.get("totalProjects")));
        activeProjectsLabel.setText(String.valueOf(stats.get("activeProjects")));
        completedProjectsLabel.setText(String.valueOf(stats.get("completedProjects")));

        totalRevenueCardLabel.setText(String.format("%.2f ₽", profitability.get("totalRevenue")));
        totalHoursLabel.setText(String.format("%.1f ч", profitability.get("totalHours")));
        avgRateLabel.setText(String.format("%.2f ₽/ч", profitability.get("averageHourlyRate")));

        totalRevenueLabel.setText(String.format("%.2f ₽", profitability.get("totalRevenue")));
        totalHoursLabel.setText(String.format("%.1f ч", profitability.get("totalHours")));
        avgRateLabel.setText(String.format("%.2f ₽/ч", profitability.get("averageHourlyRate")));

        loadWorkload();
    }

    /**
     * Загружает информацию о загруженности по неделям.
     */
    private void loadWorkload() {
        Map<String, Double> workload = projectService.getWorkloadByWeek();
        workloadContainer.getChildren().clear();

        for (Map.Entry<String, Double> entry : workload.entrySet()) {
            Label weekLabel = new Label(entry.getKey() + ": " + 
                String.format("%.1f часов", entry.getValue()));
            weekLabel.getStyleClass().add("workload-item");
            workloadContainer.getChildren().add(weekLabel);
        }
    }

    /**
     * Открывает окно со списком проектов.
     * Обработчик кнопки "Проекты".
     *
     * @throws IOException если не удается загрузить FXML
     */
    @FXML
    private void handleProjects() throws IOException {
        MainApp.loadScene("/fxml/projects.fxml", "Проекты");
    }

    /**
     * Обновляет отображаемую статистику.
     * Обработчик кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        loadStatistics();
    }
}
