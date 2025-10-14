package bedwars;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.api.factory.ItemFactory;
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
import java.util.Arrays;
import java.util.List;
import utils.SpawnItemsTask;

public class bedwars extends EZPlugin implements PluginListener{

  private static Map<String,String> teamfarbe = new HashMap<String,String>();
  public static List<Player> spielerliste = new ArrayList<Player>();
  public static boolean farm = false;
  public static List<String> voteliste = new ArrayList<String>();
  int farmcounter = 0;
  int mapcounter = 0;
  private final String pluginName = "[Bedwars] ";

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

@HookHandler
 public void pressurefromandbackhub(RedstoneChangeHook event){

  Block druckplatte = event.getSourceBlock();
  int dx = druckplatte.getX();
  int dy = druckplatte.getY();
  int dz = druckplatte.getZ();
  World world = druckplatte.getWorld();

  if(dx == 243 && dy == 71 && dz == 259){

    Player player = world.getClosestPlayer(244, 71, 258, 5);
    teamfarbe.put(player.getDisplayName(), "");

                                         }

  if(dx == 297 && dy == 20 && dz == 269){

    Player player = world.getClosestPlayer(297, 20, 269, 5);
    teamfarbe.remove(player.getDisplayName());
    player.setPrefix(ChatFormat.WHITE + "");

                                        }
                                                             }

@HookHandler
 public void teleportausbedwarshub(TeleportHook event){
    //boolean der es an/ausschaltet da sonst hashmap geleert wird sofern sich jemand rausportet
    Player player = event.getPlayer();
    Location ausgangloc = event.getCurrentLocation();
    double xa = ausgangloc.getX();
    double za = ausgangloc.getZ();
    World world = ausgangloc.getWorld();
    Location zielloc = event.getDestination();
    double xz = zielloc.getX();
    double zz = zielloc.getZ();

    if (xa >= 283 && xa <= 306 && za >= 254 && za <= 272){

      if(xz < 283 || xz > 306){
        if(xz > 300 && xz < 555){

          return;

                                }

        teamfarbe.remove(player.getDisplayName());
        player.setPrefix(ChatFormat.WHITE + "");

                               }

      if(zz < 254 || zz > 272){
        if(zz >= 300 && zz <=525){

          return;
                                  
                                  }

        teamfarbe.remove(player.getDisplayName());
        player.setPrefix(ChatFormat.WHITE + "");

                              }
                                                         }
 }

public void bedwarsstart(){

  if(farmcounter > mapcounter){

    farm = true;

                              }
  if(farm){ 
    if(spielerliste.size() > 1){
    for(Player player : spielerliste){
      String farbe = teamfarbe.get(player.getDisplayName());

      if(farbe.equalsIgnoreCase("red")){

        Location teamred = new Location(427, 227, 294);
        player.teleportTo(teamred);

                                       }

      if(farbe.equalsIgnoreCase("purple")){

        Location teampurple = new Location(328, 227, 396);
        player.teleportTo(teampurple);

                                          }

      if(farbe.equalsIgnoreCase("green")){

        Location teamgreen = new Location(529, 227, 394);
        player.teleportTo(teamgreen);

                                         }

      if(farbe.equalsIgnoreCase("yellow")){

        Location teamyellow = new Location(430, 227, 497);
        player.teleportTo(teamyellow);

                                         }                                       

    }
   }
  }
}



@HookHandler
 public void spielertot(PlayerDeathHook event){

  Player player = event.getPlayer();
  Location loc = player.getLocation();
  double xp = player.getX();
  double zp = player.getZ();
  int x = (int) xp;
  int z = (int) zp;
  World world = loc.getWorld();

  if(z >= 300 && z <= 525 && x > 300 && x < 555 && farm){

  if(teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("purple")){

    int bedlilax = 321; 
    int bedlilay = 228;
    int bedlilaz = 396;
    Block bettlila = world.getBlockAt(bedlilax, bedlilay, bedlilaz);

    if(bettlila.getType() == BlockType.BedBlock){

      Location teleportafterdeath = new Location(328, 227, 396);
      player.setSpawnPosition(teleportafterdeath);
      deathmessage(player);

                                               }

    else{

      spielerscheidetaus(player);

        }
                                                                        }

  if(teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("red")){

    int bedredx = 427; 
    int bedredy = 228;
    int bedredz = 287;
    Block bettred = world.getBlockAt(bedredx, bedredy, bedredz);

    if(bettred.getType() == BlockType.BedBlock){

      Location teleportafterdeath = new Location(427, 227, 294);
      player.setSpawnPosition(teleportafterdeath);
      deathmessage(player);

                                               }

    else{

      spielerscheidetaus(player);

        }
                                                                        }

  if(teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("green")){

    int bedgreenx = 536; 
    int bedgreeny = 228;
    int bedgreenz = 394;
    Block bettgreen = world.getBlockAt(bedgreenx, bedgreeny, bedgreenz);

    if(bettgreen.getType() == BlockType.BedBlock){

      Location teleportafterdeath = new Location(529, 227, 394);
      player.setSpawnPosition(teleportafterdeath);
      deathmessage(player);

                                               }

    else{

      spielerscheidetaus(player);

        }
                                                                        }

  if(teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("yellow")){

    int bedyellowx = 430; 
    int bedyellowy = 228;
    int bedyellowz = 504;
    Block bettred = world.getBlockAt(bedyellowx, bedyellowy, bedyellowz);

    if(bettred.getType() == BlockType.BedBlock){

      Location teleportafterdeath = new Location(430, 227, 497);
      player.setSpawnPosition(teleportafterdeath);
      deathmessage(player);

                                               }

    else{

      spielerscheidetaus(player);

        }
                                                                        }

 } 
}


