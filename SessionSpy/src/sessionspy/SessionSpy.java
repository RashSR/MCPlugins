package sessionspy;
import net.canarymod.Canary;
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
import utils.DatabaseType;

public class SessionSpy extends EZPlugin implements PluginListener{
  private final String DB_FOLDER = "plugins/SessionSpy";
  private final String DB_FILE = "sessionspy.db";

  private String insertedServerName;
  private Instant insertedServerStartTime;
  private DatabaseUtils database;

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
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