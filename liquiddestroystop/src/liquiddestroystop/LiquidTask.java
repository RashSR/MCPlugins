package liquiddestroystop;
import net.canarymod.Canary;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.position.Location;
import utils.Utils;

public class LiquidTask extends ServerTask {

	private Block block;
    private static final boolean isContinousTask = false;

    public LiquidTask(Block block) {
        super(Canary.getServer(), 10 * Utils.TICKS_PER_SECOND, isContinousTask);
        this.block = block;
    }

    public void run(){
     	Location blockloc = block.getLocation();
        BlockType type = block.getType();
        blockloc.getWorld().setBlockAt(blockloc, type);
    }
}
