package butler;
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
import java.util.List;
import utils.Utils;

public class Butler extends EZPlugin implements PluginListener{
  public String displayNameButler = "";
  private static boolean IsPluginEnabled = false;
  public static final String pluginName = "[Butler/Sir]";

  @Override
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }

  @Command(aliases = {"sir"},
           description = "sir plugin",
           permissions = {"*"},
           toolTip = "/sir")
  public void SetSirCommand(MessageReceiver caller, String[] args){
    if(hasRightArgumentCount(args) && caller instanceof Player && hasServerEnoughPlayer()){
      if(IsPluginEnabled)
        IsPluginEnabled = false;

      if(args[1].equalsIgnoreCase("info")){
        broadcastUsageMessage();
        return;
      }

      if(!IsPluginEnabled && isPlayerOnline(args[1])){ 
        Player butler = (Player)caller;
        Player sir = Canary.instance().getServer().getPlayer(args[1]);

        if(!(butler.getDisplayName().equalsIgnoreCase(args[1])))
          startGame(butler, sir);
        else
          broadcastDuplicatePlayerMessage();
      }
    }
  }

  @Command(aliases = { "butler" },
            description = "butler plugin",
            permissions = { "*" },
            toolTip = "/butler")
  public void SetButlerCommand(MessageReceiver caller, String[] args) {
    if(hasRightArgumentCount(args) && caller instanceof Player && hasServerEnoughPlayer()){ 
      if(IsPluginEnabled)
        IsPluginEnabled = false;

      if(args[1].equalsIgnoreCase("info")){
        broadcastUsageMessage();
        return;
      }

      if(!IsPluginEnabled && isPlayerOnline(args[1])){
        Player sir = (Player)caller;
        Player butler = Canary.instance().getServer().getPlayer(args[1]);

        if(!(sir.getDisplayName().equalsIgnoreCase(args[1])))
          startGame(butler, sir);

      else
        broadcastDuplicatePlayerMessage();
      }
    }
  }

  @HookHandler
  public void BulterHealthDecreaseEvent(HealthChangeHook event){
    if(IsPluginEnabled){
      Player damagedPlayer = event.getPlayer();
      String playerName = damagedPlayer.getDisplayName();
      float startingHealth = event.getOldValue();
      float newHealth = event.getNewValue();

      if (playerName.equalsIgnoreCase(displayNameButler)){
        if(startingHealth > newHealth)
          playSound(damagedPlayer.getLocation(), SoundEffect.Type.BOW_HIT, 1.0f, 1.0f);

        if(newHealth <= 4){
          String msg2 = "Der Butler ";
          String msg3 = " konnte seinem Sir nicht treu dienen.";
          String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.BLUE + playerName + ChatFormat.DARK_GREEN + msg3;
          Utils.BroadcastServerMessage(pluginName, serverMessage);
          IsPluginEnabled = false;
        }
      }
    }
  }

  public void startGame(Player butler, Player sir){
    IsPluginEnabled = true;
    Canary.getServer().addSynchronousTask(new ButlerTask(sir, butler));
    String displayNameSir = sir.getDisplayName();
    displayNameButler = butler.getDisplayName(); 
    String msg2 = "Der Sir ist ";
    String msg3 = " und der Butler ist ";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.BLUE + 
      displayNameSir + ChatFormat.DARK_GREEN + msg3 + ChatFormat.BLUE + 
      displayNameButler + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private boolean hasRightArgumentCount(String[] args){
    if(args.length != 2){
      Utils.BroadcastWrongArgumentLengthMessage(pluginName);
      broadcastUsageMessage();
      return false;
    }
    
    return true;
  }

  private void broadcastUsageMessage(){
    String msg2 = ChatFormat.GOLD + "/sir <Spielername Butler>\n";
    String msg3 = "/butler <Spielername Sir>";
    String serverMessage = ChatFormat.DARK_GREEN + "Verwendung:\n" + msg2 + msg3;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void broadcastDuplicatePlayerMessage(){
    String serverMessage = ChatFormat.DARK_GREEN + "Man kann Butler/Sir nicht mit sich selbst spielen.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private boolean hasServerEnoughPlayer(){
    List<Player> playerList = Canary.instance().getServer().getPlayerList();
    
    if(playerList.size() <= 1){
      String serverMessage = ChatFormat.DARK_GREEN + "Es sind gerade" + ChatFormat.GOLD + 
        "zu wenig" + ChatFormat.DARK_GREEN + "Spieler online zum Butler/Sir spielen.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      return false;
    }

    return true;
  }

  private boolean isPlayerOnline(String playerName){
    List<Player> playerList = Canary.instance().getServer().getPlayerList();
    for(Player p : playerList){
      if(p.getDisplayName().equalsIgnoreCase(playerName))
        return true;
    }

    String serverMessage = ChatFormat.DARK_GREEN + "Der Spieler" + ChatFormat.BLUE + playerName + ChatFormat.DARK_GREEN + " ist nicht online.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    return false;
  }
}
