package events;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.World;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.world.LeafDecayHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.BlockUpdateHook;

public class Christmas extends EZPlugin{
  public static Location[] fichtenbleatter = new Location[32];
  public static Location[] lapisblocks = new Location[4];
  public static Location[] goldblocks = new Location[4];
  public static Location[] redstoneblocks = new Location[3];
  public static Location[] schnee = new Location[9];
  public static World world;
  public static boolean weihnachten = false;
  public static boolean snowon = false;

  public static void endChristmas(){
    snowon = false;
    clear();
    dnasnowweg();
    weihnachten = false;
    events.myEvent=null;
    logger.info("Das Event Christmas wird beendet.");
    OwnFileWriter fw = new OwnFileWriter(events.fileName, "no");
  }
  public static void startChristmas(){
    fillChristmasArrays();
    weihnachtsevent();
    weihnachten = true;
    dnamakesnow();
    events.myEvent=EventEnum.CHRISTMAS;
    logger.info("Das Event Christmas wird gestartet.");
    OwnFileWriter fw = new OwnFileWriter(events.fileName, "christmas");
  }

  public static void fillChristmasArrays(){
    fillfichtenarray();
    filllapisarray();
    fillgoldarray();
    fillredstonearray();        
    fillschneearray();
  }

  public static void fillschneearray(){
      schnee[0] = new Location(252, 71, 263);
      world=schnee[0].getWorld();
      schnee[1] = new Location(251, 71, 263);
      schnee[2] = new Location(251, 71, 262);
      schnee[3] = new Location(251, 71, 261);
      schnee[4] = new Location(250, 71, 261);
      schnee[5] = new Location(249, 71, 262);
      schnee[6] = new Location(250, 71, 263);
      schnee[7] = new Location(250, 71, 264);
      schnee[8] = new Location(249, 71, 263);
    }

  public static void fillredstonearray(){
      redstoneblocks[0] = new Location(251, 74, 263);
      redstoneblocks[1] = new Location(243, 73, 265);
      redstoneblocks[2] = new Location(244, 74, 262);
    }

    public static void fillgoldarray(){
      goldblocks[0] = new Location(251, 73, 261);
      goldblocks[1] = new Location(243, 74, 263);
      goldblocks[2] = new Location(245, 73, 260);
      goldblocks[3] = new Location(246, 73, 262);
  }
  
  public static void filllapisarray(){
      lapisblocks[0] = new Location(244, 74, 264);
      lapisblocks[1] = new Location(249, 74, 262);
      lapisblocks[2] = new Location(252, 71, 261);
      lapisblocks[3] = new Location(245, 71, 261);
    }

  public static void fillfichtenarray(){
      fichtenbleatter[0] = new Location(249, 72, 264);
      fichtenbleatter[1] = new Location(250, 73, 261);
      fichtenbleatter[2] = new Location(248, 73, 262);
      fichtenbleatter[3] = new Location(248, 73, 263);
      fichtenbleatter[4] = new Location(252, 72, 262);
      fichtenbleatter[5] = new Location(252, 73, 262);
      fichtenbleatter[6] = new Location(252, 73, 263);
      fichtenbleatter[7] = new Location(252, 74, 263);
      fichtenbleatter[8] = new Location(252, 74, 262);
      fichtenbleatter[9] = new Location(251, 74, 262);
      fichtenbleatter[10] = new Location(250, 74, 262);
      fichtenbleatter[11] = new Location(250, 74, 263);
      fichtenbleatter[12] = new Location(249, 74, 263);
      fichtenbleatter[13] = new Location(250, 75, 263);
      fichtenbleatter[14] = new Location(250, 75, 262);
      fichtenbleatter[15] = new Location(251, 75, 262);
      fichtenbleatter[16] = new Location(251, 75, 263);
      fichtenbleatter[17] = new Location(245, 73, 265);
      fichtenbleatter[18] = new Location(245, 73, 264);
      fichtenbleatter[19] = new Location(244, 74, 263);
      fichtenbleatter[20] = new Location(245, 74, 263);
      fichtenbleatter[21] = new Location(245, 74, 262);
      fichtenbleatter[22] = new Location(242, 73, 263);
      fichtenbleatter[23] = new Location(244, 73, 262);
      fichtenbleatter[24] = new Location(245, 73, 261);
      fichtenbleatter[25] = new Location(244, 74, 261);
      fichtenbleatter[26] = new Location(243, 74, 262);
      fichtenbleatter[27] = new Location(244, 74, 260);
      fichtenbleatter[28] = new Location(243, 73, 260);
      fichtenbleatter[29] = new Location(244, 74, 259);
      fichtenbleatter[30] = new Location(244, 73, 258);
      fichtenbleatter[31] = new Location(246, 73, 263);
    }

