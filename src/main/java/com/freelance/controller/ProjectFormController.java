package com.freelance.controller;

import com.freelance.MainApp;
import com.freelance.model.Project;
import com.freelance.service.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
/**
 * Контроллер формы создания/редактирования проекта.
 * <p>
 * Обрабатывает ввод данных пользователя и сохранение проекта в базу данных.
 * </p>
 */

public class ProjectFormController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField clientField;
    @FXML
    private TextField rateField;
    @FXML
    private TextField hoursField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextArea descriptionArea;
    private final ProjectService projectService;
    private Project editingProject;
    /**
     * Конструктор по умолчанию.
     * Создает экземпляр ProjectService и получает проект для редактирования из MainApp.
     */
    public ProjectFormController() {
        this.projectService = new ProjectService();
        this.editingProject = MainApp.getEditingProject();
    }
    /**
     * Инициализация контроллера.
     * Настраивает выбор статуса проекта и заполняет форму при редактировании.
     */
    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("active", "paused", "completed");
        statusComboBox.setValue("active");

        if (editingProject != null) {
            populateFormFields();
        }
    }

    /**
     * Заполняет поля формы данными из проекта.
     * Вызывается в initialize() если редактируется существующий проект.
     */
    private void populateFormFields() {
        if (editingProject != null) {
            nameField.setText(editingProject.getName());
            clientField.setText(editingProject.getClient());
            rateField.setText(String.valueOf(editingProject.getHourlyRate()));
            hoursField.setText(String.valueOf(editingProject.getHoursWorked()));
            statusComboBox.setValue(editingProject.getStatus());
            startDatePicker.setValue(editingProject.getStartDate());
            endDatePicker.setValue(editingProject.getEndDate());
            descriptionArea.setText(editingProject.getDescription());
        }
    }
    // Остальные методы остаются БЕЗ ИЗМЕНЕНИЙ
    // (handleSave, validateInput, handleCancel, showAlert)
    @FXML
    private void handleSave() throws IOException {
        if (validateInput()) {
            Project project = editingProject != null ? editingProject : new Project();
            project.setName(nameField.getText().trim());
            project.setClient(clientField.getText().trim());
            project.setHourlyRate(Double.parseDouble(rateField.getText().trim()));
            project.setHoursWorked(Double.parseDouble(hoursField.getText().trim()));
            project.setStatus(statusComboBox.getValue());
            project.setStartDate(startDatePicker.getValue());
            project.setEndDate(endDatePicker.getValue());
            project.setDescription(descriptionArea.getText().trim());
            if (editingProject == null) {
                projectService.createProject(project);
            } else {
                projectService.updateProject(project);
            }
            handleCancel();
        }
    }
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (nameField.getText().trim().isEmpty()) {
            errors.append("- Название проекта обязательно\n");
        }
        if (clientField.getText().trim().isEmpty()) {
            errors.append("- Имя клиента обязательно\n");
        }
        try {
            double rate = Double.parseDouble(rateField.getText().trim());
            if (rate <= 0) {
                errors.append("- Ставка должна быть больше нуля\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Неверный формат ставки\n");
        }
        try {
            double hours = Double.parseDouble(hoursField.getText().trim());
            if (hours < 0) {
                errors.append("- Часы не могут быть отрицательными\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Неверный формат часов\n");
        }
        if (errors.length() > 0) {
            showAlert("Ошибки валидации", errors.toString());
            return false;
        }
        return true;
    }
    @FXML
    private void handleCancel() throws IOException {
        MainApp.loadScene("/fxml/projects.fxml", "Проекты");
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
