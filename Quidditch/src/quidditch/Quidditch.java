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
import java.util.HashMap;
import java.util.List;
import net.canarymod.api.scoreboard.*;
import net.canarymod.hook.player.TeleportHook;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.hook.system.ServerShutdownHook;
import net.canarymod.hook.entity.DamageHook;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.hook.player.ItemDropHook;
import net.canarymod.hook.player.SlotClickHook;
import net.canarymod.api.inventory.slot.ButtonPress;
import net.canarymod.api.entity.EntityType;
import utils.DatabaseUtils;
import utils.GivePlayerItemTask;

public class Quidditch extends EZPlugin implements PluginListener {
  
  private final String pluginName = "[Quidditch]";
  private final BlockType SNITCH_BLOCK_TYPE = BlockType.GoldBlock;
  private final int SNITCHES_PER_GAME = 10;
  private final int POINTS_PER_RIGHTCLICK = 150;
  private final int BASE_POINTS_PER_ARROW_HIT = 50;
  private final int POINTS_FOR_MISSED_ARROW = -5;
  private final int POINTS_FOR_FAST_CATCH = 20;
  private final int TIME_FOR_FAST_CATCH_IN_SECONDS = 8;
  private final int FAST_CATCHES_IN_ROW_FOR_BONUS = 3;
  private final int POINTS_FOR_FAST_CATCH_STREAK = 30;
  private final double MAX_HIT_DISTANCE = 3.5;
  private final int SPEED_FOR_FAST_CATCH_IN_SECONDS = 3;
  private final int SPEED_FOR_FAST_CATCH_STREAK_IN_SECONDS = 5;
  private final int GIVE_COMPASS_DELAY_IN_SECONDS = 30;

  private boolean isEnabled = false;
  private Location snitchLocation;
  private Player player;
  private int currentSnitchCount;
  private int rightClickCatches;
  private int score;
  private Map SELECTED_MAP;
  private DatabaseUtils database;

