package events;
import net.canarymod.logger.Logman;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class Halloween extends EZPlugin{
	public static Location[] pumpkins = new Location[5];
	public static Location[] webs = new Location[5];
	public static World world;


	public static void endHalloween(){
		logger.info("Das Event Halloween wird beendet.");
		events.CurrentEvent = null;
		OwnFileWriter fw = new OwnFileWriter(events.fileName, "no");
		removeBlocks();
		world.setRaining(false);
	}

	public static void startHalloween(){
		logger.info("Das Event Halloween wird gestartet.");
		events.CurrentEvent = EventEnum.HALLOWEEN;
		fillArrays();
		OwnFileWriter fw = new OwnFileWriter(events.fileName, "halloween");
		logger.info("Wir haben jetzt "+world.getRelativeTime()+" Uhr");
		world.setRaining(true);
		world.setThundering(true);
		world.setThunderStrength((float)Math.random());
		placeBlocks();
	}

	private static void placeBlocks(){
		for(int i = 0; i < pumpkins.length; i++){
			world.setBlockAt(pumpkins[i], BlockType.JackOLantern);
			if(i<webs.length){
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

	private static void fillPumpkinArray(){
		pumpkins[0]=new Location(252, 72, 263);
		world=pumpkins[0].getWorld();
		pumpkins[1]=new Location(248, 73, 263);
		pumpkins[2]=new Location(245, 72, 264);
		pumpkins[3]=new Location(243, 73, 265);
		pumpkins[4]=new Location(242, 73, 263);
	}

	private static void fillWebArray(){
		webs[0]=new Location(252, 72, 264);
		webs[1]=new Location(250, 71, 261);
		webs[2]=new Location(249, 72, 264);
		webs[3]=new Location(243, 73, 260);
		webs[4]=new Location(245, 74, 262);
	}

}