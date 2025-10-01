package countdown;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;
import utils.Utils;

public class countdown extends EZPlugin {

  private static boolean isEnabled = false;
  protected static final String pluginName = "[Countdown]";
  
  @Command(aliases = { "countdown" },
            description = "countdown plugin",
            permissions = { "*" },
            toolTip = "/countdown")
  public void countdownCommand(MessageReceiver caller, String[] args) {
    if(isEnabled){
      Utils.BroadcastServerMessage(pluginName, ChatFormat.DARK_GREEN + "Es ist bereits ein Countdown in Gange!");
      return;
    }
    
    if(args.length < 2 || args.length > 3){
      String serverMessage = ChatFormat.DARK_GREEN + "Geben sie " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Argumente ein!";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      return;
    }

    int time = Integer.valueOf(args[1]);
    if(time > 0){
      isEnabled = true;

      if(args.length == 2){
        String serverMessage = ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + time + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.";
        Utils.BroadcastServerMessage(pluginName, serverMessage);
        CountdownTask task = new CountdownTask(time);
        Canary.getServer().addSynchronousTask(task);
      }

      else if(args.length == 3){
        if(args[2].equalsIgnoreCase("min")){
          String serverMessage = ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + time + ChatFormat.DARK_GREEN + " Minuten heruntergefahren.";
          Utils.BroadcastServerMessage(pluginName, serverMessage);
          time = time * 60;
          CountdownTask task = new CountdownTask(time);
          Canary.getServer().addSynchronousTask(task);
        }
        else if(args[2].equalsIgnoreCase("h")){
          String serverMessage = ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + time + ChatFormat.DARK_GREEN + " Stunden heruntergefahren.";
          Utils.BroadcastServerMessage(pluginName, serverMessage);
          time = time * 3600;
          CountdownTask task = new CountdownTask(time);
          Canary.getServer().addSynchronousTask(task);
        }
      }
    }      
    else{
      String serverMessage = ChatFormat.DARK_GREEN + "Bitte geben Sie eine " + ChatFormat.GOLD + "positive" + ChatFormat.DARK_GREEN + " Zahl in Sekunden ein.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
    }
  }
}
