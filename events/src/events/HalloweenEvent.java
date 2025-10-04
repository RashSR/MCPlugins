package events;
import net.canarymod.logger.Logman;
import utils.Utils;

import java.util.ArrayList;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class HalloweenEvent extends EZPlugin implements IEvent{
	private World world;

	public HalloweenEvent(World world){
		this.world = world;
	}

	public void startEvent(){
		logger.info("Das Event Halloween wird gestartet.");
		OwnFileWriter fw = new OwnFileWriter(Utils.EventFileLocation, "halloween");
		logger.info("Wir haben jetzt " + world.getRelativeTime() + " Uhr");
		world.setRaining(true);
		world.setThundering(true);
		world.setThunderStrength((float)Math.random());
		placeBlocks();
	}

	public void endEvent(){
		logger.info("Das Event Halloween wird beendet.");
		OwnFileWriter fw = new OwnFileWriter(Utils.EventFileLocation, "no");
		removeBlocks();
		world.setRaining(false);
	}

	public EventType getEventType(){
		return EventType.HALLOWEEN;
	}

	private void placeBlocks(){
		for(Location loc : getPumpkinList())
			world.setBlockAt(loc, BlockType.JackOLantern);
		
		for(Location loc : getSpiderWebList())
			world.setBlockAt(loc, BlockType.SpiderWeb);
	}

	private void removeBlocks(){
		for(Location loc : getPumpkinList())
			world.setBlockAt(loc, BlockType.Air);
		
		for(Location loc : getSpiderWebList())
			world.setBlockAt(loc, BlockType.Air);
	}

	private static ArrayList<Location> getPumpkinList(){
		ArrayList<Location> pumpkins = new ArrayList<Location>();
		pumpkins.add(new Location(252, 72, 263));
		pumpkins.add(new Location(248, 73, 263));
		pumpkins.add(new Location(245, 72, 264));
		pumpkins.add(new Location(243, 73, 265));
		pumpkins.add(new Location(242, 73, 263));

		return pumpkins;
	}

	private static ArrayList<Location> getSpiderWebList(){
		ArrayList<Location> spiderWebs = new ArrayList<Location>();
		spiderWebs.add(new Location(252, 72, 264));
		spiderWebs.add(new Location(250, 71, 261));
		spiderWebs.add(new Location(249, 72, 264));
		spiderWebs.add(new Location(243, 73, 260));
		spiderWebs.add(new Location(245, 74, 262));
		
		return spiderWebs;
	}
}