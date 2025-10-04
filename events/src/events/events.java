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
	private static final String pluginName = "[Events]";
    public static String fileName="C:/Users/R/Desktop/server/config/events.txt";
	private IEvent currentEvent;
	
	private int month = Integer.parseInt(getMonth());
    private int year = Integer.parseInt(getYear());
    private int day = Integer.parseInt(getDay());
	
	@Override
	public boolean enable() { 
  		Canary.hooks().registerListener(this, this);
  		return super.enable(); 
	}

	private String getYear() {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy");
    	Date date = new Date();
    	return dateFormat.format(date); 
    }

	private String getMonth(){
   		DateFormat dateFormat = new SimpleDateFormat("MM");
   		Date date = new Date();
		return dateFormat.format(date);
	}

	private String getDay(){
   		DateFormat dateFormat = new SimpleDateFormat("dd");
   		Date date = new Date();
   		return dateFormat.format(date);
	}

	@HookHandler 
 	public void ConnectionHookEvent(ConnectionHook event){
    	logger.info("Wir haben den "+day+"."+month+"."+year+"!");
		currentEvent = getCurrentEvent(event.getPlayer().getWorld());
		if(currentEvent != null)
			currentEvent.startEvent();
			/* Maybe into Constructor to NewYear Event
			String serverMessage = ChatFormat.DARK_GREEN + "Wir wuenschen euch ein" + ChatFormat.GOLD + 
				"frohes " + ChatFormat.DARK_GREEN + "neues Jahr " + ChatFormat.GOLD + getYear() + ChatFormat.DARK_GREEN + ".";
			Utils.BroadcastServerMessage(pluginName, serverMessage); 
			*/
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
				default:
					break;
			}
		}
  	}

  	private void endEvent(){
		if(currentEvent != null){
			currentEvent.endEvent();
			currentEvent = null;
		}
  	}

  	private IEvent getCurrentEvent(World world){
  		if(month == 12){
    		return new ChristmasEvent(world);
    	}else if(month==1 && day<10){
    		return null;//EventEnum.NEWYEAR;
    	}else if(month==10&&day>20){
    		return new HalloweenEvent(world);
    	}else{
    		return null;
    	}
  	}
}