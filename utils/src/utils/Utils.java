package utils;
import net.canarymod.api.entity.living.humanoid.Player;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.scoreboard.*;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.blocks.Sign;
import net.canarymod.api.world.World;
import net.canarymod.api.factory.ChatComponentFactory;
import net.canarymod.api.chat.ChatComponent;

public class Utils extends EZPlugin{
  
  public static int SURVIVAL_MODE = 0;
  public static int CREATIVE_MODE = 1;
  public static int ADVENTURE_MODE = 2;
  public static int SPECTATOR_MODE = 3;

  public static final int TICKS_PER_SECOND = 20;

  public static final String EventFileLocation = "C:/Users/R/Desktop/server/config/events.txt";

  public static Location HubLocation = new Location(251, 71, 262);
  public static Location ZombieLocation = new Location(256, 71, 546);
  public static Location quidditchHubLocation = new Location(HubLocation.getWorld(), 246.5, 53, 268.5, 0f, -90f);
  public static Location Location1vs1 = new Location(107, 151, 309);
  public static Location BuildItLocation = new Location(93, 79, 327);
  public static Location CityLocation = new Location(4928, 64, 4899);
  public static Location QuidditchFieldLocation = new Location(163, 130, 309);
  public static Location NetherMapLocation = new Location(163, 149, 364);
  public static Location ChristmasMapLocation = new Location(207, 135, 309);
  public static Location ShriekingShackLocation = new Location(145, 157, 276);
  public static Location SnowMapLocation = new Location(29, 108, 232);
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

  public static int GetYear() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy");
    Date date = new Date();
    return Integer.parseInt(dateFormat.format(date)); 
  }

	public static int getMonth(){
    DateFormat dateFormat = new SimpleDateFormat("MM");
    Date date = new Date();
		return Integer.parseInt(dateFormat.format(date));
	}

	public static int getDay(){
    DateFormat dateFormat = new SimpleDateFormat("dd");
    Date date = new Date();
    return Integer.parseInt(dateFormat.format(date));
	}

  public static void WriteToEventFile(String text){
    File file = new File(EventFileLocation);
    try{
      if (file.createNewFile()){
        logger.info("[FileLoader] File wurde erstellt!");
      }else {
        logger.info("[FileWriter] File bereits vorhanden!");
      }

      FileWriter fw = new FileWriter(EventFileLocation);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(text);
      bw.close();
    }catch(Exception e){
      logger.info("[FileWriter] Unhandled IO-Exception.");
    }
  }

  public static void RefreshInventroyFromPlayer(Player player){
    //Is needed to update the Inventory
    Canary.getServer().consoleCommand("clear " + player.getDisplayName() + " minecraft:dirt 0 0");
  }

  public static double CalculateDistanceBetweenLocations(Location firstLocation, Location secondLocation, boolean withCenterCalculation) {
    double centerX = firstLocation.getX();
    double centerY = firstLocation.getY();
    double centerZ = firstLocation.getZ();

    if(withCenterCalculation){
      centerX += 0.5;
      centerY += 0.5;
      centerZ += 0.5;
    }

    double dx = centerX - secondLocation.getX();
    double dy = centerY - secondLocation.getY();
    double dz = centerZ - secondLocation.getZ();

    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  public static void clearScoreboard(Scoreboard scoreboard,  ScoreboardTimerTask timerTask, ScoreObjective objective){
    Canary.getServer().removeSynchronousTask(timerTask);

    //make it invisible
    scoreboard.clearScoreboardPosition(ScorePosition.SIDEBAR);

    List<Score> scores = scoreboard.getAllScores();
    for(Score s : scores){
      scoreboard.removeScore(s.getName(), objective);
    }
    scoreboard.removeScoreObjective(objective);
  }

  public static void playSoundAtLocation(Location loc, SoundEffect.Type soundType, float volume, float pitch){
    SoundEffect sound = new SoundEffect(soundType, loc.getX(), loc.getY(), loc.getZ(), volume, pitch);
    loc.getWorld().playSound(sound);
  }

  public static void setPlayerLevel(Player player, int targetLevel){
    if(targetLevel < 0)
      targetLevel = 0;
    
    player.removeExperience(player.getExperience());
    player.setLevel(0);

    int totalExp = getTotalExpForLevel(targetLevel);

    player.addExperience(totalExp);
	}

  private static int getTotalExpForLevel(int level){
    if(level <= 16)
      return level * level + 6 * level;
    else if(level <= 31)
      return (int)(2.5 * level * level - 40.5 * level + 360);

    return (int)(4.5 * level * level - 162.5 * level + 2220);
  }

  public static Location GetRandomLocationInsideVolume(Location startLocation, Location endLocation){
    double x = startLocation.getX() + Math.random() * (endLocation.getX() - startLocation.getX());
    double y = startLocation.getY() + Math.random() * (endLocation.getY() - startLocation.getY());
    double z = startLocation.getZ() + Math.random() * (endLocation.getZ() - startLocation.getZ());

    Location randomLocation = new Location((int)x, (int)y, (int)z);
    return randomLocation;
  }

  public static void UpdateSignText(Location loc, String[] text){
    World world = loc.getWorld();
    Block block = world.getBlockAt(loc);
    Sign sign = (Sign)block.getTileEntity();

    //Canary signs are very buggy in 1.8 -> use server commands
    for (int i = 1; i <= 4; i++) {
      updateSignLine(i, loc, text[i-1]);
    }
  }

  private static void updateSignLine(int index, Location loc, String text){ 
    //for some reason it does not work if in the string is a normal space -> replace with ASCII space character
    String safeText = text.replace(" ", "\\u0020");
    String jsonText = "{\"text\":\"" + safeText + "\"}";
    String command = "blockdata " + (int) loc.getX() + " "  + (int) loc.getY() + " " + (int) loc.getZ() + " "
        + "{Text" + index + ":\"" + jsonText.replace("\"", "\\\"") + "\"}";
    Canary.getServer().consoleCommand(command);
  }

}