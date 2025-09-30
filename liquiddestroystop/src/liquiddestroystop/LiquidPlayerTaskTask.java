package liquiddestroystop;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import utils.Utils;

public class LiquidPlayerTaskTask extends ServerTask {

	private Location loc;
	private static final boolean isContinousTask = false;

    public LiquidPlayerTaskTask(Location loc) {
        super(Canary.getServer(), 2 * Utils.TICKS_PER_SECOND, isContinousTask);
        this.loc = loc;
	}

	public void run(){
		World world = loc.getWorld();
     	world.setBlockAt(loc, BlockType.Air);
     	double x = loc.getX();
     	double y = loc.getY();
     	double z = loc.getZ();

     	Block a = world.getBlockAt((int)x, (int)y, (int)z);
     	Block b = world.getBlockAt((int)x + 1, (int)y, (int)z);
     	b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);

     	if (a.getType() == BlockType.Air){
			a.update();
			Canary.instance().getServer().broadcastMessage("wir sind da");
     	}
    }
}
