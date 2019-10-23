package butler;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.HealthChangeHook;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.hook.player.PlayerDeathHook;
import java.util.List;
import java.util.ArrayList;

public class Butler extends EZPlugin implements PluginListener{

  String dnsir = "";
  String dnbutler = "";
  public static boolean butleron = false;
  String msg1 = "[Butler/Sir] ";

  @Override
   public boolean enable() { 

   Canary.hooks().registerListener(this, this);
   return super.enable(); 

                           }
  @Command(aliases = {"sir"},
           description = "sir plugin",
           permissions = {"*"},
           toolTip = "/sir")
  public void sirCommand(MessageReceiver caller, String[] args){

    if(butleron){butleron = false;}

    if(!butleron){ 

       Player butler = (Player)caller;
       Player sir = Canary.instance().getServer().getPlayer(args[1]);

      if(!(butler.getDisplayName().equalsIgnoreCase(args[1]))){
      sirbut(butler, sir);
                                                         }
      else{doubleplayermsg();}

                  }

                                                                }

  @Command(aliases = { "butler" },
            description = "butler plugin",
            permissions = { "*" },
            toolTip = "/butler")
  public void butlerCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 

      if(butleron){butleron = false;}

      if(!butleron){

      Player sir = (Player)caller;
      Player butler = Canary.instance().getServer().getPlayer(args[1]);

      if(!(sir.getDisplayName().equalsIgnoreCase(args[1]))){

      sirbut(butler, sir);
                                                         }
      else{doubleplayermsg();}

                    }
                                  }
                                                                    }

  @HookHandler
   public void butlerverliertleben(HealthChangeHook event){

      if(butleron){

       Player ptakedmg = event.getPlayer();
       String pname = ptakedmg.getDisplayName();
       float healtbefore = event.getOldValue();
       float healtafter = event.getNewValue();

       if (pname.equalsIgnoreCase(dnbutler)){
         if(healtbefore > healtafter){

        playSound(ptakedmg.getLocation(), SoundEffect.Type.BOW_HIT, 1.0f, 1.0f);

                                    }
         if(healtafter <= 4){

           String msg2 = "Der Butler ";
           String msg3 = " konnte seinem Sir nicht treu dienen.";
           Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.BLUE + pname + ChatFormat.DARK_GREEN + msg3);
           butleron = false;
         }

                                                                          }
                     }
                                                   }
  public void sirbut(Player butler, Player sir){

      List<Player> spielerliste = new ArrayList <Player>();
      spielerliste = Canary.instance().getServer().getPlayerList();

      if(spielerliste.size() <= 1){

        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Es sind gerade" + ChatFormat.GOLD + " zu wenig " + ChatFormat.DARK_GREEN + "Spieler online zum Butler/Sir spielen.");

                                   }

      if(spielerliste.size() > 1){

        butleron = true;
        Canary.getServer().addSynchronousTask(new ButlerTask(sir, butler));

        dnsir = sir.getDisplayName();
        String msg2 = "Der Sir ist ";
        String msg3 = " und der Butler ist ";
        dnbutler = butler.getDisplayName();    

        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.BLUE + dnsir + ChatFormat.DARK_GREEN + msg3 + ChatFormat.BLUE + dnbutler + ChatFormat.DARK_GREEN + ".");

                                    }
  }

  public void doubleplayermsg(){

    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Man kann Butler/Sir nicht mit sich selbst spielen.");

                                }

}
