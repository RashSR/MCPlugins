package liquiddestroystop;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
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

public class liquiddestroystop extends EZPlugin implements PluginListener {

@Override 
  public boolean enable() {

  Canary.hooks().registerListener(this, this);
  return super.enable();

                          }

  int i = 1;

   public static boolean an = false;

 @Command(aliases = { "liquiddestroy" },
          description = "Flüßigkeiten verschwinden beim platzieren.",
          permissions = { "" },
          toolTip = "/liquiddestroy")

  public void liquiddestroyCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 

      Player me = (Player)caller;

      if(i == 1){
      an = true;
      onmessage();
      i = i + 1;
      return;
      }
      if(i == 2){
      an = false;
      offmessage();
      i = i - 1;
      }

                                  }
                                                                   }


@HookHandler
public void weggespuelt(LiquidDestroyHook event){


  Block weggeschwemmt = event.getBlock();

  if(weggeschwemmt.getType() == BlockType.Torch){



    int i = 1;
    Canary.getServer().addSynchronousTask(new LiquidTask(weggeschwemmt, i));

  }

  if(weggeschwemmt.getType() == BlockType.TallGrass || weggeschwemmt.getType() == BlockType.Dandelion || weggeschwemmt.getType() == BlockType.Poppy || weggeschwemmt.getType() == BlockType.Carrots || weggeschwemmt.getType() == BlockType.Potatoes || weggeschwemmt.getType() == BlockType.SpiderWeb){

    event.setCanceled();

  }

  if (weggeschwemmt.getType() == BlockType.NetherWart){

    int i = 2;
    Canary.getServer().addSynchronousTask(new LiquidTask(weggeschwemmt, i));
  }

}

@HookHandler
public void liquidgesetzt(BlockPlaceHook event){

if(an){
 Block lavawasser = event.getBlockPlaced();
 Location loc = lavawasser.getLocation();


 if(lavawasser.getType() == BlockType.LavaFlowing || lavawasser.getType() == BlockType.WaterFlowing){

//Canary.getServer().addSynchronousTask(new LiquidPlayerTask(loc));
  Canary.getServer().addSynchronousTask(new LiquidPlayerTaskTask(loc));


  }

 }




}

 public void onmessage(){

  String msg1 = "[Sicherheit] ";
  String msg2 = "Fluessigkeiten werden gecleart.";
  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2);

                        }

 public void offmessage(){

  String msg1 = "[Sicherheit]";
  String msg2 = "Fluessigkeiten werden nicht gecleart";
  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2);

                         }

}
