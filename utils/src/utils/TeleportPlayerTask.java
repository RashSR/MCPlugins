package utils;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;

public class TeleportPlayerTask extends ServerTask{
    private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;

    private Player player;
    private Location destinationLocation;
    private int delayInSeconds;
    private int passedSeconds = 1;

    public TeleportPlayerTask(Player player, Location destinationLocation, int delayInSeconds) {
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.player = player;
        this.destinationLocation = destinationLocation;
        this.delayInSeconds = delayInSeconds;
    }

    public void run(){
        if(passedSeconds >= delayInSeconds){
            player.teleportTo(destinationLocation);
            Canary.getServer().removeSynchronousTask(this);
        }
        
        passedSeconds++;
    }
}
