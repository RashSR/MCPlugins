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
import net.canarymod.api.world.effects.SoundEffect;
import utils.Utils;
import utils.Map;
import utils.ScoreboardTimerTask;
import utils.TeleportPlayerTask;
import java.util.List;
import net.canarymod.api.scoreboard.*;
import net.canarymod.hook.player.TeleportHook;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.hook.entity.DamageHook;

public class Quidditch extends EZPlugin implements PluginListener {
  
  private final String pluginName = "[Quidditch]";
  private final BlockType SNITCH_BLOCK_TYPE = BlockType.GoldBlock;
  private final int SNITCHES_PER_GAME = 2;
  private final int POINTS_PER_RIGHTCLICK = 150;
  private final int POINT_PER_ARROW_HIT = 50;
  private final double MAX_HIT_DISTANCE = 3.25;
  private final static Location QuidditchMapSignLocation = new Location(252, 54, 266);
  private final static Location SnowMapSignLocation = new Location(252, 54, 267);
  private final static Location NetherMapSignLocation = new Location(252, 54, 268);
  private final static Location ShriekingShackMapSignLocation = new Location(252, 54, 269);
  private final static Location ChristmasMapSignLocation = new Location(252, 54, 270);
  private boolean isEnabled = false;
  private Location snitchLocation;
  private Player player;
  private int currentSnitchCount;
  private int rightClickCatches;
  private int score;
  private Map SELECTED_MAP = Map.SNOW;

