package join;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.*;
import net.canarymod.api.factory.ItemFactory;

public class join extends EZPlugin implements PluginListener{

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

   @HookHandler
  public void spieleron(ConnectionHook event) {
    
    Player player = event.getPlayer(); 
    Location where = player.getLocation();
    Location whereNow = new Location(251, 71, 262);
    double x = where.getX();
    double z = where.getZ();

    if(x > 80 && x < 300 && z > 200 && z < 400){

    player.setModeId(2);  
   
    spielerleer(player);

    player.teleportTo(whereNow);  

   } 
   
   else {

    player.setModeId(0);

        }  

  }

  public void spielerleer(Player player){

    player.getInventory().clearInventory();
    Item schuhe = player.getInventory().getBootsSlot();
    player.getInventory().removeItem(schuhe);
    Item hose = player.getInventory().getLeggingsSlot();
    player.getInventory().removeItem(hose);
    Item brustplatte = player.getInventory().getChestplateSlot();
    player.getInventory().removeItem(brustplatte);
    Item helm = player.getInventory().getHelmetSlot();
    player.getInventory().removeItem(helm);

  }

}
