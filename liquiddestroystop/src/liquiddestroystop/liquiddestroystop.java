package liquiddestroystop;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.world.LiquidDestroyHook;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.player.BlockPlaceHook;
import utils.Utils;

public class liquiddestroystop extends EZPlugin implements PluginListener {

  public static final String pluginName = "[Sicherheit]";
  private static boolean isEnabled = false;

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @Command(aliases = { "liquiddestroy" },
          description = "Flüßigkeiten verschwinden beim platzieren.",
          permissions = { "" },
          toolTip = "/liquiddestroy")
  public void liquidDestroyCommand(MessageReceiver caller, String[] args) {
    if(isEnabled){
      isEnabled = false;
      displayDeactivationMessage();
    }
    else{
      isEnabled = true;
      displayActivationMessage();
    }  
  }

  @HookHandler
  public void weggespuelt(LiquidDestroyHook event){
    Block flushedBlock = event.getBlock();

    if(flushedBlock.getType() == BlockType.Torch){
      Canary.getServer().addSynchronousTask(new LiquidTask(flushedBlock, 1));
    }
    else if(flushedBlock.getType() == BlockType.TallGrass || flushedBlock.getType() == BlockType.Dandelion || 
       flushedBlock.getType() == BlockType.Poppy || flushedBlock.getType() == BlockType.Carrots || 
       flushedBlock.getType() == BlockType.Potatoes || flushedBlock.getType() == BlockType.SpiderWeb){
      event.setCanceled();
    }
    else if (flushedBlock.getType() == BlockType.NetherWart){
      Canary.getServer().addSynchronousTask(new LiquidTask(flushedBlock, 2));
    }
  }

  @HookHandler
  public void BlockPlaceHookEvent(BlockPlaceHook event){
    if(isEnabled){
      Block placedLiquid = event.getBlockPlaced();
      Location loc = placedLiquid.getLocation();

      if(placedLiquid.getType() == BlockType.LavaFlowing || placedLiquid.getType() == BlockType.WaterFlowing)
        Canary.getServer().addSynchronousTask(new LiquidPlayerTaskTask(loc));
    }
  }

  public void displayActivationMessage(){
    String serverMessage = ChatFormat.DARK_GREEN + "Fluessigkeiten werden" + ChatFormat.GOLD + "gecleart" + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void displayDeactivationMessage(){
    String serverMessage = ChatFormat.DARK_GREEN + "Fluessigkeiten werden" + ChatFormat.GOLD + "nicht " + ChatFormat.DARK_GREEN + "gecleart.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }
}
