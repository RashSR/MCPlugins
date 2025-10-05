package events;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.World;
import utils.Utils;
import net.canarymod.chat.ChatFormat;

public class NewYearEvent extends EZPlugin implements IEvent{
    
  private World world;

  public NewYearEvent(World world){
      this.world = world;
  }

  public EventType getEventType(){
		return EventType.NEWYEAR;
	}
    
  public void startEvent(){
    logger.info("Das Event NewYear wird gestartet.");
    String serverMessage = ChatFormat.DARK_GREEN + "Wir wuenschen euch ein" + ChatFormat.GOLD + 
      "frohes " + ChatFormat.DARK_GREEN + "neues Jahr " + ChatFormat.GOLD + Utils.GetYear() + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(events.pluginName, serverMessage); 
  };

  public void endEvent(){
    logger.info("Das Event NewYear wird beendet.");
    Utils.WriteToEventFile("no"); 
  };
}
