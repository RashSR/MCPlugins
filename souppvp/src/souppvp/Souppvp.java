package souppvp;

import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.*;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.entity.living.humanoid.Human;
import net.canarymod.api.entity.living.humanoid.NonPlayableCharacter;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Vector3D;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.inventory.*;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.PlayerInventory;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.plugin.PluginListener;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.RedstoneChangeHook;



public class Souppvp extends EZPlugin implements PluginListener{

  

   public static boolean an = false;

 @Command(aliases = { "soup" },
          description = "Pilzsuppen regenerieren Leben.",
          permissions = { "" },
          toolTip = "/soup")

  public void soupCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 

      Player me = (Player)caller;

      if(an == false){

      an = true;
      onmessage();
      return;

      }

      if(an == true){

      an = false;
      offmessage();
      return;

      }

                                  }
                                                                   }

   @Override
  public boolean enable() { 

    Canary.hooks().registerListener(this, this);
    return super.enable(); 

                          }
  
  public void cleaninv(Player player) {

   int hotbar = player.getInventory().getSelectedHotbarSlotId();
    player.getInventory().setSlot(ItemType.Bowl, 1, hotbar);

                                      }


  @HookHandler
  public void onInteract(ItemUseHook event) {

    if (an){
   
    Player player = event.getPlayer();

    if (player.getItemHeld().getType() == ItemType.MushroomSoup) {
      
      float health = player.getHealth();
      int food = player.getHunger();

      if (health < 20){

        player.setHealth(health + 8);
        event.setCanceled();
        cleaninv(player);   

                      }

      if (health >=20){

          if (food < 20){

            player.setHunger(food + 5);
            event.setCanceled();
            cleaninv(player);

                        }
                      }
                                                                }
              }
                                              }

   @HookHandler
  public void telemitdruckplatte1vs1(RedstoneChangeHook event){

    Block druckplatte = event.getSourceBlock();
    Location locdruckplatte = druckplatte.getLocation();
    double x = locdruckplatte.getX();
    double y = locdruckplatte.getY();
    double z = locdruckplatte.getZ();
    World world = locdruckplatte.getWorld();

    int xdruckplatte = (int)x;
    int ydruckplatte = (int)y;
    int zdruckplatte = (int)z;

    if(xdruckplatte == 107 && ydruckplatte == 151 && zdruckplatte == 303 && an == false){

      an = true;
      onmessage();




   }


  }

 public void onmessage(){

  String msg1 = "[1vs1] ";
  String msg2 = "Suppen ";
  String msg3 = "regenerieren ";
  String msg4 = "Leben!";
  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);

                        }

 public void offmessage(){

  String msg1 = "[1vs1] ";
  String msg2 = "Suppen regenerieren ";
  String msg3 = "kein ";
  String msg4 = "Leben!";
  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);

                         }
}
