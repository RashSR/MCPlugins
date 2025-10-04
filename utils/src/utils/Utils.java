package utils;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.blocks.Block;

public class Utils {
  
  public static int SURVIVAL_MODE = 0;
  public static int CREATIVE_MODE = 1;
  public static int ADVENTURE_MODE = 2;
  public static int SPECTATOR_MODE = 3;

  public static final int TICKS_PER_SECOND = 20;

  public static final String EventFileLocation = "C:/Users/R/Desktop/server/config/events.txt";

  public static Location HubLocation = new Location(251, 71, 262);
  public static Location ZombieLocation = new Location(256, 71, 546);
  public static Location Location1vs1 = new Location(107, 151, 309);
  public static Location BuildItLocation = new Location(93, 79, 327);
  public static Location CityLocation = new Location(4928, 64, 4899);
  public static Location QuidditchFieldLocation = new Location(163, 138, 309);
  public static Location NetherMapLocation = new Location(163, 149, 364);
  public static Location ChristmasMapLocation = new Location(207, 135, 309);
  public static Location ShriekingShackLocation = new Location(145, 157, 276);
  public static Location PressurePlate1vs1SoupKitLocation = new Location(107, 151, 303);
  public static Location PressurePlateHubTo1vs1Location = new Location(243, 71, 266);

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

  public static void BroadcastServerMessage(String pluginName, String message){
      String plugin = ChatFormat.DARK_AQUA + pluginName;
      String serverMessage = plugin + " " + message;
      Canary.instance().getServer().broadcastMessage(serverMessage);
  }

  public static void BroadcastWrongArgumentLengthMessage(String pluginName){
    String serverMessage = ChatFormat.DARK_GREEN + "Falsche Anzahl an Argumenten!";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public static int CalculateBlockCountInVolume(Block startBlock, Block endBlock){
    int xmin = Math.min(startBlock.getX(), endBlock.getX());
    int xmax = Math.max(startBlock.getX(), endBlock.getX());
    int ymin = Math.min(startBlock.getY(), endBlock.getY());
    int ymax = Math.max(startBlock.getY(), endBlock.getY());
    int zmin = Math.min(startBlock.getZ(), endBlock.getZ());
    int zmax = Math.max(startBlock.getZ(), endBlock.getZ());

    int totalBlocks = 0;
    for(int x = xmin; x <= xmax; x++){
      for(int y = ymin; y <= ymax; y++){
        for(int z = zmin; z <= zmax; z++){
          totalBlocks = totalBlocks + 1;
          Location loc = new Location(x, y, z);
        }
      }
    }

    return totalBlocks;
  }

  public static void ClearPlayerInventory(Player player){
    player.getInventory().clearInventory();
    removeArmorFromInventory(player);
  }

  private static void removeArmorFromInventory(Player player){
    removeItemIfNotNull(player, player.getInventory().getBootsSlot());
    removeItemIfNotNull(player, player.getInventory().getLeggingsSlot());
    removeItemIfNotNull(player, player.getInventory().getChestplateSlot());
    removeItemIfNotNull(player, player.getInventory().getHelmetSlot());
  }

  private static void removeItemIfNotNull(Player player, Item item){
    if(item != null)
      player.getInventory().removeItem(item);
  }
}