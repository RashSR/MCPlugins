package join;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.api.world.position.Location;
import utils.Utils;

public class join extends EZPlugin implements PluginListener{

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler
  public void ConnectionHookEvent(ConnectionHook event) {
    Player player = event.getPlayer(); 
    Location currentPosition = player.getLocation();
    double x = currentPosition.getX();
    double z = currentPosition.getZ();

    if(x > 80 && x < 300 && z > 200 && z < 400){
      player.setModeId(Utils.ADVENTURE_MODE);  
      Utils.ClearPlayerInventory(player);
      player.teleportTo(Utils.HubLocation);  
    } 
    else 
      player.setModeId(Utils.SURVIVAL_MODE); 
  }
}
