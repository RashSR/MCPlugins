package alwayssun;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.world.WeatherChangeHook;
import net.canarymod.hook.world.TimeChangeHook;
import net.canarymod.api.world.World;

public class AlwaysSun extends EZPlugin implements PluginListener{
  
  @Override 
   public boolean enable() {

   Canary.hooks().registerListener(this, this);
   return super.enable();
 }

 @HookHandler
 public void dasbleibthaengen(WeatherChangeHook event){
  	event.setCanceled();
 }

 @HookHandler 
 public void noTimeChange(TimeChangeHook event){
 	World world = event.getWorld();
 	event.setCanceled();
 	/*
 	if(world.getRelativeTime()!=1000){
 		logger.info("es ist nicht so weit!");
 		world.setTime(1000);
 	}
 	*/
 }


}
