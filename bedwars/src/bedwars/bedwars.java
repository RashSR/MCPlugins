package bedwars;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.entity.living.humanoid.Villager;
import net.canarymod.api.VillagerTrade;
import net.canarymod.api.entity.EntityType;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.PlayerDeathHook;
import java.util.HashMap;
import java.util.Map;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.world.RedstoneChangeHook;
import net.canarymod.hook.player.TeleportHook;
import java.util.ArrayList;
import java.util.List;
import utils.SpawnItemsTask;
import utils.Utils;
import net.canarymod.hook.entity.VillagerTradeUnlockHook;
import net.canarymod.hook.entity.EntitySpawnHook;

public class bedwars extends EZPlugin implements PluginListener{

  private static Map<String,String> teamColor = new HashMap<String,String>();
  public static List<Player> playerList = new ArrayList<Player>();
  public static boolean farm = false;
  public static List<String> voteList = new ArrayList<String>();
  int farmcounter = 0;
  int mapcounter = 0;
  private final String pluginName = "[Bedwars]";
  private final int BRONZE_SPAWN_DELAY_IN_SECONDS = 2;
  private final int IRON_SPAWN_DELAY_IN_SECONDS = 17;
  private final int GOLD_SPAWN_DELAY_IN_SECONDS = 25;
  private Location teamPurpleBedLocation = new Location(321, 228, 396);
  private Location teamYellowBedLocation = new Location(430, 228, 504);
  private Location teamGreenBedLocation = new Location(536, 228, 394);     
  private Location teamRedBedLocation = new Location(427, 228, 287);

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

  @HookHandler
  public void RedstoneChangeHookEvent(RedstoneChangeHook event){
    Block pressurePlate = event.getSourceBlock();
    int dx = pressurePlate.getX();
    int dy = pressurePlate.getY();
    int dz = pressurePlate.getZ();
    World world = pressurePlate.getWorld();

    if(dx == 243 && dy == 71 && dz == 259){
      Player player = world.getClosestPlayer(244, 71, 258, 5);
      teamColor.put(player.getDisplayName(), "");
    }
    else if(dx == 297 && dy == 20 && dz == 269){
      Player player = world.getClosestPlayer(297, 20, 269, 5);
      teamColor.remove(player.getDisplayName());
      player.setPrefix(ChatFormat.WHITE + "");
    }
  }

  @HookHandler
  public void TeleportHookEvent(TeleportHook event){
    //boolean der es an/ausschaltet da sonst hashmap geleert wird sofern sich jemand rausportet
    Player player = event.getPlayer();
    Location currentLocation = event.getCurrentLocation();
    double xa = currentLocation.getX();
    double za = currentLocation.getZ();
    Location destinationLocation = event.getDestination();
    double xz = destinationLocation.getX();
    double zz = destinationLocation.getZ();

    if (xa >= 283 && xa <= 306 && za >= 254 && za <= 272){
      if(xz < 283 || xz > 306){
        if(xz > 300 && xz < 555)
          return;

        teamColor.remove(player.getDisplayName());
        player.setPrefix(ChatFormat.WHITE + "");
      }
      else if(zz < 254 || zz > 272){
        if(zz >= 300 && zz <=525)
          return;

        teamColor.remove(player.getDisplayName());
        player.setPrefix(ChatFormat.WHITE + "");
      }
    }
  }

  public void startBedwarsGame(){
    if(farmcounter > mapcounter)
      farm = true;

    if(farm){ 
      if(playerList.size() > 1){
        for(Player player : playerList){
          String farbe = teamColor.get(player.getDisplayName());

          if(farbe.equalsIgnoreCase("red")){
            Location teamred = new Location(427, 227, 294);
            player.teleportTo(teamred);
          }
          else if(farbe.equalsIgnoreCase("purple")){
            Location teampurple = new Location(328, 227, 396);
            player.teleportTo(teampurple);
          }
          else if(farbe.equalsIgnoreCase("green")){
            Location teamgreen = new Location(529, 227, 394);
            player.teleportTo(teamgreen);
          }
          else if(farbe.equalsIgnoreCase("yellow")){
            Location teamyellow = new Location(430, 227, 497);
            player.teleportTo(teamyellow);
          }                                       
        }
      }
    }
  }

