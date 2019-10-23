package cantbuild;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockPlaceHook;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.chat.ChatFormat;

public class CantBuild extends EZPlugin implements PluginListener {

   @Override
    public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }
  
    @HookHandler 
  public void cantbuild(BlockPlaceHook event){

    Block placedblock = event.getBlockPlaced();
    int x = placedblock.getX();
    int y = placedblock.getY();
    int z = placedblock.getZ();

     if(x >= 85 && x <= 93 && y >= 79 && y <= 89 && z >= 321 && z <= 333){

       event.setCanceled();
       cantbuildmessage();

    }
  }

  public void cantbuildmessage(){

    String msg1 = "[BuildIt] ";
    String msg2 = "Hier koennen ";
    String msg3 = "keine ";
    String msg4 = "Bloecke platziert werden.";

    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);


  }


}
