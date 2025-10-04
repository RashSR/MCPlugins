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
import utils.Utils;

public class events extends EZPlugin implements PluginListener {
	public static EventEnum CurrentEvent;
	private static final String pluginName = "[Events]";
    public static String fileName="C:/Users/R/Desktop/server/config/events.txt";
	
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
    	CurrentEvent = getCurrentEvent();
    	logger.info("Wir haben den "+day+"."+month+"."+year+"!");

		switch (CurrentEvent) {
			case CHRISTMAS:
					Christmas.startChristmas();
				break;
			case NEWYEAR:
				String serverMessage = ChatFormat.DARK_GREEN + "Wir wuenschen euch ein" + ChatFormat.GOLD + 
					"frohes " + ChatFormat.DARK_GREEN + "neues Jahr " + ChatFormat.GOLD + getYear() + ChatFormat.DARK_GREEN + ".";
				Utils.BroadcastServerMessage(pluginName, serverMessage);
				break;
			case HALLOWEEN:
				Halloween.startHalloween();
				break;
			default:
				break;
		}
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
		if(args.length==2){
			if(args[1].equalsIgnoreCase("weihnachten") && CurrentEvent!=EventEnum.CHRISTMAS){
				endEvent();
				Christmas.startChristmas();
			}else if(args[1].equalsIgnoreCase("keins") && CurrentEvent!=null){
				endEvent();
			}else if(args[1].equalsIgnoreCase("halloween") && CurrentEvent!=EventEnum.HALLOWEEN){
				endEvent();
				Halloween.startHalloween();
			}
		}
  	}

  	private void endEvent(){
		switch (CurrentEvent) {
			case CHRISTMAS:
					Christmas.endChristmas();
				break;
			case HALLOWEEN:
				Halloween.endHalloween();
				break;
			default:
				break;
		}
  	}

  	private EventEnum getCurrentEvent(){
  		if(month == 12){
    		return EventEnum.CHRISTMAS;
    	}else if(month==1 && day<10){
    		return EventEnum.NEWYEAR;
    	}else if(month==10&&day>20){
    		return EventEnum.HALLOWEEN;
    	}else{
    		return null;
    	}
  	}
}