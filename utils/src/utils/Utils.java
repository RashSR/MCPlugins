package utils;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;

public class Utils {
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
      String serverMessage = plugin + message;
      Canary.instance().getServer().broadcastMessage(serverMessage);
  }
}
