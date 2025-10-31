package com.freelance.dao.impl;

import com.freelance.dao.TimeEntryDAO;
import com.freelance.model.TimeEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для работы с записями времени в SQLite базе данных.
 * <p>
 * Использует JDBC для выполнения SQL операций.
 * Автоматически создает таблицу записей времени при инициализации.
 * </p>
 *
 */
public class SQLiteTimeEntryDAO implements TimeEntryDAO {
    private static final String DB_URL = "jdbc:sqlite:data/freelance.db";

    public SQLiteTimeEntryDAO() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS time_entries (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "project_id INTEGER NOT NULL," +
                    "start_time TEXT," +
                    "end_time TEXT," +
                    "hours REAL NOT NULL," +
                    "description TEXT," +
                    "FOREIGN KEY(project_id) REFERENCES projects(id)" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize time entries table", e);
        }
    }

    @Override
    public List<TimeEntry> findAll() {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                entries.add(mapResultSetToTimeEntry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all time entries", e);
        }
        return entries;
    }

    @Override
    public Optional<TimeEntry> findById(Long id) {
        String sql = "SELECT * FROM time_entries WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTimeEntry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding time entry by id", e);
        }
        return Optional.empty();
    }

    @Override
    public TimeEntry save(TimeEntry timeEntry) {
        String sql = "INSERT INTO time_entries (project_id, start_time, end_time, hours, description) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, timeEntry.getProjectId());
            pstmt.setString(2, timeEntry.getStartTime() != null ? timeEntry.getStartTime().toString() : null);
            pstmt.setString(3, timeEntry.getEndTime() != null ? timeEntry.getEndTime().toString() : null);
            pstmt.setDouble(4, timeEntry.getHours());
            pstmt.setString(5, timeEntry.getDescription());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                timeEntry.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving time entry", e);
        }
        return timeEntry;
    }

    @Override
    public void update(TimeEntry timeEntry) {
        String sql = "UPDATE time_entries SET project_id = ?, start_time = ?, end_time = ?, " +
                    "hours = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, timeEntry.getProjectId());
            pstmt.setString(2, timeEntry.getStartTime() != null ? timeEntry.getStartTime().toString() : null);
            pstmt.setString(3, timeEntry.getEndTime() != null ? timeEntry.getEndTime().toString() : null);
            pstmt.setDouble(4, timeEntry.getHours());
            pstmt.setString(5, timeEntry.getDescription());
            pstmt.setLong(6, timeEntry.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating time entry", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM time_entries WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting time entry", e);
        }
    }

    @Override
    public List<TimeEntry> findByProjectId(Long projectId) {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE project_id = ? ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                entries.add(mapResultSetToTimeEntry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding time entries by project id", e);
        }
        return entries;
    }

    private TimeEntry mapResultSetToTimeEntry(ResultSet rs) throws SQLException {
        TimeEntry entry = new TimeEntry();
        entry.setId(rs.getLong("id"));
        entry.setProjectId(rs.getLong("project_id"));
        
        String startTime = rs.getString("start_time");
        if (startTime != null) {
            entry.setStartTime(LocalDateTime.parse(startTime));
        }
        
        String endTime = rs.getString("end_time");
        if (endTime != null) {
            entry.setEndTime(LocalDateTime.parse(endTime));
        }
        
        entry.setHours(rs.getDouble("hours"));
        entry.setDescription(rs.getString("description"));
        return entry;
    }
}
