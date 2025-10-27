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
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import utils.Utils;
import events.christmas.ChristmasEvent;
import net.canarymod.hook.system.LoadWorldHook;

public class events extends EZPlugin implements PluginListener {
	protected static final String pluginName = "[Events]";
	private IEvent currentEvent;
	
	@Override
	public boolean enable() { 
  		Canary.hooks().registerListener(this, this);
  		return super.enable(); 
	}

 	@HookHandler
 	public void ServerShutdownHookEvent(ServerShutdownHook event){
 		endEvent();
 	}

	@HookHandler
 	public void LoadWorldHookEvent(LoadWorldHook event){
 		logger.info("Wir haben den " + Utils.getDay() + "." + Utils.getMonth() + "." + Utils.GetYear() + "!");
		currentEvent = getCurrentEventFromDate(event.getWorld());
		if(currentEvent != null)
			currentEvent.startEvent();
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
					endEvent();
					currentEvent = new ChristmasEvent(player.getWorld());
					currentEvent.startEvent();
					break;
				case "keins":
					endEvent();
					break;
				case "halloween":
					endEvent();
					currentEvent = new HalloweenEvent(player.getWorld());
					currentEvent.startEvent();
					break;
				case "neujahr":
					endEvent();
					currentEvent = new NewYearEvent(player.getWorld());
					currentEvent.startEvent();
					break;
				case "pride":
					endEvent();
					currentEvent = new PrideEvent(player.getWorld());
					currentEvent.startEvent();
					break;
				default:
					return;
			}
			//TODO: is displayed each time -> only after successful change 
			Utils.BroadcastServerMessage(pluginName, "Setze event auf: " + ChatFormat.GOLD + Utils.GetCurrentEvent().toString());
		}
  	}

  	private void endEvent(){
		if(currentEvent != null)
			currentEvent.endEvent();
  	}

  	private IEvent getCurrentEventFromDate(World world){
		int day = Utils.getDay();
		int month = Utils.getMonth();

  		if(month == 12)
    		return new ChristmasEvent(world);
    	else if(month == 1 && day < 10)
    		return new NewYearEvent(world);
    	else if(month == 10 && day > 20)
    		return new HalloweenEvent(world);
		else if(month == 6)
			return new PrideEvent(world);

    	return null;
  	}
}