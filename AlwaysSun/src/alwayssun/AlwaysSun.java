package alwayssun;
import net.canarymod.Canary;
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
	public void WeatherChangeHookMethod(WeatherChangeHook event){
  		event.setCanceled();
 	}

 	@HookHandler 
 	public void noTimeChange(TimeChangeHook event){
 		World world = event.getWorld();
 		event.setCanceled();
 	}
}
