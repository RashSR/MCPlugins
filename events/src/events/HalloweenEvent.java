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
import utils.ServerEventType;

public class HalloweenEvent extends EZPlugin implements IEvent{
	private World world;
	private Map<BlockType, ArrayList<Location>> eventBlocks = new HashMap<>();

	public HalloweenEvent(World world){
		this.world = world;
	}

	public void startEvent(){
		logger.info("Das Event Halloween wird gestartet.");
		world.setRaining(true);
		world.setThundering(true);
		world.setThunderStrength((float)Math.random());
		placeEventBlocks();
		Utils.WriteToEventFile(getEventType());
		Utils.GetCurrentEvent();
	}

	public void endEvent(){
		logger.info("Das Event Halloween wird beendet.");
		removeEventBlocks();
		world.setRaining(false);
		world.setThundering(false);
		Utils.WriteToEventFile(ServerEventType.NONE);
	}

	public ServerEventType getEventType(){
		return ServerEventType.HALLOWEEN;
	}

	private void placeEventBlocks(){
		loadEventBlocks();
		
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

	private void loadEventBlocks(){
		Map<String, ArrayList<Location>> blocks = BlockLocationLoader.load("config/block_locations_halloween.txt");
		eventBlocks.put(BlockType.JackOLantern, blocks.get("jack_o_lantern"));
		eventBlocks.put(BlockType.RedstoneBlock, blocks.get("spider_web"));
	}
}