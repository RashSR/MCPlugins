package events;
import com.pragprog.ahmine.ez.EZPlugin;
import utils.ServerEventType;
import utils.Utils;
import net.canarymod.api.world.World;

public class PrideEvent extends EZPlugin implements IEvent{
    private World world;
    
    public PrideEvent(World world){
      this.world = world;
    }

  public ServerEventType getEventType(){
		return ServerEventType.PRIDE;
	}
    
  public void startEvent(){
    logger.info("Das Event Pride wird gestartet.");
    Utils.WriteToEventFile(getEventType());
  };

  public void endEvent(){
    logger.info("Das Event Pride wird beendet.");
    Utils.WriteToEventFile(ServerEventType.NONE);
  };
}
