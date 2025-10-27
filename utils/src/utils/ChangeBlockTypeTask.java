package utils;
import java.util.List;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;

public class ChangeBlockTypeTask extends ServerTask{
    private Block blockToChange;
    private List<BlockType> cyclingTypes;
    private int cyclingIndex;

    public ChangeBlockTypeTask(int taskDelayInTicks, boolean isContinousTask, Block blockToChange, List<BlockType> cyclingTypes) {
        super(Canary.getServer(), taskDelayInTicks, isContinousTask);
        this.blockToChange = blockToChange;
        this.cyclingTypes = cyclingTypes;
        this.cyclingIndex = 0;
    }  
    
    public void run(){
        if(cyclingIndex >= cyclingTypes.size())
            cyclingIndex = 0;
        
        BlockType newType = cyclingTypes.get(cyclingIndex);
        blockToChange.setType(newType);
        blockToChange.update();
        cyclingIndex++;
    }
}
