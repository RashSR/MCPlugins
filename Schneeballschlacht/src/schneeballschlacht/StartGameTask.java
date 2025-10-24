package schneeballschlacht;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.chat.ChatFormat;
import java.util.ArrayList;
import java.util.List;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import utils.Utils;

public class StartGameTask extends ServerTask{
    private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;

    private Schneeballschlacht parentPlugin;
    private boolean has2Players;
    private int elapsedTimeInSeconds;
    private int teleportTimeInSeconds;

    public StartGameTask(Schneeballschlacht parentPlugin, boolean has2Players, int teleportTimeInSeconds){
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.parentPlugin = parentPlugin;
        this.has2Players = has2Players;
        this.teleportTimeInSeconds = teleportTimeInSeconds;
        this.elapsedTimeInSeconds = 0;
    }

    public void stopStart(){
        Canary.getServer().removeSynchronousTask(this);
     }

     public void run(){
        elapsedTimeInSeconds++;

        Location spawnLocationPlayer1 = new Location(29, 108, 231);
        Location spawnLocationPlayer2 = new Location(28, 108, 276);
        SoundEffect plingSoung = new SoundEffect(SoundEffect.Type.NOTE_PLING, 31, 67, 261, 2.0f, 3.0f);
        SoundEffect startSoundPlayer1 = new SoundEffect(SoundEffect.Type.ORB, 29, 108, 231, 3.0f, 3.0f);
        SoundEffect startSoundPlayer2 = new SoundEffect(SoundEffect.Type.ORB, 28, 108, 276, 3.0f, 3.0f); 
        World world = spawnLocationPlayer1.getWorld();
        List<Player> playerList = Canary.getServer().getPlayerList();

        int remainingTimeInSeconds = teleportTimeInSeconds - elapsedTimeInSeconds;
        if(remainingTimeInSeconds == 10 || remainingTimeInSeconds == 5 || (remainingTimeInSeconds > 0 && remainingTimeInSeconds < 4)){
            world.playSound(plingSoung);
            Utils.BroadcastServerMessage(Schneeballschlacht.pluginName, "Das Spiel beginnt in " + ChatFormat.GOLD + remainingTimeInSeconds + ChatFormat.DARK_GREEN + " Sekunden.");
        }
        else if(elapsedTimeInSeconds >= teleportTimeInSeconds){
            teleportPlayerToLocation(playerList.get(0), spawnLocationPlayer1, startSoundPlayer1);

            if(has2Players)
                teleportPlayerToLocation(playerList.get(1), spawnLocationPlayer2, startSoundPlayer2);

            world.setRaining(true);
            Utils.BroadcastServerMessage(Schneeballschlacht.pluginName, "Los gehts!");
            Canary.getServer().removeSynchronousTask(this);
        }
    }

    private void teleportPlayerToLocation(Player player, Location location, SoundEffect soundEffect){
        player.teleportTo(location);
        location.getWorld().playSound(soundEffect);
    }
}
