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
import net.canarymod.hook.world.TimeChangeHook;
import java.util.Random;
import utils.Utils;
import java.util.ArrayList;

public class ChristmasEvent extends EZPlugin implements IEvent{
    public static Location[] fichtenbleatter = new Location[32];
    public static Location[] lapisblocks = new Location[4];
    public static Location[] goldblocks = new Location[4];
    public static Location[] redstoneblocks = new Location[3];
    public static Location[] schnee = new Location[9];

    private static ArrayList<Location> dnaSnow;
    private static ArrayList<Location> candyStickElements;

    public static World world;
    private static Random rand = new Random();
    private static BlockType[] material = {BlockType.RedstoneBlock, BlockType.QuartzBlock};
    private static int offset;
    private boolean isRunning = false;

    public ChristmasEvent(World world){
        this.world = world;
    }

    public void endEvent(){
        removeCandySticks();
        clear();
        removeSnow();
        logger.info("Das Event Christmas wird beendet.");
        isRunning = false;
        Utils.WriteToEventFile("no");
    }

    public void startEvent(){
        makeCandySticks(new Location(267, 17, 227), new Location(295, 17, 199), 30);
        fillChristmasArrays();
        weihnachtsevent();
        isRunning = true;
        dnaMakeSnow();
        logger.info("Das Event Christmas wird gestartet.");
        Utils.WriteToEventFile("christmas");
    }

    public EventType getEventType(){
		return EventType.CHRISTMAS;
	}

    private void makeCandySticks(Location groundCorner1, Location groundCorner2, int maxHeight){
        candyStickElements = new ArrayList<>();
        world = groundCorner1.getWorld();
        int x, z;
        int quitCond = 0;
        int y = (int) groundCorner2.getY() + 1;
        int[] xzVals = sortCorners(groundCorner1, groundCorner2);
        if(y != groundCorner1.getY() + 1){
            logger.info("[events] Can't make Candy Sticks!");
            return;
        }
        int amount = rand.nextInt(15) + 10;
        logger.info("Ich wuerde " + amount + " Candy Sticks generieren.");
        for(int i = 0; i < amount; i++){
            x = randNumBetween(xzVals[0], xzVals[1]);
            z = randNumBetween(xzVals[2], xzVals[3]);
            if(!makeTrunk(maxHeight, x, y, z) ){
                i--;
                quitCond++;
                if(quitCond > 100){
                    quitCond = 0;
                    break;
                }
            }
        }
    }

    private boolean makeTrunk(int maxHeight, int x, int y, int z){
        int height = rand.nextInt(maxHeight - 5) + 5;
        Location[] candyStickTrunk = new Location[height];
        for(int i = 0; i < candyStickTrunk.length; i++){
            if(!isPlaceable(x, y+i, z)){
                return false;
            }
            candyStickTrunk[i] = new Location(x, i+y, z);
        }
        offset = rand.nextInt(2);
        if(makeCone(candyStickTrunk[candyStickTrunk.length - 1], height)){
            for(int i = 0; i < candyStickTrunk.length; i++){
                candyStickElements.add(candyStickTrunk[i]);
                world.setBlockAt(candyStickTrunk[i], material[(i+offset) % 2]);
            }
            return true;
        }
        return false;
    }

    private boolean isPlaceable(int x, int y, int z){
        int distBetweenTrunks = 2;
        for(int i = -distBetweenTrunks; i <= distBetweenTrunks; i++){
            for(int j = -distBetweenTrunks; j <= distBetweenTrunks; j++){
                if(world.getBlockAt(x+i, y, z+j).getType() != BlockType.Air){
                    return false;
                }
            }
        }

        if(world.getBlockAt(x, y-1, z).getType() == BlockType.Water){
            return false;
        }
        if(world.getBlockAt(x, y, z).getType() == BlockType.Air){
                return true;
        }
        return false;
        
    }

