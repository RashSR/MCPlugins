package utils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.pragprog.ahmine.ez.EZPlugin;

public class DatabaseUtils extends EZPlugin{
    private String dbFolderPath;
    private String dbFilePath;
    private String dbUrl;
    private Connection connection;

    public DatabaseUtils(String dbFolderPath, String dbFilePath){
        this.dbFolderPath = dbFolderPath;
        this.dbFilePath = dbFilePath;
        this.dbUrl = "jdbc:sqlite:" + dbFolderPath + "/" + dbFilePath;
    }

    public void InitDatabase(){
        try {
            File folder = new File(dbFolderPath);
            if(!folder.exists()){
                if(folder.mkdirs()){
                    logger.info("[DatabaseUtils] Created plugin data folder: " + folder.getPath());
                } else {
                    logger.info("[DatabaseUtils] Failed to create plugin folder: " + folder.getPath());
                }
            }

            Class.forName("org.sqlite.JDBC");
            logger.info("[DatabaseUtils] SQLite JDBC driver loaded successfully.");
            initDatabase();
        } catch (Exception e) {
            logger.info("[DatabaseUtils] Database initialization failed:");
            e.printStackTrace();
        }
    }

    public void CloseConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("[DatabaseUtils] Closed DB connection: " + dbFilePath);
            } catch (SQLException e) {
                logger.info("[DatabaseUtils] Failed to close DB connection: " + e.getMessage());
            }
        }
    }

    private void initDatabase() {
        try {
            connection = DriverManager.getConnection(dbUrl);
            Statement stmt = connection.createStatement();

        String sql = "CREATE TABLE IF NOT EXISTS game_sessions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "player_name TEXT NOT NULL," +
            "score INTEGER DEFAULT 0," +
            "hand_catches INTEGER DEFAULT 0," +
            "bow_hits INTEGER DEFAULT 0," +
            "fastest_catch INTEGER DEFAULT 0," +
            "slowest_catch INTEGER," +
            "fast_catches INTEGER DEFAULT 0," +
            "fast_catch_streaks INTEGER DEFAULT 0," +
            "missed_arrows INTEGER DEFAULT 0," +
            "map_played TEXT," +
            "game_duration INTEGER DEFAULT 0," +
            "session_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";

            stmt.executeUpdate(sql);
            logger.info("[DatabaseUtils] Database initialized successfully: " + dbFilePath);

        } catch (SQLException e) {
            logger.info("[DatabaseUtils] Failed to initialize database: " + e.getMessage());
        }
    }

    public void insertGameSession(String playerName, int score, int handCatches, int bowHits,
                              int fastestCatch, int slowestCatch, int fastCatches, int fastCatchStreaks, int missedArrows,
                              String mapPlayed, int gameDuration) {
        String sqlCommand = "INSERT INTO game_sessions (" +
                "player_name, score, hand_catches, bow_hits, fastest_catch, slowest_catch, fast_catches, " +
                "fast_catch_streaks, missed_arrows, map_played, game_duration, session_timestamp" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.setInt(3, handCatches);
            pstmt.setInt(4, bowHits);
            pstmt.setInt(5, fastestCatch);
            pstmt.setInt(6, slowestCatch);
            pstmt.setInt(7, fastCatches);
            pstmt.setInt(8, fastCatchStreaks);
            pstmt.setInt(9, missedArrows);
            pstmt.setString(10, mapPlayed);
            pstmt.setInt(11, gameDuration);

            pstmt.executeUpdate();
            logger.info("[DatabaseUtils] Successfully inserted game session for player: " + playerName);

        } catch (SQLException e) {
            logger.info("[DatabaseUtils] Error inserting game session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> GetTop3ScoresFromMap(Map map){
        String sqlCommand = "SELECT player_name, score FROM game_sessions";
        String scoreColumn = "score";
        List<String> topScores = getTopScores(sqlCommand, scoreColumn, false, 3, false, map);
        return topScores;
    }

    public List<String> GetTop3TimesFromMap(Map map){
        String sqlCommand = "SELECT player_name, game_duration FROM game_sessions";
        String scoreColumn = "game_duration";
        List<String> topTimes = getTopScores(sqlCommand, scoreColumn, true, 3, true, map);
        return topTimes;
    }

    public List<String> GetTop3FastestCatchTimes(){
        String sqlCommand = "SELECT player_name, fastest_catch FROM game_sessions";
        String scoreColumn = "fastest_catch";
        List<String> topFastCatches = getTopScores(sqlCommand, scoreColumn, true, 3, true, null);
        return topFastCatches;
    }

    public List<String> GetTop3SlowestCatchTimes(){
        String sqlCommand = "SELECT player_name, slowest_catch FROM game_sessions";
        String scoreColumn = "slowest_catch";
        List<String> topSlowCatches = getTopScores(sqlCommand, scoreColumn, false, 3, true, null);
        return topSlowCatches;
    }

    private List<String> getTopScores(String sqlCommand, String scoreColumn, boolean hasAscendingSorting, int limit, boolean isDuration, Map map){
        List<String> topValues = new ArrayList<>();

        if(map != null)
            sqlCommand += " WHERE map_played = ?";
        
        sqlCommand += " ORDER BY " + scoreColumn;
        
        if(hasAscendingSorting)
            sqlCommand += " ASC";
        else
            sqlCommand += " DESC";
        
        sqlCommand += " LIMIT " + limit + ";";
        
        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);

            if(map != null)
                pstmt.setString(1, map.toString());

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String playerName = rs.getString("player_name");
                int score = rs.getInt(scoreColumn);
                String scoreString = String.valueOf(score);

                if(isDuration)
                    scoreString = Utils.FormatSecondsPassedIntoString(score);
                    
                topValues.add(playerName + " " + scoreString);
            }
        }
        catch(SQLException e){
            return null;
        }

        return topValues;
    }

    public List<String> GetTop3MapsPlayed(){
        String sqlCommand = "SELECT map_played, COUNT(*) FROM game_sessions GROUP BY map_played ORDER BY COUNT(*) DESC LIMIT 3;";
        List<String> topValues = new ArrayList<>();

        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String mapName = rs.getString("map_played");
                int count = rs.getInt("COUNT(*)");
                topValues.add(mapName + ":" + count);
            }
        }
        catch(SQLException e){
            return null;
        }

        return topValues;
    }

    public List<String> GetShortestAndLongestBowHit(){
        String sqlCommand = "";
        List<String> topValues = new ArrayList<>();
        topValues.add("Test1");
        topValues.add("Test2");
        return topValues;
    }

}
