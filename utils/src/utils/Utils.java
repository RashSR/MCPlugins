package utils;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.Item;

public class Utils {
  
  public static int SURVIVAL_MODE = 0;
  public static int CREATIVE_MODE = 1;
  public static int ADVENTURE_MODE = 2;
  public static int SPECTATOR_MODE = 3;
  public static Location HubLocation = new Location(251, 71, 262);

  public static Integer CalculateDistanceBetweenPlayers(Player sir, Player butler){
    double xs = sir.getX();
    double ys = sir.getY();
    double zs = sir.getZ();
    double xb = butler.getX();
    double yb = butler.getY();
    double zb = butler.getZ();
    double d = Math.sqrt((xs - xb)*(xs - xb) + (ys - yb)*(ys - yb) + (zs - zb)*(zs - zb));
    int distance = (int)d;
    return distance;
  }

  public static void SendServerMessage(String pluginName, String message){
      String plugin = ChatFormat.DARK_AQUA + pluginName;
      String serverMessage = plugin + " " + message;
      Canary.instance().getServer().broadcastMessage(serverMessage);
  }

  public static void BroadcastWrongArgumentLengthMessage(String pluginName){
    String serverMessage = ChatFormat.DARK_GREEN + "Falsche Anzahl an Argumenten!";
    Utils.SendServerMessage(pluginName, serverMessage);
  }

  public static void ClearPlayerInventory(Player player){
    player.getInventory().clearInventory();
    Item schuhe = player.getInventory().getBootsSlot();
    player.getInventory().removeItem(schuhe);
    Item hose = player.getInventory().getLeggingsSlot();
    player.getInventory().removeItem(hose);
    Item brustplatte = player.getInventory().getChestplateSlot();
    player.getInventory().removeItem(brustplatte);
    Item helm = player.getInventory().getHelmetSlot();
    player.getInventory().removeItem(helm);
  }
}
