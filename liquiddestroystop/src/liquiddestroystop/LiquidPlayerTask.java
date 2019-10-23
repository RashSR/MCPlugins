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

public class LiquidPlayerTask extends ServerTask {

	private Location loc;

    public LiquidPlayerTask(Location myLoc) {

        super(Canary.getServer(), 20, false);
        loc = myLoc;

                                  }

     public void run(){

     	loc.getWorld().setBlockAt(loc, BlockType.Dirt);

     }
}
