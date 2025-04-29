package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHandler {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static final String DB_URL = "jdbc:sqlite:weather.db";
    private static final int MAX_SAVED_CITIES = 10;

    private static DatabaseHandler instance;

    private DatabaseHandler() {
        initializeDatabase();
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS cities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "country TEXT NOT NULL," +
                "state TEXT," +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    public boolean addCity(City city) {
        if (city == null) return false;

        if (getSavedCitiesCount() >= MAX_SAVED_CITIES) {
            removeOldestCity();
        }

        String sql = "INSERT INTO cities(name, country, state, lat, lon) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city.getName());
            pstmt.setString(2, city.getCountry());
            pstmt.setString(3, city.getState());
            pstmt.setDouble(4, city.getLatitude());
            pstmt.setDouble(5, city.getLongitude());

            int affectedRows = pstmt.executeUpdate();
            logger.info("Added city: {}", city.getName());
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error adding city to database", e);
            return false;
        }
    }

    public List<City> getSavedCities() {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT name, country, state, lat, lon FROM cities ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                City city = new City(
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon")
                );
                city.setState(rs.getString("state"));
                cities.add(city);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving saved cities", e);
        }

        return cities;
    }

    public boolean removeCity(City city) {
        if (city == null) return false;

        String sql = "DELETE FROM cities WHERE name = ? AND country = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city.getName());
            pstmt.setString(2, city.getCountry());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Removed city: {}", city.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error removing city from database", e);
        }

        return false;
    }

    private int getSavedCitiesCount() {
        String sql = "SELECT COUNT(*) FROM cities";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting saved cities", e);
            return 0;
        }
    }

    private void removeOldestCity() {
        String sql = "DELETE FROM cities WHERE id = (SELECT id FROM cities ORDER BY timestamp ASC LIMIT 1)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            logger.info("Removed oldest city to maintain limit");
        } catch (SQLException e) {
            logger.error("Error removing oldest city", e);
        }
    }

    public void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM cities");
            logger.info("Cleared all saved cities");
        } catch (SQLException e) {
            logger.error("Error clearing database", e);
        }
    }
}