    @Command(aliases = { "bedwars"},
           description = "Teleportiert den Spieler zur Bedwarsmap.",
           permissions = {""},
           toolTip = "/bedwars maps, or /bedwars map Farm")

  public void teleporttobwmap(MessageReceiver caller, String[] args) {
    
    if (caller instanceof Player) {
      Player player = (Player)caller;

      if(args.length == 1){

        Location bedwarshub = new Location(304, 20, 263);
        player.teleportTo(bedwarshub);
        teamfarbe.put(player.getDisplayName(), "");
        return;

                          }

      if(args.length == 2 && args[1].equalsIgnoreCase("maps")){

      tellbwmaps();

                          }
      if(args[1].equalsIgnoreCase("map") && args[2].equalsIgnoreCase("farm")){

       Location whereNow = new Location(424, 227, 395);
       player.teleportTo(whereNow);

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
      bedwarsstart();
      if(farm){
        Canary.getServer().addSynchronousTask(new BwTaskGold(world));
        Canary.getServer().addSynchronousTask(new BwTaskBronze(world));
        Canary.getServer().addSynchronousTask(new BwTaskSilber(world));
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
      Location loc = player.getLocation();
      World world = loc.getWorld();
      Canary.getServer().addSynchronousTask(new BwTaskGold(world)); 
    }
  }

  @Command(aliases = {"bwbronze"},
           description = "bronzespawner",
           permissions = {"*"},
           toolTip = "/bwbronze")
  public void bwbronzeCommand(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
      Location loc = player.getLocation();
      World world = loc.getWorld();
      createCustomItems();
      SpawnItemsTask bronzeTask = new SpawnItemsTask(customBronze, getBronzeSpawner(), 2, true);
      Canary.getServer().addSynchronousTask(bronzeTask);
    }
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
      Location loc = player.getLocation();
      World world = loc.getWorld();
      Canary.getServer().addSynchronousTask(new BwTaskSilber(world));
    }
  }

