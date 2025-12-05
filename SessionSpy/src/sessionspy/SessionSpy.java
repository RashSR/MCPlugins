package sessionspy;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import utils.DatabaseType;
import utils.DatabaseUtils;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.hook.system.LoadWorldHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.chat.ChatFormat;
import utils.DatabaseType;
import utils.Utils;

public class SessionSpy extends EZPlugin implements PluginListener{
  private final String DB_FOLDER = "plugins/SessionSpy";
  private final String DB_FILE = "sessionspy.db";
  private final String pluginName = "[SessionSpy]";

  private String insertedServerName;
  private Instant insertedServerStartTime;
  private DatabaseUtils database;

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @Command(aliases = { "age" },
            description = "Shows player how long he has played on the server",
            permissions = { "*" },
            toolTip = "/age")
  public void AgeCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player){
      int totalTimeInSeconds = database.LoadTotalPlayerTimeInSeconds(player.getDisplayName());
      String formatedTotalTime = ChatFormat.GOLD + Utils.FormatSecondsPassedIntoString(totalTimeInSeconds);
      int activeTimeInSeconds = database.LoadActivePlayerSessionInSeconds(player.getDisplayName());
      String formatedActiveTime = ChatFormat.GOLD + Utils.FormatSecondsPassedIntoString(activeTimeInSeconds);

      String serverMessage = "Deine gesamte Spielzeit beträgt " + formatedTotalTime + ChatFormat.DARK_GREEN + " auf diesem Server. Davon " 
        + formatedActiveTime + ChatFormat.DARK_GREEN + " in deiner aktuellen Session. Dein erster (registrierter) Login war am " 
        + ChatFormat.GOLD + database.GetEarliestLoginFromPlayer(player.getDisplayName()) + ChatFormat.DARK_GREEN + ".";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
    }
  }

  @Command(aliases = { "serverage" },
            description = "Shows player how long the server was online",
            permissions = { "*" },
            toolTip = "/serverage")
  public void ServerAgeCommand(MessageReceiver caller, String[] args) {
    String serverName = Canary.instance().getServer().getHostname();
    int timeInSeconds = database.LoadTotalServerTimeInSeconds(serverName);
    String formatedTime = ChatFormat.GOLD + Utils.FormatSecondsPassedIntoString(timeInSeconds);
    String serverMessage = "Der Server hat eine Betriebszeit von  " + formatedTime + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  @HookHandler
 	public void LoadWorldHookEvent(LoadWorldHook event){
    Instant currentTime = Instant.now();
    database = setUpDatabase();
    String serverName = Canary.instance().getServer().getHostname();
    if(database.InsertServerStartSession(serverName, currentTime)){
      insertedServerName = serverName;
      insertedServerStartTime = currentTime;
    }
 	}

  @HookHandler
  public void ServerShutdownHook(ServerShutdownHook event){
    Instant currentTime = Instant.now();
    if(database != null)
      database.UpdateServerShutdownSession(insertedServerName, insertedServerStartTime, currentTime);

    //All player need to be logged out after a server shutdown
    List<Player> playerList = Canary.instance().getServer().getPlayerList();
    for(Player player : playerList)
      logoutPlayerEvent(player, currentTime);

    database.CloseConnection();
  }

  private HashMap<Player, Instant> loggedInPlayers;

  @HookHandler
  public void ConnectionHookEvent(ConnectionHook event){
    Instant currentTime = Instant.now();
    Player player = event.getPlayer();

    if(database != null && database.InsertPlayerLoginSession(player.getDisplayName(), currentTime)){
      if(loggedInPlayers == null)
        loggedInPlayers = new HashMap<>();

      loggedInPlayers.put(player, currentTime);
    }
  }

  @HookHandler
  public void DisconnectionHookHookEvent(DisconnectionHook event){
    Instant currentTime = Instant.now();
    logoutPlayerEvent(event.getPlayer(), currentTime);
  }

  private void logoutPlayerEvent(Player player, Instant logOutTime){
    if(database != null)
      database.UpdatePlayerLogoutSession(player.getDisplayName(), loggedInPlayers.get(player), logOutTime);

    loggedInPlayers.remove(player);
  }

  private DatabaseUtils setUpDatabase(){
    DatabaseUtils newDb = new DatabaseUtils(DB_FOLDER, DB_FILE);
    newDb.InitDatabase(DatabaseType.SESSION_SPY);
    return newDb;
  }
}