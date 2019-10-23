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

public class LiquidTask extends ServerTask {

	private Block block;
	private int i;

    public LiquidTask(Block myBlock, int myi) {

        super(Canary.getServer(), 10 * 20, false);
        block = myBlock;
        i = myi;

                                  }

     public void run(){

     	Location blockloc = block.getLocation();

     	if (i == 1){

     	blockloc.getWorld().setBlockAt(blockloc, BlockType.Torch);

         }

        if (i == 2){

         blockloc.getWorld().setBlockAt(blockloc, BlockType.NetherWart);


        }




     }


}
