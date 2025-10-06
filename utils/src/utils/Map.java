package utils;
import net.canarymod.api.world.position.Location;

public enum Map {
    QUIDDITCH(
        new Location(136, 122, 290), //start
        new Location(190, 154, 328), //end
        Utils.QuidditchFieldLocation //spawn
    ), 
    SNOW(
        null, 
        null, 
        Utils.SnowMapLocation
    ), 
    NETHER(null, null, null), 
    CHRISTMAS(null, null, null), 
    SHRIEKING_SHACK(null, null, null);

    private final Location startLocation;
    private final Location endLocation;
    private final Location spawnLocation;

    Map(Location start, Location end, Location spawn) {
        this.startLocation = start;
        this.endLocation = end;
        this.spawnLocation = spawn;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}