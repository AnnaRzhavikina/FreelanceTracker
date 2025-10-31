package com.freelance.dao;

import com.freelance.model.TimeEntry;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс Data Access Object для работы с записями времени в базе данных.
 * <p>
 * Определяет основные операции CRUD и дополнительные методы поиска записей времени.
 * </p>
 *
 */
public interface TimeEntryDAO {
    /**
     * Получает все записи времени из базы данных.
     *
     * @return список всех записей времени
     */
    List<TimeEntry> findAll();
    
    /**
     * Находит запись времени по её идентификатору.
     *
     * @param id уникальный идентификатор записи
     * @return Optional с записью времени, если найдена, иначе пустой Optional
     */
    Optional<TimeEntry> findById(Long id);
    
    /**
     * Сохраняет новую запись времени в базе данных.
     *
     * @param timeEntry запись времени для сохранения
     * @return сохраненная запись времени с присвоенным идентификатором
     */
    TimeEntry save(TimeEntry timeEntry);
    
    /**
     * Обновляет существующую запись времени в базе данных.
     *
     * @param timeEntry запись времени с обновленными данными
     */
    void update(TimeEntry timeEntry);
    
    /**
     * Удаляет запись времени из базы данных по её идентификатору.
     *
     * @param id идентификатор записи для удаления
     */
    void delete(Long id);
    
    /**
     * Находит все записи времени для определенного проекта.
     *
     * @param projectId идентификатор проекта
     * @return список записей времени для проекта
     */
    List<TimeEntry> findByProjectId(Long projectId);
}
