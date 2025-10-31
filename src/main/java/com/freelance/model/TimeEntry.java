package com.freelance.model;

import java.time.LocalDateTime;

/**
 * Модель данных для записи времени работы над проектом.
 * <p>
 * Класс представляет отдельную запись о времени, затраченном на проект,
 * с указанием начала, окончания работы и описания выполненных задач.
 * </p>
 *
 */
public class TimeEntry {
    /** Уникальный идентификатор записи времени */
    private Long id;
    
    /** Идентификатор проекта, к которому относится запись */
    private Long projectId;
    
    /** Время начала работы */
    private LocalDateTime startTime;
    
    /** Время окончания работы */
    private LocalDateTime endTime;
    
    /** Количество часов работы */
    private double hours;
    
    /** Описание выполненной работы */
    private String description;

    /**
     * Конструктор по умолчанию.
     */
    public TimeEntry() {
    }

    /**
     * Конструктор с параметрами для создания полностью инициализированной записи времени.
     *
     * @param id уникальный идентификатор записи
     * @param projectId идентификатор проекта
     * @param startTime время начала работы
     * @param endTime время окончания работы
     * @param hours количество часов работы
     * @param description описание выполненной работы
     */
    public TimeEntry(Long id, Long projectId, LocalDateTime startTime, 
                    LocalDateTime endTime, double hours, String description) {
        this.id = id;
        this.projectId = projectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hours = hours;
        this.description = description;
    }

    /**
     * Возвращает уникальный идентификатор записи времени.
     *
     * @return идентификатор записи
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор записи времени.
     *
     * @param id идентификатор записи
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор проекта.
     *
     * @return идентификатор проекта
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Устанавливает идентификатор проекта.
     *
     * @param projectId идентификатор проекта
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Возвращает время начала работы.
     *
     * @return время начала работы
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Устанавливает время начала работы.
     *
     * @param startTime время начала работы
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Возвращает время окончания работы.
     *
     * @return время окончания работы
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Устанавливает время окончания работы.
     *
     * @param endTime время окончания работы
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Возвращает количество часов работы.
     *
     * @return количество часов
     */
    public double getHours() {
        return hours;
    }

    /**
     * Устанавливает количество часов работы.
     *
     * @param hours количество часов
     */
    public void setHours(double hours) {
        this.hours = hours;
    }

    /**
     * Возвращает описание выполненной работы.
     *
     * @return описание работы
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание выполненной работы.
     *
     * @param description описание работы
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
