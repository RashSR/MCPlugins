package events;
import net.canarymod.logger.Logman;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import dna.DNA;

public class Halloween extends EZPlugin{
	public static Location[] pumpkins = new Location[2];
	public static World world;

	public static void endHalloween(){
		logger.info("Das Event Halloween wird beendet.");
		events.myEvent=null;
		removeBlocks();
	}

	public static void startHalloween(){
		logger.info("Das Event Halloween wird gestartet.");
		events.myEvent=EventEnum.HALLOWEEN;
		fillArrays();
		placeBlocks();
	}

	private static void placeBlocks(){
		for(int i = 0; i < pumpkins.length; i++){
			world.setBlockAt(pumpkins[i], BlockType.JackOLantern);
		}
	}

	private static void removeBlocks(){
		for(int i = 0; i < pumpkins.length; i++){
			world.setBlockAt(pumpkins[i], BlockType.Air);
		}
	}

	private static void fillArrays(){
		fillPumpkinArray();
	}

	private static void fillPumpkinArray(){
		pumpkins[0]=new Location(252, 72, 263);
		world=pumpkins[0].getWorld();
		pumpkins[1]=new Location(248, 73, 263);
	}

}