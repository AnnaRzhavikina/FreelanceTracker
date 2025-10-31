package com.freelance.model;

import java.time.LocalDate;

/**
 * Модель данных для представления фриланс-проекта.
 * <p>
 * Класс содержит всю информацию о проекте, включая данные о клиенте,
 * ставке, отработанных часах, статусе и датах выполнения.
 * </p>
 *
 */
public class Project {
    /** Уникальный идентификатор проекта */
    private Long id;
    
    /** Название проекта */
    private String name;
    
    /** Имя клиента */
    private String client;
    
    /** Почасовая ставка в рублях */
    private double hourlyRate;
    
    /** Количество отработанных часов */
    private double hoursWorked;
    
    /** Статус проекта (active, paused, completed) */
    private String status;
    
    /** Дата начала проекта */
    private LocalDate startDate;
    
    /** Дата окончания проекта */
    private LocalDate endDate;
    
    /** Описание проекта */
    private String description;

    /**
     * Конструктор по умолчанию.
     */
    public Project() {
    }

    /**
     * Конструктор с параметрами для создания полностью инициализированного проекта.
     *
     * @param id уникальный идентификатор
     * @param name название проекта
     * @param client имя клиента
     * @param hourlyRate почасовая ставка
     * @param hoursWorked отработанные часы
     * @param status статус проекта
     * @param startDate дата начала
     * @param endDate дата окончания
     * @param description описание проекта
     */
    public Project(Long id, String name, String client, double hourlyRate, 
                   double hoursWorked, String status, LocalDate startDate, 
                   LocalDate endDate, String description) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    /**
     * Рассчитывает общий доход от проекта.
     *
     * @return доход (ставка × отработанные часы)
     */
    public double calculateRevenue() {
        return hourlyRate * hoursWorked;
    }

    /**
     * Рассчитывает эффективность проекта.
     * В текущей реализации возвращает почасовую ставку.
     *
     * @return эффективность проекта
     */
    public double calculateEfficiency() {
        if (hoursWorked == 0) return 0;
        return hourlyRate;
    }

    /**
     * Возвращает уникальный идентификатор проекта.
     *
     * @return идентификатор проекта
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор проекта.
     *
     * @param id идентификатор проекта
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название проекта.
     *
     * @return название проекта
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название проекта.
     *
     * @param name название проекта
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя клиента.
     *
     * @return имя клиента
     */
    public String getClient() {
        return client;
    }

    /**
     * Устанавливает имя клиента.
     *
     * @param client имя клиента
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Возвращает почасовую ставку.
     *
     * @return почасовая ставка в рублях
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Устанавливает почасовую ставку.
     *
     * @param hourlyRate почасовая ставка в рублях
     */
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * Возвращает количество отработанных часов.
     *
     * @return отработанные часы
     */
    public double getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Устанавливает количество отработанных часов.
     *
     * @param hoursWorked отработанные часы
     */
    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    /**
     * Возвращает статус проекта.
     *
     * @return статус проекта (active, paused, completed)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус проекта.
     *
     * @param status статус проекта (active, paused, completed)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Возвращает дату начала проекта.
     *
     * @return дата начала проекта
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Устанавливает дату начала проекта.
     *
     * @param startDate дата начала проекта
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Возвращает дату окончания проекта.
     *
     * @return дата окончания проекта
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Устанавливает дату окончания проекта.
     *
     * @param endDate дата окончания проекта
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Возвращает описание проекта.
     *
     * @return описание проекта
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание проекта.
     *
     * @param description описание проекта
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