    private boolean makeCone(Location highestBlock, int height){
        int len = (height/4)+1;
        Location[] candyStickCone;
        Location slab = null;
        if(len < 4){
            logger.info("Hier ist es kleiner");
            candyStickCone = new Location[3];
            candyStickCone[0] = new Location(highestBlock.getX()+1, highestBlock.getY()+1, highestBlock.getZ());
            candyStickCone[1] = new Location(highestBlock.getX()+2, highestBlock.getY()+1, highestBlock.getZ());
            candyStickCone[2] = new Location(highestBlock.getX()+3, highestBlock.getY(), highestBlock.getZ());
            for(int i = 0; i < candyStickCone.length; i++){
                if(world.getBlockAt(candyStickCone[i]).getType() != BlockType.Air){
                    return false;
                }
            }
            slab = new Location(highestBlock.getX(), highestBlock.getY()+1, highestBlock.getZ());
            world.setBlockAt(slab, BlockType.QuartzSlab);
        }else{
            candyStickCone = new Location[len];
            for(int i = 0; i <= len-1; i++){
                Location loc = new Location(highestBlock.getX()+i+1, highestBlock.getY()+1, highestBlock.getZ());
                candyStickCone[i] = new Location(0, 0, 0);
                if(world.getBlockAt(loc).getType() == BlockType.Air){
                    candyStickCone[i] = loc;
                }
            }
        }
        
        for(int i = 0; i < candyStickCone.length; i++){
            candyStickElements.add(candyStickCone[i]);
            world.setBlockAt(candyStickCone[i], material[(i+offset+1) % 2]);
        }
        if(slab != null){
            candyStickElements.add(slab);
        }
        return true;
    }

    private void removeCandySticks(){
        for(Location loc : candyStickElements){
            world.setBlockAt(loc, BlockType.Air);
        }
    }

    private int[] sortCorners(Location groundCorner1, Location groundCorner2){
        int[] sortedCorners = new int[4];
        //logger.info("GC1.X = " + groundCorner1.getX() + ", GC1.Z = " + groundCorner1.getZ() + ", GC2.X = " + groundCorner2.getX() + ", GC2.Z = " + groundCorner2.getZ());
        if(groundCorner1.getX() <= groundCorner2.getX()){
            sortedCorners[0] = (int)groundCorner1.getX();
            sortedCorners[1] = (int)groundCorner2.getX();
        }else{
            sortedCorners[0] = (int)groundCorner2.getX();
            sortedCorners[1] = (int)groundCorner1.getX();
        }
        if(groundCorner1.getZ() <= groundCorner2.getZ()){
            sortedCorners[2] = (int)groundCorner1.getZ();
            sortedCorners[3] = (int)groundCorner2.getZ();
        }else{
            sortedCorners[2] = (int)groundCorner2.getZ();
            sortedCorners[3] = (int)groundCorner1.getZ();
        }
        //logger.info("Sorted Values: X1 = " + sortedCorners[0] + ", X2 = " + sortedCorners[1] + ", Z1 = " + sortedCorners[2] + ", Z2 = " + sortedCorners[3]);
        for(int i = 0; i <= 100; i++){
            int val = randNumBetween(sortedCorners[0], sortedCorners[1]);
            //ogger.info("Ich generiere Zahlen zwischen " + sortedCorners[0] + " und " + sortedCorners[1] + ": " + val);
        }
        return sortedCorners;
    }

    private int randNumBetween(int lowerBound, int upperBound){
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    } 

    public void makeCandyStick(Location loc){
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

    public void dnaMakeSnow(){
        //TODO darf nicht mittelblock sein LOC -> 281, 18, 213
        dnaSnow = new ArrayList<>();
        int zaehler = 0;
        for(int i = 267; i <= 295; i++){
            for(int j = 199; j <= 236; j++){
                Location dnasnow = new Location(i, 18, j);
                Block hoffentlichluft = world.getBlockAt(i, 18, j);
                if(hoffentlichluft.getType() == BlockType.Air && !(world.getBlockAt(i, 17, j).getType() == BlockType.Water)){
                    double zufallssnow = Math.random();
                    if(zufallssnow < 0.8 && zaehler <= 1000){
                        if(!(dnasnow.getX() == 281 && dnasnow.getZ() == 213)){
                            world.setBlockAt(dnasnow, BlockType.Snow);
                            zaehler = zaehler + 1;
                            dnaSnow.add(dnasnow);
                        }
                    }
                }
            }
        }
        stopSnowMelt s = new stopSnowMelt();
        s.start();
    }

    public class stopSnowMelt extends Thread{
        public void run(){
            while(isRunning){
                for(Location loc : dnaSnow){
                    if(world.getBlockAt(loc).getType() != BlockType.Snow){
                        if(isRunning){
                            world.setBlockAt(loc, BlockType.Snow);
                        }
                    }
                }
            }
        }
    }

    public void removeSnow(){
        for(Location loc : dnaSnow){
            world.setBlockAt(loc, BlockType.Air);
        }
    }
    
    public void clear(){
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

    @HookHandler
    public void stopLeafDecay(LeafDecayHook event){
        if(isRunning)
            event.setCanceled();
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

    public void weihnachtsevent(){
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
}