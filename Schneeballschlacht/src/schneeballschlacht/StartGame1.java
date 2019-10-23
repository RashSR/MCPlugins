package schneeballschlacht;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
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

public class StartGame1 extends ServerTask{

    public static List<Player> spielerliste = new ArrayList<Player>();
    private Schneeballschlacht plugin;
    private boolean player2;
    int sek = 0; //zählt durchläufe
    int tenseconds=10;
    String msg1 = ChatFormat.DARK_AQUA + "[Schneeballschlacht] ";

    public StartGame1(Schneeballschlacht parentPlugin, boolean myPlayer2) {

        super(Canary.getServer(), 20, true);
        plugin = parentPlugin;
        player2 = myPlayer2;

                                  }

    public void stopStart(){
        Canary.getServer().removeSynchronousTask(this);

     }

     public void run(){
        Location pos1 = new Location(29, 108, 231);
        Location pos2 = new Location(28, 108, 276);
        Location snowhub = new Location(31, 67, 261);
        SoundEffect pling = new SoundEffect(SoundEffect.Type.NOTE_PLING, 31, 67, 261, 2.0f, 3.0f);
        SoundEffect startlevelsound1 = new SoundEffect(SoundEffect.Type.ORB, 29, 108, 231, 3.0f, 3.0f);
        SoundEffect startlevelsound2 = new SoundEffect(SoundEffect.Type.ORB, 28, 108, 276, 3.0f, 3.0f); 
        World world = pos1.getWorld();
        spielerliste = Canary.getServer().getPlayerList();

            if(tenseconds == 10 || tenseconds == 5 || (tenseconds < 4 && tenseconds >0)){
                world.playSound(pling);
                Canary.instance().getServer().broadcastMessage(msg1 + ChatFormat.DARK_GREEN + "Das Spiel beginnt in " + ChatFormat.GOLD + tenseconds + ChatFormat.DARK_GREEN + " Sekunden.");
                          }
            if(tenseconds == 0){
                if(player2){
                    spielerliste.get(1).teleportTo(pos2);
                    world.playSound(startlevelsound2);
                            }   
                spielerliste.get(0).teleportTo(pos1);
                world.playSound(startlevelsound1);
                world.setRaining(true);
                Canary.instance().getServer().broadcastMessage(msg1 + ChatFormat.DARK_GREEN +"Los gehts!");
                Canary.getServer().removeSynchronousTask(this);
        }
        tenseconds--;
     }


}