  public static void weihnachtsevent(){
      for(int f = 0; f < fichtenbleatter.length; f++){
          world.setBlockAt(fichtenbleatter[f], BlockType.PineLeaves);
          if(f<lapisblocks.length){
            world.setBlockAt(lapisblocks[f], BlockType.LapisBlock);
          }
          if(f<redstoneblocks.length){
            world.setBlockAt(redstoneblocks[f], BlockType.RedstoneBlock);
          }
          if(f<goldblocks.length){
            world.setBlockAt(goldblocks[f], BlockType.GoldBlock);
          }
          if(f<schnee.length){
        world.setBlockAt(schnee[f], BlockType.Snow);
          } 
        }
    }

    public static void makeCandyStick(Location loc){
      for(double i = 0; i < 5; i++){
          if(!((i%2==0))){
            loc.setY(loc.getY() + 1);
            world.setBlockAt(loc, BlockType.QuartzBlock);
          }
        if(i%2 == 0) {
            loc.setY(loc.getY() + 1);
            world.setBlockAt(loc, BlockType.RedstoneBlock);
          }
      }
      loc.setX(loc.getX() + 1);
      world.setBlockAt(loc, BlockType.QuartzBlock);
      loc.setX(loc.getX() + 1);
      world.setBlockAt(loc, BlockType.RedstoneBlock);
      loc.setY(loc.getY() - 1);
      world.setBlockAt(loc, BlockType.QuartzBlock);          
    }

  public static void clear(){
      for(int a = 0; a < fichtenbleatter.length; a++){
          world.setBlockAt(fichtenbleatter[a], BlockType.Air);
          if(a<lapisblocks.length){
            world.setBlockAt(lapisblocks[a], BlockType.Air);
          }
          if(a<redstoneblocks.length){
            world.setBlockAt(redstoneblocks[a], BlockType.Air);
          }
          if(a<goldblocks.length){
            world.setBlockAt(goldblocks[a], BlockType.Air);
          }
          if(a<schnee.length){
        world.setBlockAt(schnee[a], BlockType.Air);
          }
        }
        world.setBlockAt(new Location(252, 71, 261), BlockType.Workbench);
        world.setBlockAt(new Location(245, 71, 261), BlockType.Jukebox);
    }

  public static void dnamakesnow(){
    int zaehler = 0;
    dnasnowweg();
    snowon = true;
    for(int i = 267; i <= 295; i++){
      for(int j = 199; j <= 236; j++){
        Location dnasnow = new Location(i, 18, j);
        Block hoffentlichluft = world.getBlockAt(i, 18, j);
        if(hoffentlichluft.getType() == BlockType.Air && !(world.getBlockAt(i, 17, j).getType() == BlockType.Water)){
          double zufallssnow = Math.random();
          if(zufallssnow < 0.7 && zaehler <= 700){
            world.setBlockAt(dnasnow, BlockType.Snow);
            zaehler = zaehler + 1;
          }
        }
      }
    }
  }

  @HookHandler
  public void schneedarfnedweg(BlockUpdateHook event){
    if(snowon){
      Block verschwindet = event.getBlock();
      BlockType verschwunden = event.getNewBlockType();
      if(verschwindet.getType() == BlockType.Snow  && verschwunden == BlockType.Air){
        event.setCanceled();
      }
    }
  }

  public static void dnasnowweg(){
    for(int a = 267; a <= 295; a++){
      for(int b = 199; b <= 236; b++){    
      Location probe = new Location(a, 18, b);
      if(world.getBlockAt(a, 18, b).getType()==BlockType.Snow){
        world.setBlockAt(probe, BlockType.Air);
        }
      }
    }
  }
  
  @HookHandler
  public void leavesbleiben(LeafDecayHook event){
    if(weihnachten){
      event.setCanceled();
    }
  }

}