  private Scoreboard scoreboard;
  private ScoreObjective objective;
  private Score mapScore;
  private Score countScore;
  private Score totalScore;
  private Score rightClickScore;
  private Score timeScore;
  private ScoreboardTimerTask timerTask;

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
      if(args.length == 1){
        player.teleportTo(Utils.quidditchHubLocation);
      }
    }
  }

  private void startGame(Player player){
    //TODO: add start sound
    player.teleportTo(SELECTED_MAP.getSpawnLocation());
    currentSnitchCount = 1;
    score = 0;
    rightClickCatches = 0;
    displayStartMessage();
    placeSnitch();
    this.player = player;
    giveEquipToPlayer();
    player.setModeId(Utils.ADVENTURE_MODE);
    createScoreboard();
    Utils.RefreshInventroyFromPlayer(player);
    isEnabled = true;
  }

  private void createScoreboard(){
    ScoreboardManager manager = Canary.scoreboards();

    // Gets or create the scoreboard
    this.scoreboard = manager.getScoreboard("gameboard");

    // Create or get the objective
    this.objective = scoreboard.addScoreObjective("catchSnitch");

    // Set display name and position
    this.objective.setDisplayName("§6§lGame Info");

    //Set Position and the player it is targetted to
    this.scoreboard.setScoreboardPosition(ScorePosition.SIDEBAR, this.objective, player);

    // Initialize score entries
    this.mapScore = scoreboard.getScore("§aMap: §f" + SELECTED_MAP.toString(), this.objective);
    this.mapScore.setScore(1);
    this.mapScore.update();

    this.timerTask = new ScoreboardTimerTask(this.scoreboard, this.objective, this.timeScore, 2);
    Canary.getServer().addSynchronousTask(timerTask);

    this.rightClickScore = scoreboard.getScore("§bHandCatches: §4" + rightClickCatches, this.objective);
    this.rightClickScore.setScore(3);
    this.rightClickScore.update();

    this.countScore = scoreboard.getScore("§bCatches: " + (currentSnitchCount - 1), this.objective);
    this.countScore.setScore(4);
    this.countScore.update();

    this.totalScore = scoreboard.getScore("§eScore: " + score, this.objective);
    this.totalScore.setScore(5);
    this.totalScore.update();

    //Can be used to show it for the first session (check for file) or restart server. 
    //Requirement -> manager.getScoreboard(); must have no argument, this will use the default scoreboard
    //Canary.getServer().consoleCommand("scoreboard objectives setdisplay sidebar scoreboard_catchSnitch");
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
    Block clickedBlock = event.getBlockClicked();
    Location clickedLocation = clickedBlock.getLocation();
    Player playerClicked = event.getPlayer();
    BlockType clickedType = clickedBlock.getType();

    if(clickedType == BlockType.WallSign){
      if(EZPlugin.locEqual(clickedLocation, QuidditchMapSignLocation))
        SELECTED_MAP = Map.QUIDDITCH;
      else if(EZPlugin.locEqual(clickedLocation, NetherMapSignLocation))
        SELECTED_MAP = Map.NETHER;
      else if(EZPlugin.locEqual(clickedLocation, SnowMapSignLocation))
        SELECTED_MAP = Map.SNOW;
      else if(EZPlugin.locEqual(clickedLocation, ShriekingShackMapSignLocation))
        SELECTED_MAP = Map.SHRIEKING_SHACK;
      else if(EZPlugin.locEqual(clickedLocation, ChristmasMapSignLocation))
        SELECTED_MAP = Map.CHRISTMAS;
      else
        return;
      
      startGame(playerClicked);
    }
    else if(isEnabled && this.player == playerClicked && clickedType == SNITCH_BLOCK_TYPE && EZPlugin.locEqual(clickedLocation, snitchLocation)){
      rightClickCatches++;
      snitchCatched(POINTS_PER_RIGHTCLICK, SoundEffect.Type.NOTE_PLING);
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
                snitchCatched(POINT_PER_ARROW_HIT, SoundEffect.Type.ORB);
                return;
              }
            }
          }
        }
      }
    }
  }

  private void snitchCatched(int pointsScored, SoundEffect.Type soundType){
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
    currentSnitchCount++;
    score += pointsScored;
    displayScoreMessage(pointsScored);

    if(currentSnitchCount <= SNITCHES_PER_GAME){
      Utils.playSoundAtLocation(player.getLocation(), soundType, 1.0f, 3.0f);
      placeSnitch();
    }
    else{
      Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.LEVEL_UP, 3.0f, 1.0f);
      displayWinnerMessage();
      cleanUpAfterGame();
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
    updateScoreboard();
    
    String msg2 = "Das war Nummer ";
    String msg3 = "/" + SNITCHES_PER_GAME + ". ";
    String msg4 ="+" + scoredPoints;
    String msg5 = " Punkte.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + (currentSnitchCount - 1) + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + msg5;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void updateScoreboard(){
    scoreboard.removeScore(this.rightClickScore.getName(), this.objective);
    this.rightClickScore = scoreboard.getScore("§bHandCatches: §4" + rightClickCatches, this.objective);
    this.rightClickScore.setScore(3);
    this.rightClickScore.update();

    scoreboard.removeScore(this.countScore.getName(), this.objective);
    this.countScore = scoreboard.getScore("§bCatches: " + (currentSnitchCount - 1), this.objective);
    this.countScore.setScore(4);
    this.countScore.update();

    scoreboard.removeScore(this.totalScore.getName(), this.objective);
    this.totalScore = scoreboard.getScore("§eScore: " + score, this.objective);
    this.totalScore.setScore(5);
    this.totalScore.update();
  }

  private void displayWinnerMessage(){
    String msg2 = "Du hast jeden Schnatz ";
    String msg3 = "gefangen ";
    String msg4 = " Punkte geholt in ";
    String msg5 = ChatFormat.GOLD + timerTask.getElapsedTime() + ChatFormat.DARK_GREEN + " Minuten.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + "und " + ChatFormat.GOLD + score + ChatFormat.DARK_GREEN + msg4 + msg5;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    Canary.getServer().addSynchronousTask(new TeleportPlayerTask(player, Utils.quidditchHubLocation, 5));
  }

  private void cleanUpAfterGame(){
    removeItemsFromPlayer();
    Utils.clearScoreboard(scoreboard, timerTask, objective);
    isEnabled = false;
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
  }

  private void removeItemsFromPlayer(){
    PlayerInventory playerInventory = player.getInventory();
    playerInventory.removeItem(ItemType.Bow);
    playerInventory.removeItem(ItemType.Arrow);
    Utils.RefreshInventroyFromPlayer(player);
  }

  @HookHandler
  public void DamageHookEvent(DamageHook event){
    if(isEnabled && event.getDefender() instanceof Player player){
      if(event.getDamageDealt() >= player.getHealth()){
        player.setFireTicks(0);
        player.setHealth(20f);
        event.setCanceled();
        //the teleport hook is doing the rest of it cleans up the rest 
        player.teleportTo(Utils.quidditchHubLocation);
      }
    }
  }

  @HookHandler
  public void TeleportHookEvent(TeleportHook event){
    if(isEnabled && event.getPlayer() == player && !EZPlugin.locEqual(event.getDestination(), SELECTED_MAP.getSpawnLocation())){
      cleanUpAfterGame();
      displayLoseMessage();
    }
  }

  @HookHandler
  public void DisconnectionHookEvent(DisconnectionHook event){
    if(isEnabled && event.getPlayer() == player){
      cleanUpAfterGame();
      displayLoseMessage();
    }
  }

  @HookHandler
  public void ServerShutdownHook(ServerShutdownHook event){
    if(isEnabled){
      cleanUpAfterGame();
      displayLoseMessage();
    }
  }

  private void displayLoseMessage(){
		String msg2 = player.getDisplayName();
		String msg3 = " hat ";
		String msg4 = "aufgegeben";

		String serverMessage = ChatFormat.BLUE + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".";
		Utils.BroadcastServerMessage(pluginName, serverMessage);
	}
}