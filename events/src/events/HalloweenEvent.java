package events;
import net.canarymod.logger.Logman;
import utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class HalloweenEvent extends EZPlugin implements IEvent{
	private World world;
	private Map<BlockType, ArrayList<Location>> eventBlocks = new HashMap<>();

	public HalloweenEvent(World world){
		this.world = world;
	}

	public void startEvent(){
		logger.info("Das Event Halloween wird gestartet.");
		Utils.WriteToEventFile("halloween");
		world.setRaining(true);
		world.setThundering(true);
		world.setThunderStrength((float)Math.random());
		placeEventBlocks();
	}

	public void endEvent(){
		logger.info("Das Event Halloween wird beendet.");
		Utils.WriteToEventFile("no"); 
		removeEventBlocks();
		world.setRaining(false);
		world.setThundering(false);
	}

	public EventType getEventType(){
		return EventType.HALLOWEEN;
	}

	private void placeEventBlocks(){
		gatherEventBlocks();
		
		for(Map.Entry<BlockType, ArrayList<Location>> entry : eventBlocks.entrySet()){
			BlockType blockType = entry.getKey();
			for (Location loc : entry.getValue())
				world.setBlockAt(loc, blockType);
		}
	}

	private void removeEventBlocks(){
		for(Map.Entry<BlockType, ArrayList<Location>> entry : eventBlocks.entrySet()){
			BlockType blockType = entry.getKey();
			for (Location loc : entry.getValue())
				world.setBlockAt(loc, BlockType.Air);
		}
	}

	private void gatherEventBlocks(){
		eventBlocks.put(BlockType.JackOLantern, getPumpkinList());
		eventBlocks.put(BlockType.SpiderWeb, getSpiderWebList());
	}

	private ArrayList<Location> getPumpkinList(){
		ArrayList<Location> pumpkins = new ArrayList<Location>();
		pumpkins.add(new Location(252, 72, 263));
		pumpkins.add(new Location(248, 73, 263));
		pumpkins.add(new Location(245, 72, 264));
		pumpkins.add(new Location(243, 73, 265));
		pumpkins.add(new Location(242, 73, 263));
		
		return pumpkins;
	}

	private ArrayList<Location> getSpiderWebList(){
		ArrayList<Location> spiderWebs = new ArrayList<Location>();
		spiderWebs.add(new Location(252, 72, 264));
		spiderWebs.add(new Location(250, 71, 261));
		spiderWebs.add(new Location(249, 72, 264));
		spiderWebs.add(new Location(243, 73, 260));
		spiderWebs.add(new Location(245, 74, 262));
		
		return spiderWebs;
	}
}