  @Command(aliases = { "bwclear" },
          description = "bedwars plugin",
          permissions = { "*" },
          toolTip = "/bwclear")
  public void bwclearCommand(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) { 
      Location loc = player.getLocation();
      World world = loc.getWorld();

      for (int x = 300; x <= 555; x++) {
         for(int y = 212; y <= 254; y++) {
            for (int z = 300; z <= 525; z++) { 

              Block b = world.getBlockAt(x, y, z);

              if(b.getType() == BlockType.SandstoneBlank){

                b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);

              }
            }
           }
         }    
 } 
                                                                          }

public void bettcheck(World world, Player player){
  int i = 0;
  int j = 0;
  int ybett = 228;

  int bettlilax = 321;
  int bettlilaz = 396;

  Block bettlila = world.getBlockAt(bettlilax, ybett, bettlilaz);
  Location locbettlila = new Location(bettlilax, ybett, bettlilaz);

  if(bettlila.getType() == BlockType.BedBlock){ 

        j = j + 1;

  }

  else{

    locbettlila.getWorld().setBlockAt(locbettlila, BlockType.BedBlock);

    i = i + 1;

  }

  int bettgelbx = 430;
  int bettgelbz = 504;

  Block bettgelb = world.getBlockAt(bettgelbx, ybett, bettgelbz);
  Location locbettgelb = new Location(bettgelbx, ybett, bettgelbz);

  if(bettgelb.getType() == BlockType.BedBlock){

    j = j + 1;

  }

  else{

    locbettgelb.getWorld().setBlockAt(locbettgelb, BlockType.BedBlock);
    i = i + 1;
    
  }

  int bettgruenx = 536; 
  int bettgruenz = 394; 

  Block bettgruen = world.getBlockAt(bettgruenx, ybett, bettgruenz);
  Location locbettgruen = new Location(bettgruenx, ybett, bettgruenz);

  if(bettgruen.getType() == BlockType.BedBlock){

    j = j + 1;

  }

  else{

   locbettgruen.getWorld().setBlockAt(locbettgruen, BlockType.BedBlock);
   i = i + 1;
    
  }

  int bettrotx = 427;
  int bettrotz = 287;

  Block bettrot = world.getBlockAt(bettrotx, ybett, bettrotz);
  Location locbettrot = new Location(bettrotx, ybett, bettrotz);

  if(bettrot.getType() == BlockType.BedBlock){

    j = j + 1;

  }

   else{

   locbettrot.getWorld().setBlockAt(locbettrot, BlockType.BedBlock);
   i = i + 1;
    
  }

  if(i >= 1 || j == 4){

    bettclearmessage();
  }
}

