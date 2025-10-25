package utils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

            String createGameSessionQuidditch = "CREATE TABLE IF NOT EXISTS game_sessions (" +
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
                "shortest_bow_hit INTEGER," + 
                "longest_bow_hit INTEGER DEFAULT 0," +
                "total_compass_count INTEGER DEFAULT 0," +
                "session_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";
            stmt.executeUpdate(createGameSessionQuidditch);

            String createAchievementTableQuidditch = "CREATE TABLE IF NOT EXISTS player_achievements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playername TEXT NOT NULL," +
                "achievement_name TEXT NOT NULL," + 
                "achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + 
            ");";
            stmt.executeUpdate(createAchievementTableQuidditch);

            logger.info("[DatabaseUtils] Database initialized successfully: " + dbFilePath);

        } catch (SQLException e) {
            logger.info("[DatabaseUtils] Failed to initialize database: " + e.getMessage());
        }
    }

    public void insertGameSession(String playerName, int score, int handCatches, int bowHits,
                              int fastestCatch, int slowestCatch, int fastCatches, int fastCatchStreaks, int missedArrows,
                              String mapPlayed, int gameDuration, int shortestBowHit, int longestBowHit, int totalCompassCount) {
        String sqlCommand = "INSERT INTO game_sessions (" +
                "player_name, score, hand_catches, bow_hits, fastest_catch, slowest_catch, fast_catches, " +
                "fast_catch_streaks, missed_arrows, map_played, game_duration, shortest_bow_hit, longest_bow_hit, total_compass_count, session_timestamp" + 
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
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
            pstmt.setInt(12, shortestBowHit);
            pstmt.setInt(13, longestBowHit);
            pstmt.setInt(14, totalCompassCount);

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
                topValues.add(Utils.ShortenMapName(mapName, 10) + ":" + count);
            }
        }
        catch(SQLException e){
            return null;
        }

        return topValues;
    }

    public List<String> GetShortestAndLongestBowHit(){
        List<String> topValues = new ArrayList<>();
        String maxCommand = "SELECT MAX(longest_bow_hit) FROM game_sessions;";
        String minCommand = "SELECT MIN(shortest_bow_hit) FROM game_sessions;";
        
        try{
            PreparedStatement maxStatment = connection.prepareStatement(maxCommand);
            PreparedStatement minStatment = connection.prepareStatement(minCommand);
            
            ResultSet rs = minStatment.executeQuery();
            while(rs.next()){
                String minValue = rs.getString(1);
                topValues.add(minValue + " Blocks");
            }

            rs = maxStatment.executeQuery();
            while(rs.next()){
                String maxValue = rs.getString(1);
                topValues.add(maxValue + " Blocks");
            }
        }
        catch(SQLException e){
            return null;
        }

        return topValues;
    }
    
    public HashMap<String, String> GetPlayerStatsForQuidditch(String playerName){
        String sqlCommand = "SELECT player_name, COUNT(*) AS played_matches, AVG(score), AVG(hand_catches), AVG(bow_hits),"
            + " MIN(fastest_catch), MAX(slowest_catch), AVG(fast_catch_streaks), AVG(missed_arrows), AVG(game_duration),"
            + " MIN(shortest_bow_hit), MAX(longest_bow_hit), AVG(total_compass_count) FROM game_sessions WHERE player_name = ? GROUP BY player_name;";
        HashMap<String, String> stats = new LinkedHashMap<>();

        try{
            PreparedStatement statsStatement = connection.prepareStatement(sqlCommand);
            statsStatement.setString(1, playerName);
            ResultSet results = statsStatement.executeQuery();

            while (results.next()) {
                stats.put("Gespielte Spiele", results.getString(2));
                stats.put("⌀ Score", formatDoubleValue(results.getString(3)));
                stats.put("⌀ Spieldauer", Utils.FormatSecondsPassedIntoString(results.getInt(10)));
                stats.put("⌀ Handfänge", formatDoubleValue(results.getString(4)));
                stats.put("⌀ Bogentreffer", formatDoubleValue(results.getString(5)));
                stats.put("⌀ Fast Catch Streaks", formatDoubleValue(results.getString(8)));
                stats.put("⌀ Verschossene Pfeile", formatDoubleValue(results.getString(9)));
                stats.put("⌀ Kompass verwendet", formatDoubleValue(results.getString(13)));
                stats.put("Kürzester Bogentreffer", results.getString(11));
                stats.put("Weitester Bogentreffer", results.getString(12));
                stats.put("Schnellster Fang", Utils.FormatSecondsPassedIntoString(results.getInt(6))); 
                stats.put("Langsamster Fang", Utils.FormatSecondsPassedIntoString(results.getInt(7)));
            }
        }
        catch(SQLException e){
            stats.put("ERROR", "Could not load stats");
        }

        return stats;
    }

    public List<HashMap<String, String>> GetPlayerStatsForQuidditchEachMap(String playerName){
        String sqlCommand = "SELECT player_name, map_played, COUNT(*), AVG(score), AVG(hand_catches), " + 
            "AVG(bow_hits), MIN(fastest_catch), MAX(slowest_catch), AVG(fast_catch_streaks), AVG(missed_arrows), " + 
            "AVG(game_duration), MIN(shortest_bow_hit), MAX(longest_bow_hit), AVG(total_compass_count) " + 
            "FROM game_sessions WHERE player_name = ? GROUP BY player_name, map_played;";
        List<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();

        try{
            PreparedStatement statsStatement = connection.prepareStatement(sqlCommand);
            statsStatement.setString(1, playerName);
            ResultSet results = statsStatement.executeQuery();

            while (results.next()) {
                HashMap<String, String> stats = new LinkedHashMap<>();
                stats.put("Map", results.getString(2));
                stats.put("Gespielte Spiele", results.getString(3));
                stats.put("⌀ Score", formatDoubleValue(results.getString(4)));
                stats.put("⌀ Spieldauer", Utils.FormatSecondsPassedIntoString(results.getInt(11)));
                stats.put("⌀ Handfänge", formatDoubleValue(results.getString(5)));
                stats.put("⌀ Bogentreffer", formatDoubleValue(results.getString(6)));
                stats.put("⌀ Fast Catch Streaks", formatDoubleValue(results.getString(9)));
                stats.put("⌀ Verschossene Pfeile", formatDoubleValue(results.getString(10)));
                stats.put("⌀ Kompass verwendet", formatDoubleValue(results.getString(14)));
                stats.put("Kürzester Bogentreffer", results.getString(12));
                stats.put("Weitester Bogentreffer", results.getString(13));
                stats.put("Schnellster Fang", Utils.FormatSecondsPassedIntoString(results.getInt(7))); 
                stats.put("Langsamster Fang", Utils.FormatSecondsPassedIntoString(results.getInt(8)));
                lists.add(stats);
            }
        }
        catch(SQLException e){
            return null;
        }

        return lists;
    }

    private String formatDoubleValue(String value){
        double number = Double.parseDouble(value);
        String formatted = String.format(Locale.US, "%.2f", number);
        return formatted;
    }

    public boolean hasPlayerAchievement(String playerName, String achievementName){
        String sqlCommand = "SELECT COUNT(*) FROM player_achievements WHERE playername = ? AND achievement_name = ?;";
        

        try{
            PreparedStatement achievementStatement = connection.prepareStatement(sqlCommand);
            achievementStatement.setString(1, playerName);
            achievementStatement.setString(2, achievementName);
            ResultSet result = achievementStatement.executeQuery();

            while(result.next()) {
                int count = result.getInt("COUNT(*)");
                if(count == 0)
                    return false;
                
                return true;
            }
        }
        catch(SQLException e){
            logger.info("ERROR during achievement loading: " + e.getMessage());
            return false;
        }

        return false;
    }

}
