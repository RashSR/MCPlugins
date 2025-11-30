package sessionspy;
import net.canarymod.Canary;
import java.time.Instant;
import java.util.List;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.hook.system.LoadWorldHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.DisconnectionHook;

public class SessionSpy extends EZPlugin implements PluginListener{

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler
 	public void LoadWorldHookEvent(LoadWorldHook event){
    Instant currentTime = Instant.now();
    logger.info("ServerStart at: " + currentTime);
 	}

  @HookHandler
  public void ServerShutdownHook(ServerShutdownHook event){
    Instant currentTime = Instant.now();
    logger.info("ServerShutdown at: " + currentTime);
    
    //All player need to be logged out after a server shutdown
    List<Player> playerList = Canary.instance().getServer().getPlayerList();
    for(Player player : playerList)
      logoutPlayerEvent(player, currentTime);
  }

  @HookHandler
  public void ConnectionHookEvent(ConnectionHook event){
    Instant currentTime = Instant.now();
    Player player = event.getPlayer();
    logger.info(player.getDisplayName() + " logged in at: " + currentTime);
  }

  @HookHandler
  public void DisconnectionHookHookEvent(DisconnectionHook event){
    Instant currentTime = Instant.now();
    logoutPlayerEvent(event.getPlayer(), currentTime);
  }

  private void logoutPlayerEvent(Player player, Instant time){
    logger.info(player.getDisplayName() + " logged out at: " + time);
  }
  
}
