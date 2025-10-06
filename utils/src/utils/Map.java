package utils;
import net.canarymod.api.world.position.Location;

public enum Map {
    QUIDDITCH(
        new Location(136, 122, 290), //start
        new Location(190, 154, 328), //end
        Utils.QuidditchFieldLocation //spawn
    ), 
    SNOW(
        new Location(12, 105, 221), 
        new Location(45, 121, 285),
        Utils.SnowMapLocation
    ), 
    NETHER(
        new Location(136, 129, 329), 
        new Location(190, 159, 401),
        Utils.NetherMapLocation
    ), 
    CHRISTMAS(
        new Location(191, 122, 294), 
        new Location(227, 140, 327), 
        Utils.ChristmasMapLocation
    ), 
    SHRIEKING_SHACK(
        new Location(136, 132, 260),
        new Location(221, 160, 290), 
        Utils.ShriekingShackLocation
    );

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