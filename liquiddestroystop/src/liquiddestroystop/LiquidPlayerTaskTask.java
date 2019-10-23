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

public class LiquidPlayerTaskTask extends ServerTask {

	private Location loc;

    public LiquidPlayerTaskTask(Location myLoc) {

        super(Canary.getServer(), 2 * 20, false);
        loc = myLoc;

                                  }

     public void run(){

     	loc.getWorld().setBlockAt(loc, BlockType.Air);
     	World world = loc.getWorld();
     	double x = loc.getX();
     	double y = loc.getY();
     	double z = loc.getZ();

     	int xb = (int)x;
     	int yb = (int)y;
     	int zb = (int)z;

     	Block a = world.getBlockAt(xb, yb, zb);
     	Block b = world.getBlockAt(xb + 1, yb, zb);
     	b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);

     	if (a.getType() == BlockType.Air){
     	a.update();
     	Canary.instance().getServer().broadcastMessage("wir sind da");
     	return;
     }

     }
}
