package butler;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;
import utils.Utils;

public class ButlerTask extends ServerTask {

  private static final int MAX_PLAYTIME_CYCLES = 60;
  private static final int MAX_BLOCK_DISTANCE = 10;
  private int playtimeCycle = 0;
  private Player sir;
  private Player butler;

  //the delay before executing. Set to 0 or less to run within the next Server tick If delay is 0 or less, the task will run with each server tick
  private static final long TaskDelay = 5 * Utils.TICKS_PER_SECOND; //20 Ticks -> 1 second -> repeats all 5 Seconds
  private static final boolean isContinousTask = true;

  public ButlerTask(Player sir, Player butler) {
    super(Canary.getServer(), TaskDelay, isContinousTask);
    this.sir = sir;
    this.butler = butler;
  }

  public void run(){
    playtimeCycle++;

    if(playtimeCycle < MAX_PLAYTIME_CYCLES){
      int distance = Utils.CalculateDistanceBetweenPlayers(sir, butler);
      String msg2 = "Die Distanz zwischen Sir und Butler betraegt ";
      String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD +
        distance + ChatFormat.DARK_GREEN + " Meter.";
      Utils.BroadcastServerMessage(Butler.pluginName, serverMessage);

      if (distance > MAX_BLOCK_DISTANCE){
          if(butler.getHealth() > 4)
            butler.setHealth(butler.getHealth() - 4);
          else
            Canary.getServer().removeSynchronousTask(this);
      }

      if(playtimeCycle%12 == 0){
        int remainingTime = MAX_PLAYTIME_CYCLES/12 - (playtimeCycle/12);
        String minuteLabel = "";

        if((playtimeCycle/12) < 4)
          minuteLabel = " Minuten";

        if((playtimeCycle/12) > 4)
          minuteLabel = " Minute";

        serverMessage = ChatFormat.DARK_GREEN + "Der Butler muss nur noch fuer " + 
          ChatFormat.GOLD + remainingTime + ChatFormat.DARK_GREEN + minuteLabel + " durchhalten.";
        Utils.BroadcastServerMessage(Butler.pluginName, serverMessage);
      }                
    }

    if(playtimeCycle >= MAX_PLAYTIME_CYCLES){
      String serverMessage = ChatFormat.DARK_GREEN + "Der Butler " + ChatFormat.BLUE + 
        butler.getDisplayName() + ChatFormat.DARK_GREEN + " konnte seinem Sir treu dienen!";
      Utils.BroadcastServerMessage(Butler.pluginName, serverMessage);
      Canary.getServer().removeSynchronousTask(this);
    }
  }
}
