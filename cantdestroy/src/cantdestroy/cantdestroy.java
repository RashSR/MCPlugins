package cantdestroy;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
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
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.entity.*;



public class cantdestroy extends EZPlugin implements PluginListener{

  public static boolean an = true;

 @Command(aliases = { "break" },
          description = "Blöcke gehen (nicht) kaputt",
          permissions = { "" },
          toolTip = "/break, or /break info")

  public void breakCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 

      Player me = (Player)caller;

      if(args.length == 1){

      if (an == true){
      an = false;
      breakmessage();
      return;

      }

      if (an == false){

      an = true;
      nobreakmessage();
      return;

                      }
      }

      if(args.length == 2){

        if(args[1].equalsIgnoreCase("info")){

         if (an){

        nobreakmessage();

                }

      if(!an){

        breakmessage();

               }
        }

      }
      

                                  }
                                                                   }

   @Override
    public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

  @HookHandler
  public void cantdestroy(BlockDestroyHook event) {
    if (an){

    Player player = event.getPlayer();

    int x = event.getBlock().getX();
    int y = event.getBlock().getY();
    int z = event.getBlock().getZ();

      if(event.getBlock().getType() == BlockType.SlimeBlock){

        Item iteminderhand = player.getItemHeld();
        event.setCanceled();

      if(iteminderhand.getType() == ItemType.GoldSpade){

      //iteminderhand.setDamage(iteminderhand.getDamage() + 1);
      player.getInventory().removeItem(iteminderhand.getType());
      player.setModeId(2);
      event.getBlock().getLocation().getWorld().setBlockAt(event.getBlock().getLocation(), BlockType.Air);

     }
   }   

    if(z >= 300 && z <= 525 && x > 300 && x < 555){

      if(event.getBlock().getType() == BlockType.BedBlock || event.getBlock().getType() == BlockType.SandstoneBlank){

        return; // für bedwars

      }
    }

    if(x >= 86 && x <= 90 && z >= 325 && z<= 329 && y == 78){

      return; // für builtIt

    }

      if(x >= 245 && x <= 271 && y >= 69 && y <= 80 && z >= 525 && z <= 549){

         if (event.getBlock().getType() == BlockType.Reed || event.getBlock().getType() == BlockType.OakSapling || event.getBlock().getType() == BlockType.OakLog || event.getBlock().getType() == BlockType.OakLeaves || event.getBlock().getType() == BlockType.OakWood || event.getBlock().getType() == BlockType.NetherWart || event.getBlock().getType() == BlockType.GlowStone || event.getBlock().getType() == BlockType.Carrots || event.getBlock().getType() == BlockType.Potatoes || event.getBlock().getType() == BlockType.Melon) {

        return;
                                                                           }

          else{

            event.setCanceled();
            
          }
           }

      else{

        event.setCanceled();
      }
          }  
  }

   @Command(aliases = { "breakinfo" },
          description = "Blöcke gehen (nicht) kaputt",
          permissions = { "" },
          toolTip = "/breakinfo")

  public void breakinfoCommand(MessageReceiver caller, String[] args){

    if(caller instanceof Player){

      Player player = (Player)caller;

      if(args.length == 1){

      if (an){

        nobreakmessage();

      }

      if(!an){

        breakmessage();

      }
    }
  }

    
  }




  public void breakmessage() {

    String msg1 = "[Sicherheit] ";
    String msg2 = "Bloecke koennen ";
    String msg3 = "zerstoert ";
    String msg4 = "werden!";
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);

  }

 public void nobreakmessage(){

   String msg1 = "[Sicherheit] ";
   String msg2 = "Bloecke koennen ";
   String msg3 = "nicht ";
   String msg4 = "zerstoert werden!";
   Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);

 }

}