  @HookHandler
  public void PlayerDeathHookEvent(PlayerDeathHook event){
    Player player = event.getPlayer();
    Location loc = player.getLocation();
    double xp = player.getX();
    double zp = player.getZ();
    int x = (int) xp;
    int z = (int) zp;
    World world = loc.getWorld();

    if(z >= 300 && z <= 525 && x > 300 && x < 555 && farm){
      String teamColorOfPlayer = teamColor.get(player.getDisplayName());

      if(teamColorOfPlayer.equalsIgnoreCase("purple")){
        Block bedTeamPurple = world.getBlockAt(Utils.ConvertLocationToPosition(teamPurpleBedLocation));
        
        if(bedTeamPurple.getType() == BlockType.BedBlock){
          Location spawnPositionPurple = new Location(328, 227, 396);
          player.setSpawnPosition(spawnPositionPurple);
          displayDeathMessage(player);
        }
        else
          eliminatePlayer(player);
      }
      else if(teamColorOfPlayer.equalsIgnoreCase("red")){
        Block bedTeamRed = world.getBlockAt(Utils.ConvertLocationToPosition(teamRedBedLocation));

        if(bedTeamRed.getType() == BlockType.BedBlock){
          Location spawnPositionRed = new Location(427, 227, 294);
          player.setSpawnPosition(spawnPositionRed);
          displayDeathMessage(player);
        }
        else
          eliminatePlayer(player);
      }
      else if(teamColorOfPlayer.equalsIgnoreCase("green")){
        Block bedTeamGreen = world.getBlockAt(Utils.ConvertLocationToPosition(teamGreenBedLocation));

        if(bedTeamGreen.getType() == BlockType.BedBlock){
          Location spawnPositionGreen = new Location(529, 227, 394);
          player.setSpawnPosition(spawnPositionGreen);
          displayDeathMessage(player);
        }
        else
          eliminatePlayer(player);
      }
      else if(teamColorOfPlayer.equalsIgnoreCase("yellow")){
        Block bedTeamYellow = world.getBlockAt(Utils.ConvertLocationToPosition(teamYellowBedLocation));

        if(bedTeamYellow.getType() == BlockType.BedBlock){
          Location spawnPositionYellow = new Location(430, 227, 497);
          player.setSpawnPosition(spawnPositionYellow);
          displayDeathMessage(player);
        }
        else
          eliminatePlayer(player);
      }
    } 
  }

