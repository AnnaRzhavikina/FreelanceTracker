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
 *
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
     * Конструктор с инъекцией зависимости ProjectService.
     *
     * @param projectService сервис для работы с проектами
     */
    public ProjectFormController(ProjectService projectService) {
        this.projectService = projectService;
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
     * Устанавливает проект для редактирования.
     * Вызывается из MainApp до инициализации FXML полей.
     *
     * @param project проект для редактирования
     */
    public void setEditingProject(Project project) {
        this.editingProject = project;
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

    /**
     * Сохраняет проект в базу данных.
     * Обработчик кнопки "Сохранить".
     *
     * @throws IOException если не удается вернуться к списку проектов
     */
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

    /**
     * Проверяет корректность введенных данных.
     *
     * @return true если данные валидны, иначе false
     */
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

    /**
     * Отменяет создание/редактирование и возвращается к списку проектов.
     * Обработчик кнопки "Отмена".
     *
     * @throws IOException если не удается загрузить FXML
     */
    @FXML
    private void handleCancel() throws IOException {
        MainApp.loadScene("/fxml/projects.fxml", "Проекты");
    }

    /**
     * Отображает диалог с сообщением об ошибке.
     *
     * @param title заголовок диалога
     * @param message текст сообщения
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
