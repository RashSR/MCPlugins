package events.christmas;
import java.util.ArrayList;
import java.util.Random;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.World;

public class CandyCane extends EZPlugin{
    private World world;
    private int height;

    private BlockType[] material = {BlockType.RedstoneBlock, BlockType.QuartzBlock};
    private int offset;
    private ArrayList<Location> candyStickElements;
    private Location startLocation;
    private final int MINIMUM_HEIGHT = 5;
    private final int DISTANCE_BETWEEN_TRUNKS = 2;

    public CandyCane(int maxHeight, Location startLocation, World world){
        this.world = world;
        Random rand = new Random();
        this.height = rand.nextInt(maxHeight - MINIMUM_HEIGHT) + MINIMUM_HEIGHT;
        this.startLocation = startLocation;
    }
    
    public boolean IsTrunkPlaceable(){
        Random rand = new Random();
        Location[] candyStickTrunk = new Location[height];
        int x = (int)startLocation.getX();
        int y = (int)startLocation.getY();
        int z = (int)startLocation.getZ();

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

    public void Remove(){
        for(Location loc : candyStickElements){
            world.setBlockAt(loc, BlockType.Air);
        }
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

    public static ArrayList<CandyCane> MakeCandySticksInArea(Location groundCorner1, Location groundCorner2, int maxHeight, World world){
        ArrayList<CandyCane> candyCanes = new ArrayList<>();
        world = groundCorner1.getWorld();
        int x, z;
        int quitCond = 0;
        int y = (int) groundCorner2.getY() + 1;
        int[] xzVals = sortCorners(groundCorner1, groundCorner2);
        if(y != groundCorner1.getY() + 1){
            logger.info("[events] Can't make Candy Sticks!");
            return null;
        }
        Random rand = new Random();
        int amount = rand.nextInt(15) + 10;
        logger.info("Ich wuerde " + amount + " Candy Sticks generieren.");
        for(int i = 0; i < amount; i++){
            x = randNumBetween(xzVals[0], xzVals[1]);
            z = randNumBetween(xzVals[2], xzVals[3]);
            CandyCane cc = new CandyCane(maxHeight, new Location(x, y, z), world);
            if(!cc.IsTrunkPlaceable()){
                i--;
                quitCond++;
                if(quitCond > 100){
                    quitCond = 0;
                    break;
                }
            }
        }

        return candyCanes;
    }

    private static int[] sortCorners(Location groundCorner1, Location groundCorner2){
        int[] sortedCorners = new int[4];
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

        for(int i = 0; i <= 100; i++){
            int val = randNumBetween(sortedCorners[0], sortedCorners[1]);
        }

        //returns -> [xMin, xMax, zMin, zMax]
        return sortedCorners;
    }

    private static int randNumBetween(int lowerBound, int upperBound){
        Random rand = new Random();
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
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

    private boolean isPlaceable(int x, int y, int z){
        //Don't place above water
        if(world.getBlockAt(x, y-1, z).getType() == BlockType.Water)
            return false;
        
        //Check area around
        for(int i = -DISTANCE_BETWEEN_TRUNKS; i <= DISTANCE_BETWEEN_TRUNKS; i++){
            for(int j = -DISTANCE_BETWEEN_TRUNKS; j <= DISTANCE_BETWEEN_TRUNKS; j++){
                if(world.getBlockAt(x+i, y, z+j).getType() != BlockType.Air)
                    return false;
            }
        }

        return true; 
    }
}
    