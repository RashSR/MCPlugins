package utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.pragprog.ahmine.ez.EZPlugin;

public class DatabaseUtils extends EZPlugin{

    private static final String DB_FOLDER = "plugins/Quidditch";
    private static final String DB_FILE = "quidditch.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FOLDER + "/" + DB_FILE;

    public static void InitDatabase(){
        try {
            // Ensure the folder exists
            File folder = new File(DB_FOLDER);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    logger.info("[Quidditch] Created plugin data folder: " + folder.getPath());
                } else {
                    System.err.println("[Quidditch] Failed to create plugin folder: " + folder.getPath());
                }
            }

            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            logger.info("[Quidditch] SQLite JDBC driver loaded successfully.");

            // Initialize the database structure
            initDatabase();

        } catch (Exception e) {
            logger.info("[Quidditch] Database initialization failed:");
            e.printStackTrace();
        }
    }

    /**
     * Opens a new connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Closes a connection safely.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[Quidditch] Failed to close DB connection: " + e.getMessage());
            }
        }
    }

    /**
     * Creates the database tables if they don't exist yet.
     */
    private static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS player_scores (" +
                    "player TEXT PRIMARY KEY," +
                    "points INTEGER DEFAULT 0," +
                    "hand_catches INTEGER DEFAULT 0," +
                    "bow_hits INTEGER DEFAULT 0," +
                    "fast_catches INTEGER DEFAULT 0," +
                    "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

            stmt.executeUpdate(sql);
            System.out.println("[Quidditch] Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[Quidditch] Failed to initialize database: " + e.getMessage());
        }
    }
}
