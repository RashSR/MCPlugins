package schneeballschlacht;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
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

public class StartGame1 extends ServerTask{

    private List<Player> playerList;
    private Schneeballschlacht parentPlugin;
    private boolean player2;
    int tenseconds = 10;
    private final String pluginName = "[Schneeballschlacht]";

    public StartGame1(Schneeballschlacht parentPlugin, boolean player2){
        super(Canary.getServer(), 20, true);
        this.parentPlugin = parentPlugin;
        this.player2 = player2;
        this.playerList = new ArrayList<Player>();
    }

    public void stopStart(){
        Canary.getServer().removeSynchronousTask(this);
     }

     public void run(){
        Location pos1 = new Location(29, 108, 231);
        Location pos2 = new Location(28, 108, 276);
        Location snowballAreanHubLocation = new Location(31, 67, 261);
        SoundEffect pling = new SoundEffect(SoundEffect.Type.NOTE_PLING, 31, 67, 261, 2.0f, 3.0f);
        SoundEffect startlevelsound1 = new SoundEffect(SoundEffect.Type.ORB, 29, 108, 231, 3.0f, 3.0f);
        SoundEffect startlevelsound2 = new SoundEffect(SoundEffect.Type.ORB, 28, 108, 276, 3.0f, 3.0f); 
        World world = pos1.getWorld();
        playerList = Canary.getServer().getPlayerList();

        if(tenseconds == 10 || tenseconds == 5 || (tenseconds < 4 && tenseconds > 0)){
                world.playSound(pling);
                Utils.BroadcastServerMessage(pluingName, "Das Spiel beginnt in " + ChatFormat.GOLD + tenseconds + ChatFormat.DARK_GREEN + " Sekunden.");
        }
        else if(tenseconds == 0){
            if(player2){
                playerList.get(1).teleportTo(pos2);
                world.playSound(startlevelsound2);
            }

            playerList.get(0).teleportTo(pos1);
            world.playSound(startlevelsound1);
            world.setRaining(true);
            Utils.BroadcastServerMessage(pluginName, "Los gehts!");
            Canary.getServer().removeSynchronousTask(this);
        }
        tenseconds--;
     }
}
