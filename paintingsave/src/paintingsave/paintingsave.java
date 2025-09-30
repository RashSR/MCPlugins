package paintingsave;
import net.canarymod.Canary;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.entity.HangingEntityDestroyHook;

public class paintingsave extends EZPlugin implements PluginListener {

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler
  public void HangingEntityDestroyHookEvent(HangingEntityDestroyHook event){
    event.setCanceled();
  }
}