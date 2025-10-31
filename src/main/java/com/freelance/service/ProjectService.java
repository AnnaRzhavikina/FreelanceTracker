package com.freelance.service;

import com.freelance.dao.ProjectDAO;
import com.freelance.dao.TimeEntryDAO;
import com.freelance.dao.impl.SQLiteProjectDAO;
import com.freelance.dao.impl.SQLiteTimeEntryDAO;
import com.freelance.model.Project;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления проектами и расчета бизнес-метрик.
 * <p>
 * Предоставляет методы для CRUD операций с проектами, расчета рентабельности,
 * планирования нагрузки и выявления переработок.
 * </p>
 *
 */
public class ProjectService {
    
    private final ProjectDAO projectDAO;
    private final TimeEntryDAO timeEntryDAO;

    /**
     * Конструктор по умолчанию с инициализацией DAO.
     */
    public ProjectService() {
        this.projectDAO = new SQLiteProjectDAO();
        this.timeEntryDAO = new SQLiteTimeEntryDAO();
    }

    /**
     * Конструктор с инъекцией зависимостей.
     *
     * @param projectDAO DAO для работы с проектами
     * @param timeEntryDAO DAO для работы с записями времени
     */
    public ProjectService(ProjectDAO projectDAO, TimeEntryDAO timeEntryDAO) {
        this.projectDAO = projectDAO;
        this.timeEntryDAO = timeEntryDAO;
    }

    /**
     * Получает все проекты из базы данных.
     *
     * @return список всех проектов
     */
    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    /**
     * Находит проект по идентификатору.
     *
     * @param id уникальный идентификатор проекта
     * @return Optional с проектом, если найден
     */
    public Optional<Project> getProjectById(Long id) {
        return projectDAO.findById(id);
    }

    /**
     * Создает новый проект с автоматической установкой статуса и даты начала.
     *
     * @param project новый проект для создания
     * @return созданный проект с присвоенным ID
     */
    public Project createProject(Project project) {
        if (project.getStatus() == null) {
            project.setStatus("active");
        }
        if (project.getStartDate() == null) {
            project.setStartDate(LocalDate.now());
        }
        return projectDAO.save(project);
    }

    /**
     * Обновляет существующий проект.
     *
     * @param project проект с обновленными данными
     */
    public void updateProject(Project project) {
        projectDAO.update(project);
    }

    /**
     * Удаляет проект по идентификатору.
     *
     * @param id идентификатор проекта для удаления
     */
    public void deleteProject(Long id) {
        projectDAO.delete(id);
    }

    /**
     * Получает все активные проекты.
     *
     * @return список активных проектов
     */
    public List<Project> getActiveProjects() {
        return projectDAO.findByStatus("active");
    }

    /**
     * Рассчитывает рентабельность конкретного проекта.
     *
     * @param projectId идентификатор проекта
     * @return карта с показателями рентабельности (название, доход, часы, ставка, эффективность)
     */
    public Map<String, Object> calculateProfitability(Long projectId) {
        Map<String, Object> result = new HashMap<>();
        Optional<Project> projectOpt = projectDAO.findById(projectId);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            double revenue = project.calculateRevenue();
            double hoursWorked = project.getHoursWorked();
            double hourlyRate = project.getHourlyRate();
            
            result.put("projectName", project.getName());
            result.put("totalRevenue", revenue);
            result.put("hoursWorked", hoursWorked);
            result.put("hourlyRate", hourlyRate);
            result.put("efficiency", hourlyRate);
        }
        
        return result;
    }

    /**
     * Рассчитывает общую рентабельность по всем проектам.
     *
     * @return карта с общими показателями (общий доход, общие часы, средняя ставка, количество проектов)
     */
    public Map<String, Object> calculateOverallProfitability() {
        List<Project> allProjects = projectDAO.findAll();
        
        double totalRevenue = allProjects.stream()
                .mapToDouble(Project::calculateRevenue)
                .sum();
        
        double totalHours = allProjects.stream()
                .mapToDouble(Project::getHoursWorked)
                .sum();
        
        double averageRate = totalHours > 0 ? totalRevenue / totalHours : 0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("totalHours", totalHours);
        result.put("averageHourlyRate", averageRate);
        result.put("projectCount", allProjects.size());
        
        return result;
    }

    /**
     * Рассчитывает загруженность по неделям для активных проектов.
     *
     * @return карта недель с количеством часов работы
     */
    public Map<String, Double> getWorkloadByWeek() {
        List<Project> activeProjects = getActiveProjects();
        Map<String, Double> weeklyWorkload = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        
        for (int i = 0; i < 4; i++) {
            LocalDate weekStart = startOfWeek.plusWeeks(i);
            String weekLabel = "Неделя " + weekStart.toString();
            
            double weekHours = activeProjects.stream()
                    .filter(p -> p.getStartDate() != null && 
                           !p.getStartDate().isAfter(weekStart.plusDays(7)))
                    .mapToDouble(p -> p.getHoursWorked() / 4.0)
                    .sum();
            
            weeklyWorkload.put(weekLabel, weekHours);
        }
        
        return weeklyWorkload;
    }

    /**
     * Проверяет наличие переработок (более 40 часов в неделю).
     *
     * @return список предупреждений о переработках
     */
    public List<String> checkForOverwork() {
        List<String> warnings = new ArrayList<>();
        Map<String, Double> weeklyWorkload = getWorkloadByWeek();
        
        for (Map.Entry<String, Double> entry : weeklyWorkload.entrySet()) {
            if (entry.getValue() > 40) {
                warnings.add("ПРЕДУПРЕЖДЕНИЕ: " + entry.getKey() + 
                           " - переработка (" + String.format("%.1f", entry.getValue()) + " часов)");
            }
        }
        
        return warnings;
    }

    /**
     * Получает общую статистику по проектам.
     *
     * @return карта со статистикой (всего проектов, активных, завершенных, по клиентам)
     */
    public Map<String, Object> getProjectStatistics() {
        List<Project> allProjects = projectDAO.findAll();
        
        long activeCount = allProjects.stream()
                .filter(p -> "active".equals(p.getStatus()))
                .count();
        
        long completedCount = allProjects.stream()
                .filter(p -> "completed".equals(p.getStatus()))
                .count();
        
        Map<String, Long> clientStats = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getClient, Collectors.counting()));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProjects", allProjects.size());
        stats.put("activeProjects", activeCount);
        stats.put("completedProjects", completedCount);
        stats.put("clientBreakdown", clientStats);
        
        return stats;
    }
}
