package cantdestroy;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockDestroyHook;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import utils.Utils;

public class cantdestroy extends EZPlugin implements PluginListener{

  public static final String pluginName = "[Sicherheit]";
  private static boolean isEnabled = true;

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

  @Command(aliases = { "break" },
          description = "Blöcke gehen (nicht) kaputt",
          permissions = { "" },
          toolTip = "/break, or /break info")
  public void breakCommand(MessageReceiver caller, String[] args) {
    if (hasRightArgumentCount(args) && caller instanceof Player) { 
      Player player = (Player)caller;

      if(args.length == 1){
        if (isEnabled){
          isEnabled = false;
          displayBreakMessage();
        }
        else{
          isEnabled = true;
          displayNoBreakMessage();
        }
      }
      else if(args.length == 2){
        if(args[1].equalsIgnoreCase("info")){
          if (isEnabled)
            displayNoBreakMessage();
          else if(!isEnabled)
            displayBreakMessage();    
        }
      }
    }
  }

  @HookHandler
  public void BlockDestroyHookEvent(BlockDestroyHook event) {
    if(isEnabled){
      Player player = event.getPlayer();
      int x = event.getBlock().getX();
      int y = event.getBlock().getY();
      int z = event.getBlock().getZ();

      //for bedwars
      if(z >= 300 && z <= 525 && x > 300 && x < 555){
        if(event.getBlock().getType() == BlockType.BedBlock || event.getBlock().getType() == BlockType.SandstoneBlank)
          return; 
      }

      //for buildIt
      if(x >= 86 && x <= 90 && z >= 325 && z<= 329 && y == 78)
        return;

      //for Zombie
      if(x >= 245 && x <= 271 && y >= 69 && y <= 80 && z >= 525 && z <= 549){
        if (event.getBlock().getType() == BlockType.Reed || event.getBlock().getType() == BlockType.OakSapling || event.getBlock().getType() == BlockType.OakLog || event.getBlock().getType() == BlockType.OakLeaves || event.getBlock().getType() == BlockType.OakWood || event.getBlock().getType() == BlockType.NetherWart || event.getBlock().getType() == BlockType.GlowStone || event.getBlock().getType() == BlockType.Carrots || event.getBlock().getType() == BlockType.Potatoes || event.getBlock().getType() == BlockType.Melon)
          return;
      }

      if(event.getBlock().getType() == BlockType.SlimeBlock){
        Item iteminderhand = player.getItemHeld();
        if(iteminderhand.getType() == ItemType.GoldSpade){
          player.getInventory().removeItem(iteminderhand.getType());
          player.setModeId(2);
          event.getBlock().getLocation().getWorld().setBlockAt(event.getBlock().getLocation(), BlockType.Air);
        }
      }   

      event.setCanceled();
    }  
  }

  private boolean hasRightArgumentCount(String[] args){
    if(args.length > 2){
      Utils.BroadcastWrongArgumentLengthMessage(pluginName);
      broadcastUsageMessage();
      return false;
    }

    return true;
  }

  private void broadcastUsageMessage(){
    String msg2 = ChatFormat.GOLD + "/break\n";
    String msg3 = "/break info";
    String serverMessage = ChatFormat.DARK_GREEN + "Verwendung:\n" + msg2 + msg3;
    Utils.SendServerMessage(pluginName, serverMessage);
  }

  public void displayBreakMessage() {
    String msg2 = "Bloecke koennen ";
    String msg3 = "zerstoert ";
    String msg4 = "werden!";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.SendServerMessage(pluginName, serverMessage);
  }

  public void displayNoBreakMessage(){
    String msg2 = "Bloecke koennen ";
    String msg3 = "nicht ";
    String msg4 = "zerstoert werden!";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.SendServerMessage(pluginName, serverMessage);
  }
}
