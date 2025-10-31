package com.freelance.dao;

import com.freelance.model.Project;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс Data Access Object для работы с проектами в базе данных.
 * <p>
 * Определяет основные операции CRUD и дополнительные методы поиска проектов.
 * </p>
 *
 */
public interface ProjectDAO {
    /**
     * Получает все проекты из базы данных.
     *
     * @return список всех проектов
     */
    List<Project> findAll();
    
    /**
     * Находит проект по его идентификатору.
     *
     * @param id уникальный идентификатор проекта
     * @return Optional с проектом, если найден, иначе пустой Optional
     */
    Optional<Project> findById(Long id);
    
    /**
     * Сохраняет новый проект в базе данных.
     *
     * @param project проект для сохранения
     * @return сохраненный проект с присвоенным идентификатором
     */
    Project save(Project project);
    
    /**
     * Обновляет существующий проект в базе данных.
     *
     * @param project проект с обновленными данными
     */
    void update(Project project);
    
    /**
     * Удаляет проект из базы данных по его идентификатору.
     *
     * @param id идентификатор проекта для удаления
     */
    void delete(Long id);
    
    /**
     * Находит все проекты с определенным статусом.
     *
     * @param status статус проекта (active, paused, completed)
     * @return список проектов с заданным статусом
     */
    List<Project> findByStatus(String status);
    
    /**
     * Находит все проекты определенного клиента.
     *
     * @param client имя клиента
     * @return список проектов клиента
     */
    List<Project> findByClient(String client);
}
