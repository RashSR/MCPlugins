package utils;
import java.util.Random;
import net.canarymod.api.world.position.Location;

public enum Map {
    QUIDDITCH(
        new Location(136, 122, 290), //start
        new Location(190, 154, 328), //end
        Utils.QuidditchFieldLocation, //middle
        new Location(Utils.QuidditchFieldLocation.getWorld(), 139.5, 130.5, 309.5, 0f, -90f), //first spawn position on lapis block
        new Location(Utils.QuidditchFieldLocation.getWorld(), 187.5, 130.5, 309.5, 0f, 90f), //second Position
        new Location(244, 55, 270) //high score sign
    ), 
    SNOW(
        new Location(12, 105, 221), 
        new Location(45, 121, 285),
        Utils.SnowMapLocation,
        new Location(29, 108, 231),
        new Location(Utils.SnowMapLocation.getWorld(), 28, 108, 276, 0f, 180f),
        new Location(244, 55, 269)
    ), 
    NETHER(
        new Location(136, 129, 329), 
        new Location(190, 159, 401),
        Utils.NetherMapLocation,
        new Location(163, 148, 335),
        new Location(Utils.NetherMapLocation.getWorld(), 163, 148, 394, 0f, 180f),
        new Location(244, 55, 268)
    ), 
    CHRISTMAS(
        new Location(191, 122, 294), 
        new Location(227, 140, 327), 
        Utils.ChristmasMapLocation,
        new Location(Utils.ChristmasMapLocation.getWorld(), 191, 123, 309, 0f, -90f),
        new Location(Utils.ChristmasMapLocation.getWorld(), 226, 123, 309, 0f, 90f),
        new Location(244, 55, 266)
    ), 
    SHRIEKING_SHACK(
        new Location(136, 132, 260),
        new Location(221, 160, 290), 
        Utils.ShriekingShackLocation,
        new Location(Utils.ShriekingShackLocation.getWorld(), 149, 148, 266, 0f, -70f),
        new Location(Utils.ShriekingShackLocation.getWorld(), 210, 141, 283, 0f, 105f),
        new Location(244, 55, 267)
    );

    private Location startLocation;
    private Location endLocation;
    private Location middleLocation;
    private Location firstPosition;
    private Location secondPosition;
    private Location quidditchPluginHighScoreSign;

    Map(Location start, Location end, Location middle, Location firstPosition, Location secondPosition, Location quidditchPluginHighScoreSign) {
        //TODO: verify if start and end location is correct (first position with lowest x, y and z value)
        this.startLocation = start;
        this.endLocation = end;
        this.middleLocation = middle;
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.quidditchPluginHighScoreSign = quidditchPluginHighScoreSign;
    }

    public static Map GetRandomMap(){
        Map[] maps = Map.values();
        Random random = new Random();
        return maps[random.nextInt(maps.length)];
    }

    public Location GetStartLocation() {
        return startLocation;
    }

    public Location GetEndLocation() {
        return endLocation;
    }

    public Location GetMiddleLocation() {
        return middleLocation;
    }

    public Location GetFirstPositon(){
        return firstPosition;
    }

    public Location GetSecondPosition(){
        return secondPosition;
    }

    public Location GetQuidditchPluginHighScoreSign(){
        return quidditchPluginHighScoreSign;
    }

    public Location GetRandomSpawnPosition(){
        Random random = new Random();
        int number = random.nextInt(2) + 1;
        return GetSpawnPositionByNumber(number);
    }

    public Location GetSpawnPositionByNumber(int index){
        if(index == 1)
            return firstPosition;
        
        return secondPosition;
    }

    public boolean IsLocationInsideMap(Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double minX = Math.min(startLocation.getX(), endLocation.getX());
        double maxX = Math.max(startLocation.getX(), endLocation.getX());
        double minY = Math.min(startLocation.getY(), endLocation.getY());
        double maxY = Math.max(startLocation.getY(), endLocation.getY());
        double minZ = Math.min(startLocation.getZ(), endLocation.getZ());
        double maxZ = Math.max(startLocation.getZ(), endLocation.getZ());

        return (x >= minX && x <= maxX) && (y >= minY && y <= maxY) && (z >= minZ && z <= maxZ);
    }
}