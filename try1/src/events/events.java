package events;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.World;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.world.LeafDecayHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.BlockUpdateHook;

import net.canarymod.hook.player.BlockRightClickHook;

public class events extends EZPlugin implements PluginListener {
	public static EventEnum myEvent;
	private int monat = Integer.parseInt(getMonth());
    private int jahr = Integer.parseInt(getYear());
    private int tag = Integer.parseInt(getDay());

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
 	public void joinevent(ConnectionHook event){
    	Player player = event.getPlayer();
    	this.myEvent=checkEvent();
    	logger.info("Wir haben den "+tag+"."+monat+"."+jahr);
    	if(myEvent==EventEnum.CHRISTMAS){
    		Christmas.startChristmas();
   		}else if(myEvent==EventEnum.NEWYEAR){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Events] " + ChatFormat.DARK_GREEN + "Wir wuenschen euch ein" + ChatFormat.GOLD + "frohes " + ChatFormat.DARK_GREEN + "neues Jahr " + ChatFormat.GOLD + getYear() + ChatFormat.DARK_GREEN + ".");
   		}else if(myEvent==EventEnum.HALLOWEEN){
   			Halloween.startHalloween();
   		}
 	}

	@Command(aliases = { "event" },
            description = "event plugin",
            permissions = { "*" },
            toolTip = "/event weihnachten, /event keins")
  	public void eventsCommand(MessageReceiver caller, String[] args) {
    	if (caller instanceof Player) { 
      		Player player = (Player)caller;
     		if(args.length==2){
        		if(args[1].equalsIgnoreCase("weihnachten")&&myEvent!=EventEnum.CHRISTMAS){
        			endEvent();
        			Christmas.startChristmas();
        		}else if(args[1].equalsIgnoreCase("keins")&&myEvent!=null){
          			endEvent();
            	}else if(args[1].equalsIgnoreCase("test")){
        			Christmas.makeCandyStick(new Location(263, 63, 286));
        			player.getWorld().setBlockAt(new Location(263, 63, 289), BlockType.Snow);   
      			}else if(args[1].equalsIgnoreCase("halloween")&&myEvent!=EventEnum.HALLOWEEN){
      				endEvent();
      				Halloween.startHalloween();
      			}
      		}
    	}
  	}

  	private void endEvent(){
  		if(myEvent==EventEnum.CHRISTMAS){
  			Christmas.endChristmas();
  		}else if(myEvent==EventEnum.NEWYEAR){

  		}else if(myEvent==EventEnum.HALLOWEEN){
  			Halloween.endHalloween();
  		}else{

  		}
  	}

  	private EventEnum checkEvent(){
  		if(monat == 12 || monat == 1){
    		return EventEnum.CHRISTMAS;
    	}else if(monat==1 && tag<10){
    		return EventEnum.NEWYEAR;
    	}else if(monat==10&&tag>20){
    		return EventEnum.HALLOWEEN;
    	}else{
    		return null;
    	}
  	}
}