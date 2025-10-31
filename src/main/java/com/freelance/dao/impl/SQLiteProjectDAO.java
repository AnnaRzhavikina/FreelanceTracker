package com.freelance.dao.impl;

import com.freelance.dao.ProjectDAO;
import com.freelance.model.Project;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для работы с проектами в SQLite базе данных.
 * <p>
 * Использует JDBC для выполнения SQL операций.
 * Автоматически создает таблицу проектов при инициализации.
 * </p>
 *
 */
public class SQLiteProjectDAO implements ProjectDAO {
    private static final String DB_URL = "jdbc:sqlite:data/freelance.db";

    public SQLiteProjectDAO() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS projects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "client TEXT NOT NULL," +
                    "hourly_rate REAL NOT NULL," +
                    "hours_worked REAL DEFAULT 0," +
                    "status TEXT DEFAULT 'active'," +
                    "start_date TEXT," +
                    "end_date TEXT," +
                    "description TEXT" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all projects", e);
        }
        return projects;
    }

    @Override
    public Optional<Project> findById(Long id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO projects (name, client, hourly_rate, hours_worked, " +
                    "status, start_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getClient());
            pstmt.setDouble(3, project.getHourlyRate());
            pstmt.setDouble(4, project.getHoursWorked());
            pstmt.setString(5, project.getStatus());
            pstmt.setString(6, project.getStartDate() != null ? project.getStartDate().toString() : null);
            pstmt.setString(7, project.getEndDate() != null ? project.getEndDate().toString() : null);
            pstmt.setString(8, project.getDescription());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                project.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving project", e);
        }
        return project;
    }

    @Override
    public void update(Project project) {
        String sql = "UPDATE projects SET name = ?, client = ?, hourly_rate = ?, " +
                    "hours_worked = ?, status = ?, start_date = ?, end_date = ?, " +
                    "description = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getClient());
            pstmt.setDouble(3, project.getHourlyRate());
            pstmt.setDouble(4, project.getHoursWorked());
            pstmt.setString(5, project.getStatus());
            pstmt.setString(6, project.getStartDate() != null ? project.getStartDate().toString() : null);
            pstmt.setString(7, project.getEndDate() != null ? project.getEndDate().toString() : null);
            pstmt.setString(8, project.getDescription());
            pstmt.setLong(9, project.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }

    @Override
    public List<Project> findByStatus(String status) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = ? ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding projects by status", e);
        }
        return projects;
    }

    @Override
    public List<Project> findByClient(String client) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE client LIKE ? ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + client + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding projects by client", e);
        }
        return projects;
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setClient(rs.getString("client"));
        project.setHourlyRate(rs.getDouble("hourly_rate"));
        project.setHoursWorked(rs.getDouble("hours_worked"));
        project.setStatus(rs.getString("status"));
        
        String startDate = rs.getString("start_date");
        if (startDate != null) {
            project.setStartDate(LocalDate.parse(startDate));
        }
        
        String endDate = rs.getString("end_date");
        if (endDate != null) {
            project.setEndDate(LocalDate.parse(endDate));
        }
        
        project.setDescription(rs.getString("description"));
        return project;
    }
}