@HookHandler
 public void teamauswahl(BlockRightClickHook event){

  Block geklickterblock = event.getBlockClicked();
  Player player = event.getPlayer();
  int x = geklickterblock.getX();
  int y = geklickterblock.getY();
  int z = geklickterblock.getZ();

  if(geklickterblock.getType() == BlockType.WallSign){
    if(x == 283 && y == 20 && z == 263){
      if(voteliste.size() >= 1){
        for(String playername : voteliste){
          if(playername.equalsIgnoreCase(player.getDisplayName())){
            event.setCanceled();
            return;
          }
        }
      }

      farmcounter = farmcounter + 1;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + "Die Map Farm hat " + ChatFormat.GOLD + farmcounter + ChatFormat.DARK_GREEN + " Stimmen.");
      voteliste.add(player.getDisplayName());
    }
  } 

  if(geklickterblock.getType() == BlockType.BedBlock){

    int macht = teamfarbe.size();
    //player.chat("es ist so groß: " + macht); 

    if(teamfarbe.size() > 1){

      spielerliste = Canary.getServer().getPlayerList();

                            }

    if(x == 293 && y == 20){
      if(z == 271 || z == 272){

        event.setCanceled();

        if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("red")){

          if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("red")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

          teamfarbe.put(player.getDisplayName(), "red");
          player.setPrefix(ChatFormat.RED + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.RED + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.RED + "rot" + ChatFormat.DARK_GREEN + ".");

                                                   }

                               }

      if(z == 255 || z == 254){

        event.setCanceled();

        if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("yellow")){

          if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("yellow")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

          teamfarbe.put(player.getDisplayName(), "yellow");
          player.setPrefix(ChatFormat.YELLOW + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.YELLOW + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.YELLOW + "gelb" + ChatFormat.DARK_GREEN + ".");
                                                                              }
                               }
                            }

    if(x == 290 && y == 20){
      if(z == 271 || z == 272){

        event.setCanceled();

        if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("green")){

          if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("green")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

          teamfarbe.put(player.getDisplayName(), "green");
          player.setPrefix(ChatFormat.DARK_GREEN + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.GREEN + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.GREEN + "gruen" + ChatFormat.DARK_GREEN + ".");

                                                                             }

                               }

      if(z == 255 || z == 254){

        event.setCanceled();

        if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("purple")){ 

          if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 283 && xs <= 306 && zs >= 254 && zs <= 272){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("purple")){

                  teamschonvergebenmessage();
                  return;

                                                                                      }
                                                                  } 
                                              }
                                  } 

          teamfarbe.put(player.getDisplayName(), "purple");
          player.setPrefix(ChatFormat.DARK_PURPLE + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_PURPLE + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.DARK_PURPLE + "lila" + ChatFormat.DARK_GREEN + ".");

                                                                              }

                               }
                             }
                                                       }
 }

  public void spielerscheidetaus(Player player){

    Location hub = new Location(251, 71, 262);
    teamfarbe.remove(player.getDisplayName());
    player.setSpawnPosition(hub);
    outofgamemessage(player);

  }

  public void tellbwmaps(){

    String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
    String msg3 = "Farm";

    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + ".");
                          
                          }

  public void bettclearmessage(){

    String msg2 = "Betten wurden geladen.";
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + msg2);

                                }

  public void outofgamemessage(Player player) {

    String msg2 = player.getDisplayName();
    String msg3 = " ist ";
    String msg4 = "ausgeschieden";
    if(teamfarbe.get(msg2).equalsIgnoreCase("red")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.RED + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");

                                                  }

    if(teamfarbe.get(msg2).equalsIgnoreCase("yellow")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.YELLOW + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");

                                                      }

    if(teamfarbe.get(msg2).equalsIgnoreCase("green")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.GREEN + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");

                                                    }

    if(teamfarbe.get(msg2).equalsIgnoreCase("purple")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_PURPLE + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");

                                                  }
                                              }

  public void deathmessage(Player player) {

    String msg2 = player.getDisplayName();
    String msg3 = " ist gestorben.";

    if(teamfarbe.get(msg2).equalsIgnoreCase("red")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.RED + msg2 + ChatFormat.DARK_GREEN + msg3);

                                                  }

    if(teamfarbe.get(msg2).equalsIgnoreCase("yellow")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.YELLOW + msg2 + ChatFormat.DARK_GREEN + msg3);

                                                        }

    if(teamfarbe.get(msg2).equalsIgnoreCase("green")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.GREEN + msg2 + ChatFormat.DARK_GREEN + msg3);

                                                    }

    if(teamfarbe.get(msg2).equalsIgnoreCase("purple")){

      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_PURPLE + msg2 + ChatFormat.DARK_GREEN + msg3);

                                                      }
                                          }

  public void bwcommandmessage(){
    String msg2 = "Zurzeit gibt es folgende Befehle fuer Bedwars: ";
    String msg3 = "/bwgold";
    String msg4 = "/bwsilber";
    String msg5 = "/bwbronze";
    String msg6 = "/bwstart";
    String msg8 = "/bwmap";
  }

  public void teamschonvergebenmessage(){
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + "Diese Farbe ist bereits vergeben.");
  }
}