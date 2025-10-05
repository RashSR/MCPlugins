package events.christmas;
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
import net.canarymod.hook.world.TimeChangeHook;
import utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import events.IEvent;
import events.BlockLocationLoader;
import events.EventType;

public class ChristmasEvent extends EZPlugin implements IEvent{
    private Map<BlockType, ArrayList<Location>> eventBlocks = new HashMap<>();

    private ArrayList<Location> dnaSnow;
    private ArrayList<CandyCane> candyCanes;
    private static final int CANDY_CANE_MAX_HEIGHT = 30;
    public World world;
    private boolean isRunning = false;

    public ChristmasEvent(World world){
        this.world = world;
    }

    public void startEvent(){
        logger.info("Das Event Christmas wird gestartet.");
        candyCanes = CandyCane.MakeCandySticksInArea(new Location(267, 17, 227), new Location(295, 17, 199), CANDY_CANE_MAX_HEIGHT, world);
        placeEventBlocks();
        isRunning = true;
        dnaMakeSnow();
        Utils.WriteToEventFile("christmas");
    }

    public void endEvent(){
        logger.info("Das Event Christmas wird beendet.");
        removeEventBlocks();
        isRunning = false;
        Utils.WriteToEventFile("no");
    }

    public EventType getEventType(){
		return EventType.CHRISTMAS;
	}

    public void dnaMakeSnow(){
        //TODO darf nicht mittelblock sein LOC -> 281, 18, 213
        dnaSnow = new ArrayList<>();
        int count = 0;
        for(int i = 267; i <= 295; i++){
            for(int j = 199; j <= 236; j++){
                Location snowLocation = new Location(i, 18, j);
                Block block = world.getBlockAt(i, 18, j);
                if(block.getType() == BlockType.Air && !(world.getBlockAt(i, 17, j).getType() == BlockType.Water)){
                    double randomSnowFactor = Math.random();
                    if(randomSnowFactor < 0.8 && count <= 1000){
                        if(!(snowLocation.getX() == 281 && snowLocation.getZ() == 213)){
                            world.setBlockAt(snowLocation, BlockType.Snow);
                            count = count + 1;
                            dnaSnow.add(snowLocation);
                        }
                    }
                }
            }
        }
        stopSnowMelt s = new stopSnowMelt();
        s.start();
    }

    public class stopSnowMelt extends Thread{
        public void run(){
            while(isRunning){
                for(Location loc : dnaSnow){
                    if(world.getBlockAt(loc).getType() != BlockType.Snow){
                        world.setBlockAt(loc, BlockType.Snow);
                    }
                }
            }
        }
    }

    @HookHandler
    public void stopLeafDecay(LeafDecayHook event){
        if(isRunning)
            event.setCanceled();
    }

    private void loadEventBlocks(){
        Map<String, ArrayList<Location>> blocks = BlockLocationLoader.load("config/block_locations_christmas.txt");
		eventBlocks.put(BlockType.Snow, blocks.get("snow"));
		eventBlocks.put(BlockType.RedstoneBlock, blocks.get("redstone"));
        eventBlocks.put(BlockType.GoldBlock, blocks.get("gold"));
		eventBlocks.put(BlockType.LapisBlock, blocks.get("lapis"));
        eventBlocks.put(BlockType.PineLeaves, blocks.get("spruce_leaves"));
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

        removeCandySticks();
        removeSnow();

        //readd existing decoration
        world.setBlockAt(new Location(252, 71, 261), BlockType.Workbench);
        world.setBlockAt(new Location(245, 71, 261), BlockType.Jukebox);
	}

    private void removeCandySticks(){
        for(CandyCane candyCane : candyCanes){
            candyCane.Remove();
        }
    }

    private void removeSnow(){
        for(Location loc : dnaSnow){
            world.setBlockAt(loc, BlockType.Air);
        }
    }
}