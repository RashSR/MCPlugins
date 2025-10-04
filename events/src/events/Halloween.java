package events;
import net.canarymod.logger.Logman;
import java.util.ArrayList;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class Halloween extends EZPlugin implements IEvent{
	public static Location[] pumpkins = new Location[5];
	public static Location[] webs = new Location[5];
	private static World world;

	public Halloween(){
		//startEvent();
	}

	public void startEvent(){
		logger.info("Das Event Halloween wird gestartet.");
		events.CurrentEventType = EventEnum.HALLOWEEN;
		fillArrays();
		OwnFileWriter fw = new OwnFileWriter(events.fileName, "halloween");
		logger.info("Wir haben jetzt " + world.getRelativeTime() + " Uhr");
		world.setRaining(true);
		world.setThundering(true);
		world.setThunderStrength((float)Math.random());
		placeBlocks();
	}

	public void endEvent(){
		logger.info("Das Event Halloween wird beendet.");
		events.CurrentEventType = null;
		OwnFileWriter fw = new OwnFileWriter(events.fileName, "no");
		removeBlocks();
		world.setRaining(false);
	}

	public EventEnum getEventType(){
		return EventEnum.HALLOWEEN;
	}

	private static void placeBlocks(){
		for(int i = 0; i < pumpkins.length; i++){
			world.setBlockAt(pumpkins[i], BlockType.JackOLantern);
			if(i < webs.length){
				world.setBlockAt(webs[i], BlockType.SpiderWeb);
			}
		}
	}

	private static void removeBlocks(){
		for(int i = 0; i < pumpkins.length; i++){
			world.setBlockAt(pumpkins[i], BlockType.Air);
			if(i<webs.length){
				world.setBlockAt(webs[i], BlockType.Air);
			}
		}
	}

	private static void fillArrays(){
		fillPumpkinArray();
		fillWebArray();
	}

	private static ArrayList<Location> fillPumpkinArray(){
		ArrayList<Location> pumpkins = new ArrayList<Location>();
		pumpkins.add(new Location(252, 72, 263));
		pumpkins.add(new Location(248, 73, 263));
		pumpkins.add(new Location(245, 72, 264));
		pumpkins.add(new Location(243, 73, 265));
		pumpkins.add(new Location(242, 73, 263));

		return pumpkins;
	}

	private static ArrayList<Location> fillWebArray(){
		ArrayList<Location> spiderWebs = new ArrayList<Location>();
		spiderWebs.add(new Location(252, 72, 264));
		spiderWebs.add(new Location(250, 71, 261));
		spiderWebs.add(new Location(249, 72, 264));
		spiderWebs.add(new Location(243, 73, 260));
		spiderWebs.add(new Location(245, 74, 262));
		
		return spiderWebs;
	}
}