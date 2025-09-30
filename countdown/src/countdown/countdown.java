package countdown;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;

public class countdown extends EZPlugin {

  public static boolean istan = false;
  
  @Command(aliases = { "countdown" },
            description = "countdown plugin",
            permissions = { "*" },
            toolTip = "/countdown")
  public void countdownCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 
      Player player = (Player)caller;
      int zeit = Integer.valueOf(args[1]);

      if(istan == true){
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN + "Es ist bereits ein Countdown in Gange!");
        return;
      }
      
      if(args.length < 2 || args.length > 3){
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN + "Geben sie " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Argumente ein!");
        return;
      }

      if(zeit > 0){

        istan = true;

        if(args.length == 2){

          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + zeit + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.");
          CountdownTask task = new CountdownTask(zeit, istan);
          Canary.getServer().addSynchronousTask(task);

                            }

        if(args.length == 3){
          if(args[2].equalsIgnoreCase("min")){
            
            Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + zeit + ChatFormat.DARK_GREEN + " Minuten heruntergefahren.");
            zeit = zeit * 60;
            CountdownTask task = new CountdownTask(zeit, istan);
            Canary.getServer().addSynchronousTask(task);

                                             }
          if(args[2].equalsIgnoreCase("h")){

            Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + zeit + ChatFormat.DARK_GREEN + " Stunden heruntergefahren.");
            zeit = zeit * 3600;
            CountdownTask task = new CountdownTask(zeit, istan);
            Canary.getServer().addSynchronousTask(task);

                                           }
        }
        }      

      else{

        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN + "Bitte geben Sie eine " + ChatFormat.GOLD + "positive" + ChatFormat.DARK_GREEN + " Zahl in Sekunden ein.");

           }
                                  }
                                                                       }


}
