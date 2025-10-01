package countdown;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.tasks.ServerTask;
import utils.Utils;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;

public class CountdownTask extends ServerTask {

    private int timeInSeconds;
    private boolean isFirstRun = true;
    private static final boolean isContinousTask = true;

	public CountdownTask(int timeInSeconds) {
        super(Canary.getServer(), 1 * Utils.TICKS_PER_SECOND, isContinousTask);
        this.timeInSeconds = timeInSeconds;
    }

    public void run(){
        if (!isFirstRun && (multipleOf30(timeInSeconds) > 0 || timeInSeconds == 10 || (timeInSeconds <= 5 && timeInSeconds > 0) )) 
        {
            String serverMessage = ChatFormat.DARK_GREEN + "Der Server wird in " + ChatFormat.GOLD + 
                timeInSeconds + ChatFormat.DARK_GREEN + " Sekunden heruntergefahren.";
            Utils.BroadcastServerMessage(countdown.pluginName, serverMessage);
        }
        else if(timeInSeconds == 0){
            Canary.getServer().removeSynchronousTask(this);
            Canary.getServer().initiateShutdown(ChatFormat.DARK_GREEN + "Besuchen Sie uns gerne wieder!");
        }

        isFirstRun = false; 
        timeInSeconds--;
    }


    public Integer multipleOf30(int number){
        int countOfMultiples = 0;

        while(number >= 30){
            number = number - 30;
            countOfMultiples = countOfMultiples + 1;
        }

        if(number == 0 && countOfMultiples > 0)
            return countOfMultiples;
        
        return 0;     
    } 
}
