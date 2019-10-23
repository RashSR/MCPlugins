package paintingsave;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.entity.HangingEntityDestroyHook;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;

public class paintingsave extends EZPlugin implements PluginListener {

@Override 
  public boolean enable() {

  Canary.hooks().registerListener(this, this);
  return super.enable();

                          }

@HookHandler
public void dasbleibthaengen(HangingEntityDestroyHook event){

  Entity abhaengen = event.getPainting();
  event.setCanceled();

 }
}