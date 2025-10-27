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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.canarymod.api.inventory.CustomStorageInventory;
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
import utils.ChangeBlockTypeTask;
import utils.DatabaseUtils;
import utils.GivePlayerItemTask;
import utils.IServerTaskCallback;
import utils.ServerEventType;
import utils.SpawnParticlesTask;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.DamageType;
import net.canarymod.hook.player.HealthChangeHook;
import net.canarymod.tasks.ServerTask;
import net.canarymod.hook.player.InventoryHook;
import net.canarymod.hook.player.PlayerMoveHook;

public class Quidditch extends EZPlugin implements PluginListener, IServerTaskCallback {
  
  private final String pluginName = "[Quidditch]";
  private final int SNITCHES_PER_GAME = 10;
  private final int POINTS_PER_RIGHTCLICK = 150;
  private final int BASE_POINTS_PER_ARROW_HIT = 50;
  private final int POINTS_FOR_MISSED_ARROW = -5;
  private final int POINTS_FOR_ADDED_COMPASS = -10;
  private final int POINTS_FOR_FAST_CATCH = 20;
  private final int TIME_FOR_FAST_CATCH_IN_SECONDS = 8;
  private final int FAST_CATCHES_IN_ROW_FOR_BONUS = 3;
  private final int POINTS_FOR_FAST_CATCH_STREAK = 30;
  private final double MAX_HIT_DISTANCE = 3.5;
  private final int SPEED_FOR_FAST_CATCH_IN_SECONDS = 3;
  private final int SPEED_FOR_FAST_CATCH_STREAK_IN_SECONDS = 5;
  private final int GIVE_COMPASS_DELAY_IN_SECONDS = 30;
  private final int SHOW_HELP_PARTICLE_AFTER_DELAY_IN_SECONDS = 45;
  private final int CHANGE_BLOCK_COLOR_IN_TICKS = 12;
  private final int GLITCH_EVENT_CHANCE_IN_PERCENT = 2;

  private BlockType SNITCH_BLOCK_TYPE;
  private boolean isEnabled = false;
  private Location snitchLocation;
  private Player player;
  private int currentSnitchCount;
  private int rightClickCatches;
  private int score;
  private Map SELECTED_MAP;
  private DatabaseUtils database;

