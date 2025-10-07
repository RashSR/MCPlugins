package utils;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.effects.SoundEffect;

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
        Utils.setPlayerLevel(player, delayInSeconds);
        Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.NOTE_BASS, 1.0f, 1.0f);
    }

    public void run(){
        if(passedSeconds >= delayInSeconds){
            player.teleportTo(destinationLocation);
            
            //make sure player hears the soundeffects
            Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.NOTE_PLING, 1.0f, 1.0f);
            Utils.playSoundAtLocation(destinationLocation, SoundEffect.Type.NOTE_PLING, 1.0f, 1.0f);
            Canary.getServer().removeSynchronousTask(this);
        }
        
        Utils.setPlayerLevel(player, delayInSeconds - passedSeconds);
        passedSeconds++;
        if(delayInSeconds - passedSeconds > -1)
            Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.NOTE_BASS, 1.0f, 1.0f);
    }
}