  //TODO: Stats erweitern (most played map, compass getted) und stats for each map, check if highscore has been achieved -> tell player
  //Ideen:partikel für schnatz nach zeit/PartikelEffektItem einmal benutzen für Vorteil, schnatz bewegt sich?
  //DELETE DB and look what throws an exception e.g. only one line to write on highscore sign
  //Achievements -> block spawned in der nähe, keinen pfeil verschossen, nur hand catches, ...

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }
  
  @Command(aliases = { "quidditch" },
            description = "quidditch plugin",
            permissions = { "*" },
            toolTip = "/quidditch")
  public void quidditchschnatzCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player) {
      if(args.length == 1)
        player.teleportTo(Utils.quidditchHubLocation);
      else if(args.length == 2 && args[1].equalsIgnoreCase("stats")){
        displayPlayerStats(player);
      }
    }
  }

  private void startGame(Player player){
    setStartVariables();
    this.player = player;
    this.database = setUpDatabase();
    hasStartedGame = false;
    player.setModeId(Utils.ADVENTURE_MODE);
    displayStartMessage();
    placeSnitch();
    giveEquipToPlayer();
    createScoreboard();
    Utils.RefreshInventroyFromPlayer(player);
    isEnabled = true;
  }

  private void setStartVariables(){
    currentSnitchCount = 1;
    score = 0;
    rightClickCatches = 0;
    missedArrowCount = 0;
    lastCatchTimeInSeconds = 0;
    fastCatchStreak = 0;
    totalFastCatches = 0;
    totalFastCatchStreaks = 0;
    fastestCatch = Integer.MAX_VALUE;
    slowestCatch = 0;
    longestBowHit = 0;
    shortestBowHit = Integer.MAX_VALUE;
  }

  private final String DB_FOLDER = "plugins/Quidditch";
  private final String DB_FILE = "quidditch.db";

  private DatabaseUtils setUpDatabase(){
    DatabaseUtils newDb = new DatabaseUtils(DB_FOLDER, DB_FILE);
    newDb.InitDatabase();
    return newDb;
  }

  private Scoreboard scoreboard;
  private ScoreObjective objective;
  private Score mapScore;
  private Score countScore;
  private Score totalScore;
  private Score rightClickScore;
  private Score timeScore;
  private ScoreboardTimerTask timerTask;

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

    this.rightClickScore = scoreboard.getScore("§cHandCatches: §f" + rightClickCatches, this.objective);
    this.rightClickScore.setScore(3);
    this.rightClickScore.update();

    this.countScore = scoreboard.getScore("§bCatches: §f" + (currentSnitchCount - 1) + "/" + SNITCHES_PER_GAME, this.objective);
    this.countScore.setScore(4);
    this.countScore.update();

    this.totalScore = scoreboard.getScore("§dScore: §e" + score, this.objective);
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
      Location startLocation = SELECTED_MAP.GetStartLocation();
      Location endLocation = SELECTED_MAP.GetEndLocation();
      Location randomLocation = Utils.GetRandomLocationInsideVolume(startLocation, endLocation);
      Block possibleSnitch = randomLocation.getWorld().getBlockAt((int)randomLocation.getX(), (int)randomLocation.getY(), (int)randomLocation.getZ());
      
      if(possibleSnitch.getType() == BlockType.Air){
        snitchLocation = randomLocation;
        snitchLocation.getWorld().setBlockAt(snitchLocation, SNITCH_BLOCK_TYPE);
        hasAirBlockBeenSelected = true;
        givePlayerDelayedCompass();
      }
    }
  }
  
  private GivePlayerItemTask givePlayerItemTask;

  private void givePlayerDelayedCompass(){
    ItemFactory itemFactory = Canary.factory().getItemFactory();
    Item compass = itemFactory.newItem(ItemType.Compass);
    player.setCompassTarget((int)snitchLocation.getX(), (int)snitchLocation.getY(), (int)snitchLocation.getZ());
    givePlayerItemTask = new GivePlayerItemTask(player, compass, 0, GIVE_COMPASS_DELAY_IN_SECONDS);
    Canary.getServer().addSynchronousTask(givePlayerItemTask);
  }

  private boolean hasStartedGame = false;
  private final Location QuidditchMapSignLocation = new Location(252, 54, 266);
  private final Location SnowMapSignLocation = new Location(252, 54, 267);
  private final Location NetherMapSignLocation = new Location(252, 54, 268);
  private final Location ShriekingShackMapSignLocation = new Location(252, 54, 269);
  private final Location ChristmasMapSignLocation = new Location(252, 54, 270);
  private final Location RandomMapSignLocation = new Location(252, 54, 265);

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    Block clickedBlock = event.getBlockClicked();
    Location clickedLocation = clickedBlock.getLocation();
    Player playerClicked = event.getPlayer();
    BlockType clickedType = clickedBlock.getType();

    if(clickedType == BlockType.WallSign){
      //early return to avoid multiple starts
      if(hasStartedGame){
        Utils.BroadcastServerMessage(pluginName, ChatFormat.RED + "Du hast bereits ein Spiel gestartet!");
        return;
      }

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
      else if(EZPlugin.locEqual(clickedLocation, RandomMapSignLocation))
        SELECTED_MAP = Map.GetRandomMap();
      else
        return;
      
      hasStartedGame = true;
      this.player = playerClicked;
      Canary.getServer().addSynchronousTask(new TeleportPlayerTask(playerClicked, SELECTED_MAP.GetRandomSpawnPosition(), 3));
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
      if(arrow.getEntityType() == EntityType.ARROW){
        Location arrowLocation = arrow.getLocation();

        //arrow.getLocation() is very inaccurate! -> try to improve (idea: change block to redstone and use RedstoneChangeHookEvent)
        double arrowSnitchDistance = Utils.CalculateDistanceBetweenLocations(snitchLocation, arrowLocation);
        if(arrowSnitchDistance <= MAX_HIT_DISTANCE)
          snitchCatched(calculateBowPoints(), SoundEffect.Type.ORB);
        else
          decreaseScoreForMissedArrow();

        arrow.destroy();
      }
    }
  }

  private int shortestBowHit;
  private int longestBowHit;

  private int calculateBowPoints(){
    double playerSnitchDistance = Utils.CalculateDistanceBetweenLocations(playerBowStartLocation, snitchLocation);
    setShortestLongestBowHit(playerSnitchDistance);
    int roundedPoints = (int)Math.round(playerSnitchDistance / 10);
    int points = BASE_POINTS_PER_ARROW_HIT + (int)playerSnitchDistance;
    return points;
  }

  private void setShortestLongestBowHit(double playerSnitchDistance){
    if(playerSnitchDistance > longestBowHit)
      longestBowHit = (int)playerSnitchDistance;
    
    if(playerSnitchDistance < shortestBowHit)
      shortestBowHit = (int)playerSnitchDistance;
  }

  private int missedArrowCount;
  private void decreaseScoreForMissedArrow(){
    score += POINTS_FOR_MISSED_ARROW;
    displayScoreMessage(POINTS_FOR_MISSED_ARROW, false);
    missedArrowCount++;
  }

  
  private int fastCatchStreak;

  private void snitchCatched(int pointsScored, SoundEffect.Type soundType){
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
    removeCompassFromPlayer();
    currentSnitchCount++;
    boolean isFastCatch = handleFastCatches();

    if(isFastCatch)
      pointsScored += POINTS_FOR_FAST_CATCH;

    score += pointsScored;
    displayScoreMessage(pointsScored, isFastCatch);
    
    if(fastCatchStreak == FAST_CATCHES_IN_ROW_FOR_BONUS)
      handleFastCatchStreak();

    if(currentSnitchCount <= SNITCHES_PER_GAME){
      Utils.playSoundAtLocation(player.getLocation(), soundType, 1.0f, 3.0f);
      placeSnitch();
    }
    else{
      initializeWin();
    }
  }

  private void removeCompassFromPlayer(){
    Canary.getServer().removeSynchronousTask(givePlayerItemTask);
    player.getInventory().removeItem(ItemType.Compass);
    Utils.RefreshInventroyFromPlayer(player);
  }

  private int lastCatchTimeInSeconds;
  private int totalFastCatches;
  private int fastestCatch;
  private int slowestCatch;

  private boolean handleFastCatches(){
    int gameDuration = timerTask.getElapsedTimeInSeconds();
    int catchTimeInSeconds = gameDuration - lastCatchTimeInSeconds;
    if(catchTimeInSeconds < fastestCatch)
      fastestCatch = catchTimeInSeconds;
    
    if(catchTimeInSeconds > slowestCatch)
      slowestCatch = catchTimeInSeconds;
    
    lastCatchTimeInSeconds = gameDuration;
    boolean isFastCatch = catchTimeInSeconds < TIME_FOR_FAST_CATCH_IN_SECONDS;
    if(isFastCatch){
      fastCatchStreak++;
      totalFastCatches++;
      Utils.GivePlayerSpeedEffect(player, SPEED_FOR_FAST_CATCH_IN_SECONDS, 0);
    }
    else
      fastCatchStreak = 0;
    
    return isFastCatch;
  }

  private int totalFastCatchStreaks;

  private void handleFastCatchStreak(){
    totalFastCatchStreaks++;
    fastCatchStreak = 0;
    score += POINTS_FOR_FAST_CATCH_STREAK; 
    Utils.BroadcastServerMessage(pluginName, ChatFormat.YELLOW + "Blitzfang streak!");
    Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.WITHER_SHOOT, 1.0f, 1.0f);
    displayScoreMessage(POINTS_FOR_FAST_CATCH_STREAK, false);
    Utils.GivePlayerSpeedEffect(player, SPEED_FOR_FAST_CATCH_STREAK_IN_SECONDS, 1);
  }

  private void initializeWin(){
    Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.LEVEL_UP, 3.0f, 1.0f);
    displayWinMessage();
    insertGameSessionIntoDb();
    updateHighScoreSigns();
    cleanUpAfterGame();
  }

  private void insertGameSessionIntoDb(){
    String playerName = player.getDisplayName();
    int bowHits = SNITCHES_PER_GAME - rightClickCatches;
    database.insertGameSession(playerName, score, rightClickCatches, bowHits, 
      fastestCatch, slowestCatch, totalFastCatches, totalFastCatchStreaks, 
      missedArrowCount, SELECTED_MAP.toString(), timerTask.getElapsedTimeInSeconds(), 
      shortestBowHit, longestBowHit);
  }

  private void updateHighScoreSigns(){
    updateHighScores();
    updateHighScoreTimes();
    updateFastestCatchTimes();
    updateSlowestCatchTimes();
    updateMostPlayedMaps();
    updateShortestAndLongestBowHit();
  }

  private void updateHighScores(){
    List<String> top3Scores = database.GetTop3ScoresFromMap(SELECTED_MAP);
    writeTextToSign(SELECTED_MAP.toString(), top3Scores, SELECTED_MAP.GetQuidditchPluginHighScoreSign(), true);
  }

  private void updateHighScoreTimes(){
    List<String> top3Scores = database.GetTop3TimesFromMap(SELECTED_MAP);
    writeTextToSign(SELECTED_MAP.toString(), top3Scores, SELECTED_MAP.GetQuidditchPluginHighScoreTimeSign(), true);
  }

  private void updateFastestCatchTimes(){
    List<String> top3Scores = database.GetTop3FastestCatchTimes();
    Location fastestCatchHighscoreSign = new Location(248, 55, 272);
    writeTextToSign("Fastest Catch", top3Scores, fastestCatchHighscoreSign, true);
  }

  private void updateSlowestCatchTimes(){
    List<String> top3Scores = database.GetTop3SlowestCatchTimes();
    Location slowestCatchHighscoreSign = new Location(248, 54, 272);
    writeTextToSign("Slowest Catch", top3Scores, slowestCatchHighscoreSign, true);
  }

  private void updateMostPlayedMaps(){
    List<String> top3MapsPlayed = database.GetTop3MapsPlayed();
    Location slowestCatchHighscoreSign = new Location(248, 55, 264);
    writeTextToSign("Most played Maps", top3MapsPlayed, slowestCatchHighscoreSign, true);
  }

  private void updateShortestAndLongestBowHit(){
    List<String> shortestAndLongestBowHit = database.GetShortestAndLongestBowHit();
    shortestAndLongestBowHit.add(1, "Longest Bow Hit");
    Location shortestAndLongestHighscoreSign = new Location(248, 54, 264);
    writeTextToSign("Shortest Bow Hit", shortestAndLongestBowHit, shortestAndLongestHighscoreSign, false);
  }

  private void writeTextToSign(String headline, List<String> top3Scores, Location signLocation, boolean hasPosition){
    if(top3Scores != null){
      String[] signText = new String[4];
      signText[0] = headline;
      for(int i = 1; i <= top3Scores.size(); i++){
        signText[i] = top3Scores.get(i-1);
        if(hasPosition)
          signText[i] = i + ". " + signText[i];
      }
    
      Utils.UpdateSignText(signLocation, signText);
    }
    else
      logger.info(pluginName + " ERROR while reading TOP3 Times");
  }

  private Location playerBowStartLocation;

  @HookHandler
  public void ItemUseHookEvent(ItemUseHook event){
    if(isEnabled && event.getPlayer() == player){
      Item usedItem = event.getItem();
      ItemType itemType = usedItem.getType();

      if(itemType == ItemType.Bow)
        playerBowStartLocation = player.getLocation();
    }
  }

  private void displayStartMessage(){
    String msg2 = "Versuche jeden ";
    String msg3 = "goldenen Schnatz";
    String msg4 = "zu fangen.";
    String serverMessage = msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayScoreMessage(int pointsScored, boolean isFastCatch){
    updateScoreboard();

    String sign = ChatFormat.GOLD + "+";
    if(pointsScored < 0)
      sign = ChatFormat.RED + "";

    String serverMessage = sign + pointsScored + ChatFormat.DARK_GREEN + " Punkte.";
    if(isFastCatch)
      serverMessage = ChatFormat.YELLOW + "Blitzfang! " + serverMessage; 
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void updateScoreboard(){
    scoreboard.removeScore(this.rightClickScore.getName(), this.objective);
    this.rightClickScore = scoreboard.getScore("§cHandCatches: §f" + rightClickCatches, this.objective);
    this.rightClickScore.setScore(3);
    this.rightClickScore.update();

    scoreboard.removeScore(this.countScore.getName(), this.objective);
    this.countScore = scoreboard.getScore("§bCatches: §f" + (currentSnitchCount - 1) + "/" + SNITCHES_PER_GAME, this.objective);
    this.countScore.setScore(4);
    this.countScore.update();

    scoreboard.removeScore(this.totalScore.getName(), this.objective);
    this.totalScore = scoreboard.getScore("§dScore: §e" + score, this.objective);
    this.totalScore.setScore(5);
    this.totalScore.update();
  }

  private void displayWinMessage(){
    String msg2 = "Du hast jeden Schnatz ";
    String msg3 = ChatFormat.GOLD + "gefangen";

    String msg4 = "\nPunkte: " + ChatFormat.GOLD + score + ChatFormat.DARK_GREEN;
    String msg5 = "\nZeit: " + ChatFormat.GOLD + timerTask.getFormatedElapsedTime() + ChatFormat.DARK_GREEN + " Minuten";
    String msg6 = "\nFehlschüsse: " + ChatFormat.RED + missedArrowCount;

    String serverMessage =  msg2 + msg3 + ChatFormat.DARK_GREEN + "!" + msg4 + msg5 + msg6;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    Canary.getServer().addSynchronousTask(new TeleportPlayerTask(player, Utils.quidditchHubLocation, 5));
  }

  private void cleanUpAfterGame(){
    removeItemsFromPlayer();
    Utils.clearScoreboard(scoreboard, timerTask, objective);
    isEnabled = false;
    isFirstStartPort = true;
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
    database.CloseConnection();
  }

  private void removeItemsFromPlayer(){
    PlayerInventory playerInventory = player.getInventory();
    playerInventory.removeItem(ItemType.Bow);
    playerInventory.removeItem(ItemType.Arrow);
    removeCompassFromPlayer();
  }

  @HookHandler
  public void DamageHookEvent(DamageHook event){
    if(isEnabled && event.getDefender() instanceof Player player){
      if(event.getDamageDealt() >= player.getHealth()){
        player.setFireTicks(0);
        player.setHealth(20f);
        event.setCanceled();
        //the teleport hook cleans up the rest 
        player.teleportTo(Utils.quidditchHubLocation);
      }
    }
  }

  private boolean isFirstStartPort = true;

  @HookHandler
  public void TeleportHookEvent(TeleportHook event){
    Player teleportedPlayer = event.getPlayer();
    if(teleportedPlayer == player && SELECTED_MAP.IsLocationInsideMap(event.getDestination()) && isFirstStartPort){
      isFirstStartPort = false;
      startGame(player);
    }
    else if(isEnabled && event.getPlayer() == player && !SELECTED_MAP.IsLocationInsideMap(event.getDestination())){
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

  @HookHandler
  public void ItemDropHook(ItemDropHook event){
    if(isEnabled && event.getPlayer() == player){
      event.setCanceled();
      PlayerInventory playerInventory = player.getInventory();
      int hotbarSlotId = playerInventory.getSelectedHotbarSlotId();
      playerInventory.setSlot(hotbarSlotId, event.getItem().getItem());
      Utils.RefreshInventroyFromPlayer(player);
    }
  }

  @HookHandler
  public void SlotClickHook(SlotClickHook event){
    if(isEnabled && event.getPlayer() == player){
      ButtonPress buttonPress = event.getButtonPress();
      //allows only to rearrange hotbar
      if(event.getSlotId() < 36)
        event.setCanceled();
      else if(buttonPress == ButtonPress.KEY_DROP || buttonPress == ButtonPress.CTRL_DROP)
        event.setCanceled();
    }
  }

  private void displayPlayerStats(Player player){
    DatabaseUtils statsDb = setUpDatabase();

    String serverMessage = "Das sind die Stats von " + ChatFormat.BLUE + player.getDisplayName() + ChatFormat.DARK_GREEN + ":\n";
    HashMap<String, String> stats = statsDb.GetPlayerStatsForQuidditch(player.getDisplayName());
    for(String key : stats.keySet())
      serverMessage += ChatFormat.DARK_GREEN + " - " + key + ": " + ChatFormat.GOLD + stats.get(key) + "\n";
      
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    statsDb.CloseConnection();
  }
}