  //TODO Click on e.g. /quidditch stats in the displayUsageMessage to execute it, load chunk to prevent wrong lighting, improve bow hit e.g. extrapolate direction
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
      else if(args.length == 2)
      {
        String secondArgument = args[1];
        if(secondArgument.equalsIgnoreCase("stats"))
          displayPlayerStats(player);
        else if(secondArgument.equalsIgnoreCase("usage"))
          displayUsageMessage();
        else if(secondArgument.equalsIgnoreCase("achievements"))
          displayAchievementInventory(player);
      }
      else if(args.length == 3 && args[1].equalsIgnoreCase("stats") && args[2].equalsIgnoreCase("map")){
        displayPlayerStatsEachMap(player);
      }
    }
  }

  private void startGame(Player player){
    setStartVariables();
    this.player = player;
    this.database = setUpDatabase();
    hasPlayerStartedGame = false;
    player.setModeId(Utils.ADVENTURE_MODE);
    displayStartMessage();
    checkForEvent();
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
    endTimeInSeconds = Integer.MAX_VALUE;
    totalCompassCount = 0;
    hasPlayerLostHealth = false;
    hasPlayerStandOnGround = true;
    isGlitchActive = false;
  }

  private ArrayList<Particle.Type> particleTypes;
  private ChangeBlockTypeTask changeBlockTypeTask;
  private List<BlockType> eventChangingBlocks;

  private void checkForEvent(){
    particleTypes = new ArrayList<>();

    switch (Utils.GetCurrentEvent()) {
      case ServerEventType.HALLOWEEN:
        SNITCH_BLOCK_TYPE = BlockType.JackOLantern;

        particleTypes.add(Particle.Type.SMOKE_NORMAL);
        particleTypes.add(Particle.Type.SMOKE_LARGE);
        particleTypes.add(Particle.Type.FLAME);

        tryEarnAchievement(player, AchievementType.PUMPKIN_SEASON, database);
        break;
      case ServerEventType.CHRISTMAS:
        SNITCH_BLOCK_TYPE = BlockType.LapisBlock;

        particleTypes.add(Particle.Type.SNOW_SHOVEL);
        particleTypes.add(Particle.Type.SNOWBALL);
        particleTypes.add(Particle.Type.REDSTONE);

        tryEarnAchievement(player, AchievementType.CHRISTMAS_SEASON, database);
        break;
      case ServerEventType.PRIDE:
        eventChangingBlocks = Utils.GetPrideBlockTypes();
        SNITCH_BLOCK_TYPE = BlockType.WoolMagenta;

        tryEarnAchievement(player, AchievementType.PRIDE_SEASON, database);
        break;
      case ServerEventType.NONE:
      default:
        particleTypes.add(Particle.Type.CRIT);
        particleTypes.add(Particle.Type.PORTAL);
        particleTypes.add(Particle.Type.VILLAGER_HAPPY);

        SNITCH_BLOCK_TYPE = BlockType.GoldBlock;
        break;
    } 
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
        handleChangingBlocks(possibleSnitch);

        hasAirBlockBeenSelected = true;

        if(Utils.CalculateDistanceBetweenLocations(snitchLocation, player.getLocation()) <= 5)
          tryEarnAchievement(player, AchievementType.LUCKY_SPAWN, database);

        givePlayerDelayedCompass();
        spawnDelayedParticles();
      }
    }
  }

  private boolean isGlitchActive;

  private void handleChangingBlocks(Block snitchBlock){
    if(isGlitchActive){
      eventChangingBlocks = null;
      isGlitchActive = false;
    }
      
    if(eventChangingBlocks != null && eventChangingBlocks.size() > 0){
      changeBlockTypeTask = new ChangeBlockTypeTask(CHANGE_BLOCK_COLOR_IN_TICKS, true, snitchBlock, eventChangingBlocks);
      Canary.getServer().addSynchronousTask(changeBlockTypeTask);
    }else{
      Random random = new Random();
      if(random.nextInt(100) < GLITCH_EVENT_CHANCE_IN_PERCENT){
        isGlitchActive = true;
        int randomDelayTicks = 2 + random.nextInt(6); //between  2..7

        eventChangingBlocks = new ArrayList<>();
        eventChangingBlocks.add(BlockType.EndStone);
        eventChangingBlocks.add(BlockType.Obsidian);
        eventChangingBlocks.add(BlockType.Melon);
        eventChangingBlocks.add(BlockType.GlowStone);

        changeBlockTypeTask = new ChangeBlockTypeTask(randomDelayTicks, true, snitchBlock, eventChangingBlocks);
        Canary.getServer().addSynchronousTask(changeBlockTypeTask);
        tryEarnAchievement(player, AchievementType.GLITCH, database);
      }
    }
  }

  private SpawnParticlesTask spawnParticlesTask;

  private void spawnDelayedParticles(){
    spawnParticlesTask = new SpawnParticlesTask(snitchLocation, particleTypes, SHOW_HELP_PARTICLE_AFTER_DELAY_IN_SECONDS, this);
    Canary.getServer().addSynchronousTask(spawnParticlesTask);
  }

  private GivePlayerItemTask givePlayerItemTask;

  private void givePlayerDelayedCompass(){
    ItemFactory itemFactory = Canary.factory().getItemFactory();
    Item compass = itemFactory.newItem(ItemType.Compass);
    player.setCompassTarget((int)snitchLocation.getX(), (int)snitchLocation.getY(), (int)snitchLocation.getZ());
    givePlayerItemTask = new GivePlayerItemTask(player, compass, 0, GIVE_COMPASS_DELAY_IN_SECONDS, this);
    Canary.getServer().addSynchronousTask(givePlayerItemTask);
  }

  public void ExecuteTaskCallback(ServerTask caller){
    if(caller instanceof GivePlayerItemTask)
      incrementCompassCount();
    else if(caller instanceof SpawnParticlesTask)
      tryEarnAchievement(player, AchievementType.SPARKLE, database);
  }

  private int totalCompassCount;

  private void incrementCompassCount(){
    totalCompassCount++;
    score += POINTS_FOR_ADDED_COMPASS;
    displayScoreMessage(POINTS_FOR_ADDED_COMPASS, false);
  }

  private boolean hasPlayerStartedGame = false;
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
      if(hasPlayerStartedGame){
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
      else if(EZPlugin.locEqual(clickedLocation, RandomMapSignLocation)){
        SELECTED_MAP = Map.GetRandomMap();
        DatabaseUtils database = setUpDatabase();
        tryEarnAchievement(playerClicked, AchievementType.RANDOM_MAP, database);
        database.CloseConnection();
      }
        
      else
        return;
      
      Utils.BroadcastServerMessage(pluginName, "Das Spiel startet auf der Map: " + ChatFormat.GOLD + SELECTED_MAP.toString());
      hasPlayerStartedGame = true;
      this.player = playerClicked;
      Canary.getServer().addSynchronousTask(new TeleportPlayerTask(playerClicked, SELECTED_MAP.GetRandomSpawnPosition(), 3));
    }
    else if(isEnabled && this.player == playerClicked && EZPlugin.locEqual(clickedLocation, snitchLocation)){
      rightClickCatches++;
      tryEarnCatchAchievements();
      snitchCatched(POINTS_PER_RIGHTCLICK, SoundEffect.Type.NOTE_PLING);
    }
  }

  private void tryEarnCatchAchievements(){
    if(currentSnitchCount == 1 && rightClickCatches == 1)
        tryEarnAchievement(player, AchievementType.GREAT_START, database);
    if(!player.isOnGround())
      tryEarnAchievement(player, AchievementType.AIR_JORDAN, database);
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
    int points = BASE_POINTS_PER_ARROW_HIT + (int)playerSnitchDistance;
    return points;
  }

  private void setShortestLongestBowHit(double playerSnitchDistance){
    if(playerSnitchDistance > longestBowHit){
      longestBowHit = (int)playerSnitchDistance;
      if(longestBowHit >= 25)
        tryEarnAchievement(player, AchievementType.MID_SHOT, database);
      if(longestBowHit >= 50)
        tryEarnAchievement(player, AchievementType.LONG_SHOT, database);
      if(longestBowHit >= 70)
        tryEarnAchievement(player, AchievementType.KATNISS_EVERDEEN, database);
    }
      
    if(playerSnitchDistance < shortestBowHit)
      shortestBowHit = (int)playerSnitchDistance;
  }

  private int missedArrowCount;
  private void decreaseScoreForMissedArrow(){
    score += POINTS_FOR_MISSED_ARROW;
    displayScoreMessage(POINTS_FOR_MISSED_ARROW, false);
    missedArrowCount++;
    if(missedArrowCount >= 10)
      tryEarnAchievement(player, AchievementType.STORMTROOPER, database);
  }

  
  private int fastCatchStreak;

  private void snitchCatched(int pointsScored, SoundEffect.Type soundType){
    checkForSnitchCatchAchievements();
    snitchLocation.getWorld().setBlockAt(snitchLocation, BlockType.Air);
    removeCompassFromPlayer();
    Canary.getServer().removeSynchronousTask(spawnParticlesTask);
    if(changeBlockTypeTask != null)
      Canary.getServer().removeSynchronousTask(changeBlockTypeTask);
    currentSnitchCount++;
    boolean isFastCatch = handleFastCatches();

    if(isFastCatch){
      pointsScored += POINTS_FOR_FAST_CATCH;
      tryEarnAchievement(player, AchievementType.FAST_CATCH, database);
    }
      
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

  private void checkForSnitchCatchAchievements(){
    if(player.isInWater())
      tryEarnAchievement(player, AchievementType.MERMAID, database);
    int heightDifference = Utils.CalculateHeightDifference(player.getLocation(), snitchLocation);
    if(heightDifference >= 10)
      tryEarnAchievement(player, AchievementType.HIGH_GROUND, database);
    checkForSpecialAchievementLocation();
  }

  private void checkForSpecialAchievementLocation(){
    if(Utils.IsInsideVolume(new Location(29, 108, 256), new Location(26, 109, 251), snitchLocation))
      tryEarnAchievement(player, AchievementType.ALL_WATER_UNDER_THE_BRIDGE, database);
    else if(Utils.IsInsideVolume(new Location(142, 134, 275), new Location(209, 136, 275), snitchLocation))
      tryEarnAchievement(player, AchievementType.UNLUCKY_HAUNT, database);
    else if(Utils.IsInsideVolume(new Location(148, 154, 291), new Location(144, 129, 295), snitchLocation) //Gryffindor
          || Utils.IsInsideVolume(new Location(148, 154, 327), new Location(144, 129, 323), snitchLocation) //Ravenclaw
          || Utils.IsInsideVolume(new Location(178, 154, 327), new Location(182, 129, 323), snitchLocation) //Slytherin
          || Utils.IsInsideVolume(new Location(178, 154, 291), new Location(182, 129, 295), snitchLocation)){ //Hufflepuff
      tryEarnAchievement(player, AchievementType.TOWER, database);
    }
    else if(EZPlugin.locEqual(snitchLocation, new Location(182, 144, 313)) || EZPlugin.locEqual(snitchLocation, new Location(181, 145, 309)) //Goalposts
      || EZPlugin.locEqual(snitchLocation, new Location(182, 144, 305)) || EZPlugin.locEqual(snitchLocation, new Location(144, 144, 305))
      || EZPlugin.locEqual(snitchLocation, new Location(145, 145, 309)) || EZPlugin.locEqual(snitchLocation, new Location(144, 144, 313))){
      tryEarnAchievement(player, AchievementType.GOAL, database);
    }
    
    Block belowSnitch = snitchLocation.getWorld().getBlockAt((int)snitchLocation.getX(), (int)snitchLocation.getY() - 1, (int)snitchLocation.getZ());
    if(belowSnitch.getType() == BlockType.Lava)
      tryEarnAchievement(player, AchievementType.CRISPY, database);
    
    for(Block surroundingBlock : Utils.GetSurroundingBlocks(snitchLocation)){
      if(Utils.IsWhiteStainedGlass(surroundingBlock))
        tryEarnAchievement(player, AchievementType.ON_EDGE, database);
      else if(surroundingBlock.getType() == BlockType.GoldBlock)
        tryEarnAchievement(player, AchievementType.MIX_UP, database);
      else if(surroundingBlock.getType() == BlockType.PineLeaves || surroundingBlock.getType() == BlockType.BirchLeaves)
        tryEarnAchievement(player, AchievementType.GARDENER, database);
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

    if(catchTimeInSeconds > GIVE_COMPASS_DELAY_IN_SECONDS && catchTimeInSeconds <= GIVE_COMPASS_DELAY_IN_SECONDS + 3)
      tryEarnAchievement(player, AchievementType.UNECCESSARY_COMPASS, database);
    
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
    tryEarnAchievement(player, AchievementType.FAST_STREAK, database);
    totalFastCatchStreaks++;
    if(totalFastCatchStreaks > 1)
      tryEarnAchievement(player, AchievementType.STREAK_MASTER, database);
    fastCatchStreak = 0;
    score += POINTS_FOR_FAST_CATCH_STREAK; 
    Utils.BroadcastServerMessage(pluginName, ChatFormat.YELLOW + "Blitzfang streak!");
    Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.WITHER_SHOOT, 1.0f, 1.0f);
    displayScoreMessage(POINTS_FOR_FAST_CATCH_STREAK, false);
    Utils.GivePlayerSpeedEffect(player, SPEED_FOR_FAST_CATCH_STREAK_IN_SECONDS, 1);
  }
  
  private int consecutiveGames = 0;

  private void initializeWin(){
    consecutiveGames++;
    Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.LEVEL_UP, 3.0f, 1.0f);
    displayWinMessage();
    insertGameSessionIntoDb();
    checkForAchievements();
    updateHighScoreSigns();
    cleanUpAfterGame();
  }

  private void checkForAchievements(){
    int totalGameCount = database.GetQuidditchGameCountByPlayer(player.getDisplayName());
    if(totalGameCount >= 1)
      tryEarnAchievement(player, AchievementType.WARM_UP, database);
    if(totalGameCount >= 50)
      tryEarnAchievement(player, AchievementType.GAME_50, database);
    if(totalGameCount >= 100)
      tryEarnAchievement(player, AchievementType.GAME_100, database);
    if(totalGameCount >= 200)
      tryEarnAchievement(player, AchievementType.GAME_200, database);
    if(totalGameCount >= 350)
      tryEarnAchievement(player, AchievementType.GAME_350, database);
    if(totalGameCount >= 500)
      tryEarnAchievement(player, AchievementType.GAME_500, database);
    
    if(consecutiveGames >= 21)
      tryEarnAchievement(player, AchievementType.HALF_MARATHON, database);
    if(consecutiveGames >= 42)
      tryEarnAchievement(player, AchievementType.MARATHON, database);

    int distinctMapsPlayedCount = database.GetQuidditchMapCountByPlayer(player.getDisplayName());
    if(distinctMapsPlayedCount == Map.values().length)
      tryEarnAchievement(player, AchievementType.MAP_SPECIALIST, database);
    
    if(!hasPlayerLostHealth)
      tryEarnAchievement(player, AchievementType.INVINCIBLE, database);
    
    if(hasPlayerStandOnGround)
      tryEarnAchievement(player, AchievementType.EARTHBOUND, database);
    
    if(missedArrowCount == 0){
      tryEarnAchievement(player, AchievementType.PERFECT_ACCURACY, database);
      if(rightClickCatches <= 5)
        tryEarnAchievement(player, AchievementType.SHARP_SHOOTER, database);
    }
    if(rightClickCatches == 10)
      tryEarnAchievement(player, AchievementType.HAND_ONLY, database);
    if(rightClickCatches == 0){
      tryEarnAchievement(player, AchievementType.ARROW_SUPREMACY, database);
      if(missedArrowCount == 0)
        tryEarnAchievement(player, AchievementType.HAWKEYE, database);
    }

    if(score >= 1000)
      tryEarnAchievement(player, AchievementType.OVER_1000, database);
    if(score >= 1500)
      tryEarnAchievement(player, AchievementType.OVER_1500, database);

    if(endTimeInSeconds < 60)
      tryEarnAchievement(player, AchievementType.UNDER_60, database);
    if(endTimeInSeconds < 30)
      tryEarnAchievement(player, AchievementType.UNDER_30, database);

    if(totalCompassCount == 0)
      tryEarnAchievement(player, AchievementType.NO_COMPASS_REQUIRED, database);
    
    int consecutiveDaysPlayed = database.GetConsecutiveDayCount(player.getDisplayName());
    if(consecutiveDaysPlayed >= 7)
      tryEarnAchievement(player, AchievementType.DEDICATED, database);
    
    int todayMapCount = database.GetTodayQuidditchMapCountByPlayer(player.getDisplayName());
    if(todayMapCount >= 5)
      tryEarnAchievement(player, AchievementType.RAINBOW, database);
  }

  private int endTimeInSeconds;

  private void insertGameSessionIntoDb(){
    String playerName = player.getDisplayName();
    int bowHits = SNITCHES_PER_GAME - rightClickCatches;
    endTimeInSeconds = timerTask.getElapsedTimeInSeconds();

    database.insertGameSession(playerName, score, rightClickCatches, bowHits, 
      fastestCatch, slowestCatch, totalFastCatches, totalFastCatchStreaks, 
      missedArrowCount, SELECTED_MAP.toString(), endTimeInSeconds, 
      shortestBowHit, longestBowHit, totalCompassCount);
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
    for(int i = 0; i < top3Scores.size(); i++){
      String[] parts = top3Scores.get(i).split(" ");
      String points = parts[1];

      if(this.score == Integer.parseInt(points)){
        String serverMessage = ChatFormat.YELLOW + "NEW HIGHSCORE! " + ChatFormat.DARK_GREEN + "You are now on number " + ChatFormat.GOLD + (i+1) 
          + ChatFormat.DARK_GREEN + " with a score of " + ChatFormat.GOLD + this.score + ChatFormat.DARK_GREEN + ".";
        Utils.BroadcastServerMessage(pluginName, serverMessage);
        writeTextToSign(SELECTED_MAP.toString(), top3Scores, SELECTED_MAP.GetQuidditchPluginHighScoreSign(), true);
        Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ENDERDRAGON_GROWL, 1.0f, 1.0f);
        break;
      }
    }
  }

  private void updateHighScoreTimes(){
    List<String> top3Scores = database.GetTop3TimesFromMap(SELECTED_MAP);
    for(int i = 0; i < top3Scores.size(); i++){
      String[] parts = top3Scores.get(i).split(" ");
      String gameDuration = parts[1];

      if(endTimeInSeconds == Utils.ReformatSecondsPassedIntoInt(gameDuration)){
        String serverMessage = ChatFormat.YELLOW + "NEW HIGHSCORE! " + ChatFormat.DARK_GREEN + "You are now on number " + ChatFormat.GOLD + (i+1) 
          + ChatFormat.DARK_GREEN + " with a score of " + ChatFormat.GOLD + Utils.FormatSecondsPassedIntoString(endTimeInSeconds) + ChatFormat.DARK_GREEN + ".";
        Utils.BroadcastServerMessage(pluginName, serverMessage);
        writeTextToSign(SELECTED_MAP.toString(), top3Scores, SELECTED_MAP.GetQuidditchPluginHighScoreTimeSign(), true);
        Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ENDERDRAGON_GROWL, 1.0f, 1.0f);
        break;
      }
    }
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
    Location mostPlayedMapsSign = new Location(248, 55, 264);
    writeTextToSign("Most played Maps", top3MapsPlayed, mostPlayedMapsSign, true);
  }

  private void updateShortestAndLongestBowHit(){
    List<String> shortestAndLongestBowHit = database.GetShortestAndLongestBowHit();
    boolean hasNewHighScore = false;

    String[] parts = shortestAndLongestBowHit.get(0).split(" ");
    int shortestBowHitFromDb = Integer.parseInt(parts[0]);
    if(shortestBowHitFromDb == shortestBowHit){
      String serverMessage = ChatFormat.YELLOW + "NEW HIGHSCORE! " + ChatFormat.DARK_GREEN + "You have made the shortest bow hit with " 
        + ChatFormat.GOLD + shortestBowHit + ChatFormat.DARK_GREEN + " Blocks.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      hasNewHighScore = true;
    }

    parts = shortestAndLongestBowHit.get(1).split(" ");
    int longestBowHitFromDb = Integer.parseInt(parts[0]);
    if(longestBowHitFromDb == longestBowHit){
      String serverMessage = ChatFormat.YELLOW + "NEW HIGHSCORE! " + ChatFormat.DARK_GREEN + "You have made the longest bow hit with " 
        + ChatFormat.GOLD + longestBowHit + ChatFormat.DARK_GREEN + " Blocks.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      hasNewHighScore = true;
    }

    if(hasNewHighScore){
      shortestAndLongestBowHit.add(1, "Longest Bow Hit");
      Location shortestAndLongestHighscoreSign = new Location(248, 54, 264);
      writeTextToSign("Shortest Bow Hit", shortestAndLongestBowHit, shortestAndLongestHighscoreSign, false);
      Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ENDERDRAGON_GROWL, 1.0f, 1.0f);
    }
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
    Canary.getServer().removeSynchronousTask(spawnParticlesTask);
    if(changeBlockTypeTask != null)
      Canary.getServer().removeSynchronousTask(changeBlockTypeTask);
    eventChangingBlocks = null;
    Utils.clearScoreboard(scoreboard, timerTask, objective);
    isEnabled = false;
    isFirstStartPort = true;
    isGlitchActive = false;
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
        if(event.getDamageSource().getDamagetype() == DamageType.LAVA)
          tryEarnAchievement(player, AchievementType.LAVA_SWIMMER, database);

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
      tryEarnAchievement(player, AchievementType.REBEL, database);
    }
  }

  @HookHandler
  public void SlotClickHook(SlotClickHook event){
    if(isCustomInventoryOpen)
      event.setCanceled();
    else if(isEnabled && event.getPlayer() == player){
      ButtonPress buttonPress = event.getButtonPress();
      //allows only to rearrange hotbar
      if(event.getSlotId() < 36)
        event.setCanceled();
      else if(buttonPress == ButtonPress.KEY_DROP || buttonPress == ButtonPress.CTRL_DROP){
        event.setCanceled();
        tryEarnAchievement(player, AchievementType.REBEL, database);
      }
    }
  }

  private boolean hasPlayerLostHealth;

  @HookHandler
  public void HealthChangeHookEvent(HealthChangeHook event){
    if(isEnabled && event.getPlayer() == player && event.getOldValue() > event.getNewValue())
      hasPlayerLostHealth = true;
  }

  private boolean hasPlayerStandOnGround;

  @HookHandler
  public void PlayerMoveHookEvent(PlayerMoveHook event){
    if(isEnabled && event.getPlayer() == player){
      if(!player.isOnGround())
        hasPlayerStandOnGround = false;
    }
  }
  
  private void displayPlayerStats(Player player){
    DatabaseUtils statsDb = setUpDatabase();

    String serverMessage = "Das sind die Stats von " + ChatFormat.BLUE + player.getDisplayName() + ChatFormat.DARK_GREEN + ":\n";
    HashMap<String, String> stats = statsDb.GetPlayerStatsForQuidditch(player.getDisplayName());
    for(String key : stats.keySet())
      serverMessage += ChatFormat.DARK_GREEN + " - " + key + ": " + ChatFormat.GOLD + stats.get(key) + "\n";
      
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    tryEarnAchievement(player, AchievementType.RAVENCLAW_ZAG, statsDb);

    statsDb.CloseConnection();
  }

  private void displayPlayerStatsEachMap(Player player){
    DatabaseUtils statsDb = setUpDatabase();

    String serverMessage = "Das sind die Stats von " + ChatFormat.BLUE + player.getDisplayName() + ChatFormat.DARK_GREEN + ":\n";
    List<HashMap<String, String>> lists = statsDb.GetPlayerStatsForQuidditchEachMap(player.getDisplayName());
    for(HashMap<String, String> stats : lists){
      for(String key : stats.keySet())
        serverMessage += ChatFormat.DARK_GREEN + " - " + key + ": " + ChatFormat.GOLD + stats.get(key) + "\n";
      
      serverMessage += ChatFormat.DARK_AQUA + "------------------------------------------------\n";
    }

    Utils.BroadcastServerMessage(pluginName, serverMessage);
    tryEarnAchievement(player, AchievementType.RAVENCLAW_UTZ, statsDb);

    statsDb.CloseConnection();
  }

  private void displayUsageMessage(){
    String serverMessage = "Die folgenden Kommandos stehen zur Verfügung:\n";
    String command1 = ChatFormat.GOLD + "/quidditch" + ChatFormat.DARK_GREEN + " -> Teleportiert dich zum Quidditch Minigame Hub\n";
    String command2 = ChatFormat.GOLD + "/quidditch usage" + ChatFormat.DARK_GREEN + "-> Zeigt alle verfügbaren Kommandos\n";
    String command3 = ChatFormat.GOLD + "/quidditch achievements" + ChatFormat.DARK_GREEN + "-> Zeigt alle erspielten Achievements\n";
    String command4 = ChatFormat.GOLD + "/quidditch stats" + ChatFormat.DARK_GREEN + "-> Zeigt die Statistik des Spielers\n";
    String command5 = ChatFormat.GOLD + "/quidditch stats map" + ChatFormat.DARK_GREEN + "-> Zeigt die Statistik des Spielers für jede Map";

    Utils.BroadcastServerMessage(pluginName, serverMessage + command1 + command2 + command3 + command4 + command5);
  }

  private boolean isCustomInventoryOpen = false;

  private void displayAchievementInventory(Player player){
    ObjectFactory objectFactory = Canary.factory().getObjectFactory();
    int inventoryRows = (AchievementType.values().length + 8) / 9; //This ensures correct size for the custom Inventory
    CustomStorageInventory customInventory = objectFactory.newCustomStorageInventory(ChatFormat.DARK_AQUA + "Achievements", inventoryRows);
    fillInventoryWithAchievements(customInventory, player);
    player.openInventory(customInventory);
    isCustomInventoryOpen = true;
  }

  private void fillInventoryWithAchievements(CustomStorageInventory customInventory, Player player){
    DatabaseUtils database = setUpDatabase();
    ItemFactory itemFactory = Canary.factory().getItemFactory();
    
    for(int i = 0; i < AchievementType.values().length; i++){
      Item item;
      AchievementType achievement = AchievementType.values()[i];

      if(database.hasPlayerQuidditchAchievement(player.getDisplayName(), achievement.toString())){
        item = itemFactory.newItem(ItemType.LimeDye);
        item.setDisplayName(ChatFormat.GREEN + achievement.toString() + " - " + achievement.getDescription());
      }
      else{
        item = itemFactory.newItem(ItemType.GrayDye);
        item.setDisplayName(ChatFormat.RED + "Noch nicht erspielt!");
      }

      customInventory.setSlot(i, item);
    }
    database.CloseConnection();
  }

  @HookHandler
  public void InventoryHookEvent(InventoryHook event){
    if(isCustomInventoryOpen && event.isClosing())
      isCustomInventoryOpen = false;
  }
  
  private void tryEarnAchievement(Player player, AchievementType achievementType, DatabaseUtils database){
    if(!database.hasPlayerQuidditchAchievement(player.getDisplayName(), achievementType.toString()))
      insertAchievementIntoDb(player, achievementType, database);
  }

  private void insertAchievementIntoDb(Player player, AchievementType achievementType, DatabaseUtils database){
    database.InsertQuidditchAchievementIntoDbForPlayer(player.getDisplayName(), achievementType.toString());
    displayAchievementEarnMessage(player, achievementType);
  }

  private void displayAchievementEarnMessage(Player player, AchievementType achievementType){
    String serverMessage = "Du hast das Achievement " + ChatFormat.GOLD + achievementType.toString() + ChatFormat.DARK_GREEN + " erspielt!";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ORB, 1.0f, 0.9f);
    Utils.playSoundAtLocation(Utils.quidditchHubLocation, SoundEffect.Type.ORB, 1.0f, 0.9f);
  }
}