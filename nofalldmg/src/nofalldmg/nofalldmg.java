package nofalldmg;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.entity.DamageHook;
import net.canarymod.api.DamageType;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.world.position.Location;


public class nofalldmg extends EZPlugin implements PluginListener{

  @Override 
  public boolean enable() {

    Canary.hooks().registerListener(this, this);
    return super.enable();

                          }
  
  @HookHandler
  public void onEntityDamage(DamageHook event) {

    Entity ent = event.getDefender();

    if (ent instanceof Player) {

      Player player = (Player) ent;


      if (event.getDamageSource().getDamagetype() == DamageType.FALL) {

        double xp = player.getX();
        double yp = player.getY();
        double zp = player.getZ();

        int x = (int) xp;
        int y = (int) yp;
        int z = (int) zp;

        if(x == 251 && y == 71 && z == 262 || x == 256 && y == 71 && z == 546 || x == 107 && y == 151 && z == 309 || x ==281 && y == 18 && z == 235 || x == 93 && y == 79 && z == 327 || x == -350 && y == 64 && z == 264){

          event.setCanceled();

            }
            
        if(x >= 267 && x <= 295 && y >= 18 && y <= 53 && z >= 199 && z <= 236){

          event.setCanceled();

        }

                                                                      }
                                }
                                                }
}

