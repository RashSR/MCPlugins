package souppvp;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.plugin.PluginListener;
import utils.Utils;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.RedstoneChangeHook;

public class Souppvp extends EZPlugin implements PluginListener{

  private boolean isEnabled = false;
  public static final String pluginName = "[1vs1]";

  @Override
  public boolean enable() { 
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }

 @Command(aliases = { "soup" },
          description = "Pilzsuppen regenerieren Leben.",
          permissions = { "" },
          toolTip = "/soup")
  public void soupCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 
      Player player = (Player)caller;

      if(isEnabled)
        displayDecativationMethod();
      else
        displayActivationMethod();
      
      isEnabled = !isEnabled;
    }
  }

  private void removeSoupFromSelectedHotbarSlot(Player player) {
    int hotbar = player.getInventory().getSelectedHotbarSlotId();
    player.getInventory().setSlot(ItemType.Bowl, 1, hotbar);
  }

  @HookHandler
  public void onInteract(ItemUseHook event) {
    if(isEnabled){
      Player player = event.getPlayer();

      if(player.getItemHeld().getType() == ItemType.MushroomSoup) {
        float health = player.getHealth();
        int hungerBar = player.getHunger();
        
        if (health < 20){
          player.setHealth(health + 8);
          event.setCanceled();
          removeSoupFromSelectedHotbarSlot(player);   
        }
        else{
          if (hungerBar < 20){
            player.setHunger(hungerBar + 5);
            event.setCanceled();
            removeSoupFromSelectedHotbarSlot(player);
          }
        }
      }
    }
  }

  @HookHandler
  public void telemitdruckplatte1vs1(RedstoneChangeHook event){
    Block druckplatte = event.getSourceBlock();
    Location locdruckplatte = druckplatte.getLocation();

    if(EZPlugin.locEqual(locdruckplatte, Utils.PressurePlate1vs1Location) && !isEnabled){
      isEnabled = true;
      displayActivationMethod();
    }
  }

 public void displayActivationMethod(){
    String msg2 = "Suppen ";
    String msg3 = "regenerieren ";
    String msg4 = "Leben!";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

 public void displayDecativationMethod(){
    String msg2 = "Suppen regenerieren ";
    String msg3 = "kein ";
    String msg4 = "Leben!";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }
}