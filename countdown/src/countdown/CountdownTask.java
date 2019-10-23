package countdown;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class CountdownTask extends ServerTask {

    int timeins;
    int i = 0;
    public static boolean gleich = true;
    int anzahlvielfaches;

	public CountdownTask(int myzahl, boolean istan) {

        super(Canary.getServer(), 20, istan);
        timeins = myzahl;

                                             }

    public void run(){

        if(vielfaches30(timeins) > 0 && gleich == false){

            Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + timeins + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.");

                                     }
        
        if(timeins == 10 && gleich == false){

            Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + timeins + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.");

                                            }

        if(timeins <= 5 && timeins > 0 && gleich == false){

            Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Countdown] " + ChatFormat.DARK_GREEN +  "Der Server wird in " + ChatFormat.GOLD + timeins + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.");

                                       }

        if(timeins == 0){

            Canary.getServer().removeSynchronousTask(this);
            Canary.getServer().initiateShutdown(ChatFormat.DARK_GREEN + "Besuchen Sie uns gerne wieder!");

                        }

        if(i == 0){

            gleich = false;

                  }

        i = i + 1;
        timeins = timeins - 1;
    }


    public Integer vielfaches30(int meinezahl){

        anzahlvielfaches = 0;
        while(meinezahl >= 30){

            meinezahl = meinezahl - 30;
            anzahlvielfaches = anzahlvielfaches + 1;

                             }

        if(meinezahl == 0 && anzahlvielfaches > 0){

            return anzahlvielfaches;

                          }

        else{

            return 0;

            }
    } 
}
