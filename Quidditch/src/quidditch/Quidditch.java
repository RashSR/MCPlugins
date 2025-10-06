package quidditch;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.entity.ProjectileHitHook;
import net.canarymod.api.entity.Entity;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Enchantment;
import net.canarymod.api.inventory.Enchantment.Type;
import net.canarymod.api.inventory.PlayerInventory;
import utils.Utils;
import utils.Map;
import net.canarymod.api.scoreboard.*;

public class Quidditch extends EZPlugin implements PluginListener {
  
  private final String pluginName = "[Quidditch]";
  private final BlockType SNITCH_BLOCK_TYPE = BlockType.GoldBlock;
  private final int SNITCHES_PER_GAME = 10;
  private final int POINTS_PER_RIGHTCLICK = 150;
  private final int POINT_PER_ARROW_HIT = 50;
  private final double MAX_HIT_DISTANCE = 3.25;
  private boolean isEnabled = false;
  private Location snitchLocation;
  private Player player;
  private int currentSnitchCount;
  private int score;
  private Map SELECTED_MAP = Map.QUIDDITCH;

  private Scoreboard scoreboard;
  private ScoreObjective objective;
  private Score mapScore;
  private Score countScore;
  private Score totalScore;

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }
  
  @Command(aliases = { "quidditch" },
            description = "quidditch plugin",
            permissions = { "*" },
            toolTip = "/quidditch schnatz")
  public void quidditchschnatzCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player) {
      if(args.length == 2){
        if(args[1].equalsIgnoreCase("schnatz")){
            startGame(player);
        }
      }
    }
  }

  private void startGame(Player player){
    player.teleportTo(SELECTED_MAP.getSpawnLocation());
    currentSnitchCount = 1;
    score = 0;
    displayStartMessage();
    isEnabled = true;
    placeSnitch();
    this.player = player;
    giveEquipToPlayer();
    player.setModeId(Utils.ADVENTURE_MODE);
    createScoreboard();
  }

  private void createScoreboard(){
    ScoreboardManager manager = Canary.scoreboards();

    // Gets or create the scoreboard
    this.scoreboard = manager.getScoreboard("gameboard");

    // Create or get the objective
    this.objective = scoreboard.addScoreObjective("Quidditch");

    // Set display name and position
    this.objective.setDisplayName("§6§lGame Info");

    //Set Position and the player it is targetted to
    this.scoreboard.setScoreboardPosition(ScorePosition.SIDEBAR, this.objective, player);

    // Initialize score entries
    this.countScore = scoreboard.getScore("§bCount:", this.objective);
    this.countScore.setScore(0);
    this.countScore.update();

    this.totalScore = scoreboard.getScore("§eScore:", this.objective);
    this.totalScore.setScore(0);
    this.totalScore.update();

    String label = "§aMap: §f" + SELECTED_MAP.toString();
    this.mapScore = scoreboard.getScore(label, this.objective);
    this.mapScore.setScore(0);
    this.mapScore.update();
  }

  private void giveEquipToPlayer(){
    ItemFactory factory = Canary.factory().getItemFactory();
    Item infinityBow = factory.newItem(ItemType.Bow);
    infinityBow.setDisplayName(ChatFormat.GOLD + "Schnatzfaenger");

    short enchantmentLevel = 1;
    Enchantment infinity = factory.newEnchantment(Enchantment.Type.Infinity, enchantmentLevel);
    infinityBow.addEnchantments(infinity);

    PlayerInventory playerInventory = player.getInventory();
    playerInventory.setSlot(1, infinityBow);
    playerInventory.setSlot(ItemType.Arrow, 1, 8);
  }

  private void placeSnitch(){
    boolean hasAirBlockBeenSelected = false;

    while(!hasAirBlockBeenSelected){
      Location startLocation = SELECTED_MAP.getStartLocation();
      Location endLocation = SELECTED_MAP.getEndLocation();
      Location randomLocation = getRandomLocationInsideVolume(startLocation, endLocation);
      Block possibleSnitch = randomLocation.getWorld().getBlockAt((int)randomLocation.getX(), (int)randomLocation.getY(), (int)randomLocation.getZ());
      
      if(possibleSnitch.getType() == BlockType.Air){
        snitchLocation = randomLocation;
        snitchLocation.getWorld().setBlockAt(snitchLocation, SNITCH_BLOCK_TYPE);
        hasAirBlockBeenSelected = true;
      }
    }
  }

  private Location getRandomLocationInsideVolume(Location startLocation, Location endLocation){
    double x = startLocation.getX() + Math.random() * (endLocation.getX() - startLocation.getX());
    double y = startLocation.getY() + Math.random() * (endLocation.getY() - startLocation.getY());
    double z = startLocation.getZ() + Math.random() * (endLocation.getZ() - startLocation.getZ());

    Location randomLocation = new Location((int)x, (int)y, (int)z);
    return randomLocation;
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    if(isEnabled){
      Block clickedBlock = event.getBlockClicked();
      Player player = event.getPlayer();
      if(this.player == player && clickedBlock.getType() == SNITCH_BLOCK_TYPE && EZPlugin.locEqual(clickedBlock.getLocation(), snitchLocation)){
        snitchCatched(POINTS_PER_RIGHTCLICK);
      }
    } 
  }

  @HookHandler
  public void ProjectileHitHookEvent(ProjectileHitHook event){
    if(isEnabled){
      Entity arrow = event.getProjectile();
      World world = arrow.getWorld();
      Location arrowLocation = arrow.getLocation();
      arrow.destroy();

      int x = (int)arrowLocation.getX();
      int y = (int)arrowLocation.getY();
      int z = (int)arrowLocation.getZ();

      int zaehlx = x + 3;
      int zahely = y + 3;
      int zahelz = z + 3;

      for (int scanx = x - 3; scanx <= zaehlx ; scanx++) {
        for (int scany = y - 3; scany <= zahely ; scany++){
          for (int scanz = z - 3; scanz <= zahelz ; scanz++){
            Block hitBlock = world.getBlockAt(scanx, scany, scanz);

            if(hitBlock.getType() == SNITCH_BLOCK_TYPE && EZPlugin.locEqual(hitBlock.getLocation(), snitchLocation)){
              //arrow.getLocation() is very inaccurate!
              double distance = Utils.CalculateDistanceBetweenLocations(hitBlock.getLocation(), arrowLocation, true);
              Utils.BroadcastServerMessage(pluginName, "Distance: " + distance);
              if(distance <= MAX_HIT_DISTANCE){
                snitchCatched(POINT_PER_ARROW_HIT);
                return;
              }
            }
          }
        }
      }
    }
  }

  private void snitchCatched(int pointsScored){
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
    currentSnitchCount++;
    score += pointsScored;
    displayScoreMessage(pointsScored);

    if(currentSnitchCount <= SNITCHES_PER_GAME)
      placeSnitch();
    else{
      displayWinnerMessage();
      removeItemsFromPlayer();
      this.scoreboard.clearScoreboardPosition(ScorePosition.SIDEBAR);
    }
      
  }

  private void displayStartMessage(){
    String msg2 = "Versuche jeden ";
    String msg3 = "goldenen Schnatz";
    String msg4 = "zu fangen.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayScoreMessage(int scoredPoints){
    int totalCatchedSnitchCount = currentSnitchCount - 1;
    this.countScore.setScore(totalCatchedSnitchCount);
    this.countScore.update();
    this.totalScore.setScore(this.totalScore.getScore() + scoredPoints);
    this.totalScore.update();
    String msg2 = "Das war Nummer ";
    String msg3 = "/" + SNITCHES_PER_GAME + ". ";
    String msg4 ="+" + scoredPoints;
    String msg5 = " Punkte.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + totalCatchedSnitchCount + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + msg5;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayWinnerMessage(){
    String msg2 = "Du hast jeden Schnatz ";
    String msg3 = "gefangen ";
    String msg4 = " Punkte geholt.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + "und " + ChatFormat.GOLD + score + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    isEnabled = false;
  }

  private void removeItemsFromPlayer(){
    PlayerInventory playerInventory = player.getInventory();
    playerInventory.removeItem(ItemType.Bow);
    playerInventory.removeItem(ItemType.Arrow);
    Utils.RefreshInventroyFromPlayer(player);
  }
}