  @Command(aliases = { "bedwars"},
           description = "Teleportiert den Spieler zur Bedwarsmap.",
           permissions = {""},
           toolTip = "/bedwars maps, or /bedwars map Farm")
  public void TeleportToBedwarsHub(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player) {
      if(args.length == 1){
        Location bedwarshub = new Location(304, 20, 263);
        player.teleportTo(bedwarshub);
        teamColor.put(player.getDisplayName(), "");
        return;
      }
      if(args.length == 2 && args[1].equalsIgnoreCase("maps")){
        displayBwMapsMessage();
      }
      if(args[1].equalsIgnoreCase("map") && args[2].equalsIgnoreCase("farm")){
        Location farmMapLocation = new Location(424, 227, 395);
        player.teleportTo(farmMapLocation);
      }
    }
  }    

  @Command(aliases = {"bwstart"},
          description = "startet bedwars",
          permissions = "*",
          toolTip = "/bwstart")
  public void bwstart(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
      Location loc = player.getLocation();
      World world = loc.getWorld();
      startBedwarsGame();
      if(farm){
        startGoldSpawning();
        startBronzeSpawning();
        startIronSpawning();
        bettcheck(world, player);
      }
    }
  }

  @Command(aliases = {"bwgold"},
           description = "goldspawner",
           permissions = { "*" },
           toolTip = "/bwgold")
  public void bwgoldCommand(MessageReceiver caller, String[] parameters){
    if (caller instanceof Player) {
      Player player = (Player)caller;
      startGoldSpawning();
    }
  }

  private SpawnItemsTask goldTask;

  private void startGoldSpawning(){
    createCustomItems();
    goldTask = new SpawnItemsTask(customGold, getGoldSpawner(), GOLD_SPAWN_DELAY_IN_SECONDS, true);
    Canary.getServer().addSynchronousTask(goldTask);
  }

  private ArrayList<Location> getGoldSpawner(){
    ArrayList<Location> locs = new ArrayList<>();
    locs.add(new Location(430, 230, 396));
    locs.add(new Location(431, 228, 395));
    locs.add(new Location(428, 228, 395));
    return locs;
  }

  @Command(aliases = {"bwbronze"},
           description = "bronzespawner",
           permissions = {"*"},
           toolTip = "/bwbronze")
  public void bwbronzeCommand(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
      startBronzeSpawning();
    }
  }

  private SpawnItemsTask bronzeTask;

  private void startBronzeSpawning(){
    createCustomItems();
    bronzeTask = new SpawnItemsTask(customBronze, getBronzeSpawner(), BRONZE_SPAWN_DELAY_IN_SECONDS, true);
    Canary.getServer().addSynchronousTask(bronzeTask);
  }

  private ArrayList<Location> getBronzeSpawner(){
    Location bronzeYellowSpawner = new Location(427, 227, 493);
    Location bronzeRedSpawner = new Location(430, 227, 298);
    Location bronzePurpleSpawner = new Location(332, 227, 393);
    Location bronzeGreenSpawner = new Location(525, 227, 397);

    ArrayList<Location> locs = new ArrayList<>();
    locs.add(bronzeYellowSpawner);
    locs.add(bronzeRedSpawner);
    locs.add(bronzePurpleSpawner);
    locs.add(bronzeGreenSpawner);
    return locs;
  }

  private Item customBronze;
  private Item customIron;
  private Item customGold;
  
  private void createCustomItems(){
    ItemFactory factory = Canary.factory().getItemFactory();
    customBronze = factory.newItem(ItemType.ClayBrick);
    customBronze.setDisplayName(ChatFormat.GRAY + "Bronze");

    customGold = factory.newItem(ItemType.GoldIngot);
    customGold.setDisplayName(ChatFormat.GOLD + "Gold");

    customIron = factory.newItem(ItemType.IronIngot);
    customIron.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");
  }

  @Command(aliases = {"bwsilber"},
         description = "silberspawner",
         permissions = {"*"},
         toolTip = "/bwsilber")
  public void bwsilberCommand(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
      startIronSpawning();
    }
  }

  private SpawnItemsTask ironTask;

  private void startIronSpawning(){
    createCustomItems();
    ironTask = new SpawnItemsTask(customIron, getIronSpawner(), IRON_SPAWN_DELAY_IN_SECONDS, true);
    Canary.getServer().addSynchronousTask(ironTask);
  }

  private ArrayList<Location> getIronSpawner(){
    Location bronzeYellowSpawner = new Location(436, 228, 502);
    Location bronzeRedSpawner = new Location(421, 228, 289);
    Location bronzePurpleSpawner = new Location(323, 228, 402);
    Location bronzeGreenSpawner = new Location(534, 228, 388);

    ArrayList<Location> locs = new ArrayList<>();
    locs.add(bronzeYellowSpawner);
    locs.add(bronzeRedSpawner);
    locs.add(bronzePurpleSpawner);
    locs.add(bronzeGreenSpawner);
    return locs;
  }

  @Command(aliases = { "bwclear" },
          description = "bedwars plugin",
          permissions = { "*" },
          toolTip = "/bwclear")
  public void removeSandStoneBlocks(MessageReceiver caller, String[] parameters) {
    if(caller instanceof Player player){ 
      spawnVillagerWithCustomTrades();
      Location loc = player.getLocation();
      World world = loc.getWorld();

      for (int x = 300; x <= 555; x++) {
        for(int y = 212; y <= 254; y++) {
          for (int z = 300; z <= 525; z++) { 
            Block b = world.getBlockAt(x, y, z);
            if(b.getType() == BlockType.SandstoneBlank)
              b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);
          }
        }
      }    
    } 
  }

  public void bettcheck(World world, Player player){
    int i = 0;
    int j = 0;

    Block purpleBed = world.getBlockAt(Utils.ConvertLocationToPosition(teamPurpleBedLocation));
    if(purpleBed.getType() == BlockType.BedBlock)
      j++;
    else{
      teamPurpleBedLocation.getWorld().setBlockAt(teamPurpleBedLocation, BlockType.BedBlock);
      i++;
    }

    Block yellowBed = world.getBlockAt(Utils.ConvertLocationToPosition(teamYellowBedLocation));
    if(yellowBed.getType() == BlockType.BedBlock)
      j++;
    else{
      teamYellowBedLocation.getWorld().setBlockAt(teamYellowBedLocation, BlockType.BedBlock);
      i++;
    }

    Block greenBed = world.getBlockAt(Utils.ConvertLocationToPosition(teamGreenBedLocation));
    if(greenBed.getType() == BlockType.BedBlock)
      j++;
    else{
      teamGreenBedLocation.getWorld().setBlockAt(teamGreenBedLocation, BlockType.BedBlock);
      i++;
    }

    Block redBed = world.getBlockAt(Utils.ConvertLocationToPosition(teamRedBedLocation));
    if(redBed.getType() == BlockType.BedBlock){
      j++;
    }
    else{
      teamRedBedLocation.getWorld().setBlockAt(teamRedBedLocation, BlockType.BedBlock);
      i++;
    }

    if(i >= 1 || j == 4)
      displayBedClearMessage();
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    Block clickedBlock = event.getBlockClicked();
    Player player = event.getPlayer();
    int x = clickedBlock.getX();
    int y = clickedBlock.getY();
    int z = clickedBlock.getZ();

    if(clickedBlock.getType() == BlockType.WallSign){
      if(x == 283 && y == 20 && z == 263){
        if(voteList.size() >= 1){
          for(String playername : voteList){
            if(playername.equalsIgnoreCase(player.getDisplayName())){
              event.setCanceled();
              return;
            }
          }
        }

        farmcounter = farmcounter + 1;
        Utils.BroadcastServerMessage(pluginName,  ChatFormat.DARK_GREEN + "Die Map Farm hat " + ChatFormat.GOLD + farmcounter + ChatFormat.DARK_GREEN + " Stimmen.");
        voteList.add(player.getDisplayName());
      }
    } 

    if(clickedBlock.getType() == BlockType.BedBlock){
      if(teamColor.size() > 1){
        playerList = Canary.getServer().getPlayerList();
      }

      if(x == 293 && y == 20){
        if(z == 271 || z == 272){
          event.setCanceled();

          if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("red")){
            if(teamColor.size() > 1){
              for(Player spieler : playerList){
                double xs = spieler.getX();
                double zs = spieler.getZ();
                if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                  if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("red")){
                    displayTeamAlreadyTakenMessage();
                    return;
                  }
                }
              }
            } 

            teamColor.put(player.getDisplayName(), "red");
            player.setPrefix(ChatFormat.RED + "");
            Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.RED + "rot" + ChatFormat.DARK_GREEN + ".");
          }
        }

        if(z == 255 || z == 254){
          event.setCanceled();

          if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("yellow")){
            if(teamColor.size() > 1){
              for(Player spieler : playerList){
                double xs = spieler.getX();
                double zs = spieler.getZ();
                if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                  if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("yellow")){
                    displayTeamAlreadyTakenMessage();
                    return;
                  }
                }
              }
            } 

            teamColor.put(player.getDisplayName(), "yellow");
            player.setPrefix(ChatFormat.YELLOW + "");
            Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.YELLOW + "gelb" + ChatFormat.DARK_GREEN + ".");
          }
        }
      }

      if(x == 290 && y == 20){
        if(z == 271 || z == 272){
          event.setCanceled();

          if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("green")){
            if(teamColor.size() > 1){
              for(Player spieler : playerList){
                double xs = spieler.getX();
                double zs = spieler.getZ();
                if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                  if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("green")){
                    displayTeamAlreadyTakenMessage();
                    return;
                  }
                }
              }
            } 

            teamColor.put(player.getDisplayName(), "green");
            player.setPrefix(ChatFormat.DARK_GREEN + "");
            Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.GREEN + "gruen" + ChatFormat.DARK_GREEN + ".");
          }
        }

        if(z == 255 || z == 254){
          event.setCanceled();

          if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("purple")){
            if(teamColor.size() > 1){
              for(Player spieler : playerList){
                double xs = spieler.getX();
                double zs = spieler.getZ();
                if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                  if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("purple")){
                    displayTeamAlreadyTakenMessage();
                    return;
                  }
                } 
              }
            }

            teamColor.put(player.getDisplayName(), "purple");
            player.setPrefix(ChatFormat.DARK_PURPLE + "");
            Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.DARK_PURPLE + "lila" + ChatFormat.DARK_GREEN + ".");
          }
        }
      }
    }
  }

  @HookHandler
  public void VillagerTradeUnlockHookEvent(VillagerTradeUnlockHook event){
    //TODO: keep track of all villagers for bedwars and check in this hook with event.getVillager() if it is the correct one
    if(hasSpawnedCustomVillager)
      event.setCanceled();
  }

  @HookHandler
  public void EntitySpawnHookEvent(EntitySpawnHook event){
    if(hasSpawnedCustomVillager && event.getEntity().getEntityType() == EntityType.XPORB)
      event.setCanceled();
  }

  private boolean hasSpawnedCustomVillager = false;

  private void spawnVillagerWithCustomTrades(){
    hasSpawnedCustomVillager = true;
    Location villagerLocation = new Location(534, 227, 399);
    Villager villager = (Villager)spawnEntityLiving(villagerLocation, EntityType.VILLAGER);
    int tradeCount = villager.getTrades().length;
    for(int i = tradeCount - 1; i >= 0; i--)
      villager.removeTrade(i);

    createCustomItems();
    ObjectFactory objectFactory = Canary.factory().getObjectFactory();
    ItemFactory itemFactory = Canary.factory().getItemFactory();
    
    //first Trade
    Item sandStone = itemFactory.newItem(ItemType.SandstoneBlank);
    sandStone.setAmount(2);
    VillagerTrade blocksTrade = objectFactory.newVillagerTrade(customBronze, sandStone);
    //If set to the max value or near it -> the trade is marked as not possible
    blocksTrade.increaseMaxUses(Integer.MAX_VALUE / 2);
    villager.addTrade(blocksTrade);

    //second trade
    Item pickAxe = itemFactory.newItem(ItemType.WoodPickaxe);
    //needs its own Item instance to modify the amount needed for the trade
    Item tradeBronze = itemFactory.newItem(ItemType.ClayBrick);
    tradeBronze.setDisplayName(ChatFormat.GRAY + "Bronze");
    tradeBronze.setAmount(4);
    VillagerTrade pickaxeTrade = objectFactory.newVillagerTrade(tradeBronze, pickAxe);
    pickaxeTrade.increaseMaxUses(Integer.MAX_VALUE / 2);
    villager.addTrade(pickaxeTrade);
  }

  private void eliminatePlayer(Player player){
    teamColor.remove(player.getDisplayName());
    player.setSpawnPosition(Utils.HubLocation);
    displayOutOfGameMessage(player);
  }

  private void displayBwMapsMessage(){
    String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
    String msg3 = "Farm";
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + "."); 
  }

  public void displayBedClearMessage(){
    String msg2 = "Betten wurden geladen.";
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + msg2);
  }

  public void displayOutOfGameMessage(Player player) {
    String msg3 = " ist ";
    String msg4 = "ausgeschieden";
    Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");
  }

  private void displayDeathMessage(Player player) {
    String msg3 = " ist gestorben.";
    Utils.BroadcastServerMessage(pluginName, getColoredPlayerName(player) + ChatFormat.DARK_GREEN + msg3);
  }

  private String getColoredPlayerName(Player player){
    String playerName = player.getDisplayName();

    if(teamColor.get(playerName).equalsIgnoreCase("red"))
      playerName = ChatFormat.RED + playerName;
    else if(teamColor.get(playerName).equalsIgnoreCase("yellow"))
      playerName = ChatFormat.YELLOW + playerName;
    else if(teamColor.get(playerName).equalsIgnoreCase("green"))
      playerName = ChatFormat.GREEN + playerName;
    else if(teamColor.get(playerName).equalsIgnoreCase("purple"))
      playerName = ChatFormat.DARK_PURPLE + playerName;
    
    return playerName;
  }

  private void bedWarsCommands(){
    String msg2 = "Zurzeit gibt es folgende Befehle fuer Bedwars: ";
    String msg3 = "/bwgold";
    String msg4 = "/bwsilber";
    String msg5 = "/bwbronze";
    String msg6 = "/bwstart";
    String msg8 = "/bwmap";
  }

  private void displayTeamAlreadyTakenMessage(){
    Utils.BroadcastServerMessage(pluginName, ChatFormat.DARK_GREEN + "Diese Farbe ist bereits vergeben.");
  }
}