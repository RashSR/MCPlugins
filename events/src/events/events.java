package events;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import utils.Utils;

public class events extends EZPlugin implements PluginListener {
	protected static final String pluginName = "[Events]";
	private IEvent currentEvent;
	
	@Override
	public boolean enable() { 
  		Canary.hooks().registerListener(this, this);
  		return super.enable(); 
	}

	@HookHandler 
 	public void ConnectionHookEvent(ConnectionHook event){
    	logger.info("Wir haben den " + Utils.getDay() + "." + Utils.getMonth() + "." + Utils.GetYear() + "!");
		currentEvent = getCurrentEvent(event.getPlayer().getWorld());
		if(currentEvent != null)
			currentEvent.startEvent();
 	}

 	@HookHandler
 	public void serverShutdown(ServerShutdownHook event){
 		endEvent();
 	}

	@Command(aliases = { "event" },
            description = "event plugin",
            permissions = { "*" },
            toolTip = "/event weihnachten, /event keins")
  	public void eventsCommand(MessageReceiver caller, String[] args) {
		if(args.length==2 && caller instanceof Player player){
			String eventParam = args[1];

			switch (eventParam) {
				case "weihnachten":
					if(currentEvent == null || currentEvent.getEventType() != EventType.CHRISTMAS){
						endEvent();
						currentEvent = new ChristmasEvent(player.getWorld());
						currentEvent.startEvent();
					}
					break;
				case "keins":
					endEvent();
					break;
				case "halloween":
					if(currentEvent == null || currentEvent.getEventType() != EventType.HALLOWEEN){
						endEvent();
						currentEvent = new HalloweenEvent(player.getWorld());
						currentEvent.startEvent();
					}
					break;
				case "neujahr":
					if(currentEvent == null || currentEvent.getEventType() != EventType.NEWYEAR){
						endEvent();
						currentEvent = new NewYearEvent(player.getWorld());
						currentEvent.startEvent();
					}
				default:
					break;
			}
		}
  	}

  	private void endEvent(){
		if(currentEvent != null)
			currentEvent.endEvent();
  	}

  	private IEvent getCurrentEvent(World world){
		int day = Utils.getDay();
		int month = Utils.getMonth();

  		if(month == 12)
    		return new ChristmasEvent(world);
    	else if(month==1 && day<10)
    		return new NewYearEvent(world);
    	else if(month==10&&day>20)
    		return new HalloweenEvent(world);

    	return null;
  	}
}