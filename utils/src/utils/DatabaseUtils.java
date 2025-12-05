package utils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
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

    public void InitDatabase(DatabaseType databaseType){
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
            switch(databaseType) {
                case QUIDDITCH:
                    initQuidditchDatabase();
                    break;
                case SESSION_SPY:
                    initSessionSpyDatabase();
                    break;
                default:
                    break;
            }
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

    private void initSessionSpyDatabase(){
        try {
            connection = DriverManager.getConnection(dbUrl);
            Statement stmt = connection.createStatement();

            String createSessionSpyServer = "CREATE TABLE IF NOT EXISTS server_session (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "server_name TEXT NOT NULL, " +
                "started_at TIMESTAMP NOT NULL, " +
                "shutdown_at TIMESTAMP" +
            ");";
            stmt.executeUpdate(createSessionSpyServer);

            String createSessionSpyPlayer = "CREATE TABLE IF NOT EXISTS player_session (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "logged_in_at TIMESTAMP NOT NULL, " +
                "logged_out_at TIMESTAMP" +
            ");";
            stmt.executeUpdate(createSessionSpyPlayer);

            logger.info("[DatabaseUtils] Database initialized successfully: " + dbFilePath);
        }
        catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to initialize database: " + e.getMessage());
        }
    }

    public boolean InsertServerStartSession(String serverName, Instant startTime){
        String sqlCommand = "INSERT INTO server_session (server_name, started_at) VALUES (?, ?);";

        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setString(1, serverName);
            pstmt.setString(2, startTime.toString());
            int updatedRows = pstmt.executeUpdate();

            logger.info("[DatabaseUtils] Successfully inserted server start time");
            return updatedRows > 0;
            
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to insert server start time");
        }

        return false;
    }

    public void UpdateServerShutdownSession(String serverName, Instant startTime, Instant shutdownTime){
        String sqlCommand = "UPDATE server_session SET shutdown_at = ? WHERE server_name = ? AND started_at = ?;";

        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setString(1, shutdownTime.toString());
            pstmt.setString(2, serverName);
            pstmt.setString(3, startTime.toString());
            int updatedRows = pstmt.executeUpdate();

            logger.info("[DatabaseUtils] Successfully inserted server shutdown time");
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to update server shutdown time");
        }
    }

    public boolean InsertPlayerLoginSession(String playerName, Instant loginTime){
        String sqlCommand = "INSERT INTO player_session (player_name, logged_in_at) VALUES (?, ?);";

        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setString(1, playerName);
            pstmt.setString(2, loginTime.toString());
            int updatedRows = pstmt.executeUpdate();

            logger.info("[DatabaseUtils] Successfully inserted player login time from " + playerName);
            return updatedRows > 0;
            
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to insert player login time from " + playerName);
        }

        return false;
    }

    public void UpdatePlayerLogoutSession(String playerName, Instant loginTime, Instant logoutTime){
        String sqlCommand = "UPDATE player_session SET logged_out_at = ? WHERE player_name = ? AND logged_in_at = ?;";
        
        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
            pstmt.setString(1, logoutTime.toString());
            pstmt.setString(2, playerName);
            pstmt.setString(3, loginTime.toString());
            int updatedRows = pstmt.executeUpdate();

            logger.info("[DatabaseUtils] Successfully inserted player logout time from " + playerName);
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to insert player logout time from " + playerName);
        }
    }

    public int LoadTotalPlayerTimeInSeconds(String playerName){
        String sqlCommand = "SELECT logged_in_at, logged_out_at FROM player_session WHERE player_name = ? AND logged_out_at IS NOT NULL;";
        int totalSeconds = 0;

        try{
            PreparedStatement pstm = connection.prepareStatement(sqlCommand);
            pstm.setString(1, playerName);

            ResultSet rs = pstm.executeQuery();
            totalSeconds = getTotalSessionDuration(rs, "logged_in_at", "logged_out_at");
            totalSeconds += LoadActivePlayerSessionInSeconds(playerName);
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to load player time from " + playerName);
        }

        return totalSeconds;
    }

    public int LoadActivePlayerSessionInSeconds(String playerName){
        String sqlCommand = "SELECT logged_in_at FROM player_session WHERE player_name = ? AND logged_out_at IS NULL;";
        int totalSeconds = 0;

        try{
            PreparedStatement pstm = connection.prepareStatement(sqlCommand);
            pstm.setString(1, playerName);

            ResultSet rs = pstm.executeQuery();
            totalSeconds = getTotalSessionDuration(rs, "logged_in_at", null);
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to load active player time from " + playerName);
        }

        return totalSeconds;
    }

    //TODO: this only works with already logged sessions -> the current playtime is not included
    public int LoadTotalServerTimeInSeconds(String serverName){
        String sqlCommand = "SELECT started_at, shutdown_at FROM server_session WHERE server_name = ? AND shutdown_at IS NOT NULL;";
        int totalSeconds = 0;

        try{
            PreparedStatement pstm = connection.prepareStatement(sqlCommand);
            pstm.setString(1, serverName);

            ResultSet rs = pstm.executeQuery();
            totalSeconds = getTotalSessionDuration(rs, "started_at", "shutdown_at");
        }catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to load total server time.");
        }

        return totalSeconds;
    }

    private int getTotalSessionDuration(ResultSet resultSet, String startColum, String endColumn) throws SQLException{
        int totalSeconds = 0;
        
        while(resultSet.next()){
            String loginTime = resultSet.getString(startColum);
            String logoutTime;
            if(endColumn == null) //Is used to get the active session
                logoutTime = Instant.now().toString();
            else
                logoutTime = resultSet.getString(endColumn);

            Instant start = Instant.parse(loginTime);
            Instant end = Instant.parse(logoutTime);

            long sessionSeconds = Duration.between(start, end).getSeconds();
            totalSeconds += sessionSeconds;
        }

        return totalSeconds;
    }

    private void initQuidditchDatabase() {
        try {
            connection = DriverManager.getConnection(dbUrl);
            Statement stmt = connection.createStatement();

            String createGameSessionQuidditch = "CREATE TABLE IF NOT EXISTS game_session (" +
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
                "player_name TEXT NOT NULL," +
                "achievement_name TEXT NOT NULL," +
                "achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + 
                "PRIMARY KEY(player_name, achievement_name)" +
            ");";
            stmt.executeUpdate(createAchievementTableQuidditch);

            logger.info("[DatabaseUtils] Database initialized successfully: " + dbFilePath);

        } catch (SQLException e){
            logger.info("[DatabaseUtils] Failed to initialize database: " + e.getMessage());
        }
    }

    public void insertGameSession(String playerName, int score, int handCatches, int bowHits,
                              int fastestCatch, int slowestCatch, int fastCatches, int fastCatchStreaks, int missedArrows,
                              String mapPlayed, int gameDuration, int shortestBowHit, int longestBowHit, int totalCompassCount) {
        String sqlCommand = "INSERT INTO game_session (" +
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
        String sqlCommand = "SELECT player_name, score FROM game_session";
        String scoreColumn = "score";
        List<String> topScores = getTopScores(sqlCommand, scoreColumn, false, 3, false, map);
        return topScores;
    }

    public List<String> GetTop3TimesFromMap(Map map){
        String sqlCommand = "SELECT player_name, game_duration FROM game_session";
        String scoreColumn = "game_duration";
        List<String> topTimes = getTopScores(sqlCommand, scoreColumn, true, 3, true, map);
        return topTimes;
    }

    public List<String> GetTop3FastestCatchTimes(){
        String sqlCommand = "SELECT player_name, fastest_catch FROM game_session";
        String scoreColumn = "fastest_catch";
        List<String> topFastCatches = getTopScores(sqlCommand, scoreColumn, true, 3, true, null);
        return topFastCatches;
    }

    public List<String> GetTop3SlowestCatchTimes(){
        String sqlCommand = "SELECT player_name, slowest_catch FROM game_session";
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
        String sqlCommand = "SELECT map_played, COUNT(*) FROM game_session GROUP BY map_played ORDER BY COUNT(*) DESC LIMIT 3;";
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
        String maxCommand = "SELECT MAX(longest_bow_hit) FROM game_session;";
        String minCommand = "SELECT MIN(shortest_bow_hit) FROM game_session;";
        
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
            + " MIN(shortest_bow_hit), MAX(longest_bow_hit), AVG(total_compass_count) FROM game_session WHERE player_name = ? GROUP BY player_name;";
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

            statsStatement = connection.prepareStatement("SELECT COUNT(*) FROM player_achievements WHERE player_name = ?;");
            statsStatement.setString(1, playerName);
            ResultSet result = statsStatement.executeQuery();

            while(result.next()){
                stats.put("Achievements", Integer.toString(result.getInt("COUNT(*)")));
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
            "FROM game_session WHERE player_name = ? GROUP BY player_name, map_played;";
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

    public int GetQuidditchGameCountByPlayer(String playerName){
        String sql = "SELECT COUNT(*) FROM game_session WHERE player_name = ?;";

        try{
            PreparedStatement gameCountStatement = connection.prepareStatement(sql);
            gameCountStatement.setString(1, playerName);
            ResultSet result = gameCountStatement.executeQuery();

            while(result.next()){
                int count = result.getInt("COUNT(*)");
                return count;
            }
        }
        catch(SQLException e){
            logger.info("ERROR during game count loading: " + e.getMessage());
        }

        return 0;
    }

    public int GetQuidditchMapCountByPlayer(String playerName){
        String sql = "SELECT COUNT(DISTINCT map_played) FROM game_session WHERE player_name = ?;";

        try{
            PreparedStatement mapCountStatement = connection.prepareStatement(sql);
            mapCountStatement.setString(1, playerName);
            ResultSet result = mapCountStatement.executeQuery();

            while(result.next()){
                int count = result.getInt("COUNT(DISTINCT map_played)");
                return count;
            }
        }
        catch(SQLException e){
            logger.info("ERROR during game count loading: " + e.getMessage());
        }

        return 0;
    }

    public int GetTodayQuidditchMapCountByPlayer(String playerName){
        String sql = "SELECT COUNT(DISTINCT map_played) AS map_count " + 
            "FROM game_session WHERE player_name = ? " + 
            "AND DATE(session_timestamp) = DATE('now');";
        
        try{
            PreparedStatement mapTodayCountStatement = connection.prepareStatement(sql);
            mapTodayCountStatement.setString(1, playerName);
            ResultSet result = mapTodayCountStatement.executeQuery();

            while(result.next()){
                int count = result.getInt("map_count");
                return count;
            }
        }
        catch(SQLException e){
            logger.info("ERROR during today map count loading: " + e.getMessage());
        }

        return 0;
    }

    public int GetConsecutiveDayCount(String playerName) {
        String sql = "SELECT DISTINCT DATE(session_timestamp) AS play_date " +
                    "FROM game_session " +
                    "WHERE player_name = ? " +
                    "ORDER BY play_date DESC";

        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();

            int streak = 0;
            LocalDate expected = LocalDate.now();

            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("play_date"));

                if (date.equals(expected)) {
                    streak++;
                    expected = expected.minusDays(1); // check previous day next loop
                } else {
                    break; // streak broken
                }
            }

            return streak;
        } catch (SQLException e) {
            logger.info("ERROR during consecutive game count loading: " + e.getMessage());
        }

        return 0;
    }


    public boolean hasPlayerQuidditchAchievement(String playerName, String achievementName){
        String sqlCommand = "SELECT COUNT(*) FROM player_achievements WHERE player_name = ? AND achievement_name = ?;";
        

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
        }

        return false;
    }
    
    public void InsertQuidditchAchievementIntoDbForPlayer(String playerName, String achievementName){
        String sql = "INSERT OR IGNORE INTO player_achievements (player_name, achievement_name) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, achievementName);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.info("ERROR during achievement insertion: " + e.getMessage());
        }
    }

}
