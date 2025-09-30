package butler;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;

public class ButlerTask extends ServerTask {
  
  private static final int TICKS_PER_SECOND = 20;
  private static final int MAX_PLAYTIME_CYCLES = 60;
  private static final int MAX_BLOCK_DISTANCE = 10;
  private int playtimeCycle = 0;
  private Player sir;
  private Player butler;

  //the delay before executing. Set to 0 or less to run within the next Server tick If delay is 0 or less, the task will run with each server tick
  private static final long TaskDelay = 5 * TICKS_PER_SECOND; //20 Ticks -> 1 second -> repeats all 5 Seconds
  private static final boolean IsContinousTask = true;

  public ButlerTask(Player sir, Player butler) {
    super(Canary.getServer(), TaskDelay, IsContinousTask);
    this.sir = sir;
    this.butler = butler;
  }

  public void run(){
    playtimeCycle++;

    if(playtimeCycle < MAX_PLAYTIME_CYCLES){
      int distance = calculateDistanceBetweenPlayers(sir, butler);
      String msg2 = "Die Distanz zwischen Sir und Butler betraegt ";
      String serverMessage = Butler.msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD +
        distance + ChatFormat.DARK_GREEN + " Meter.";
      Canary.instance().getServer().broadcastMessage(serverMessage);

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

        serverMessage = Butler.msg1 + ChatFormat.DARK_GREEN + "Der Butler muss nur noch fuer " + 
          ChatFormat.GOLD + remainingTime + ChatFormat.DARK_GREEN + minuteLabel + " durchhalten.";
        Canary.instance().getServer().broadcastMessage(serverMessage);
      }                
    }

    if(playtimeCycle >= MAX_PLAYTIME_CYCLES){
      String serverMessage = Butler.msg1 + ChatFormat.DARK_GREEN + "Der Butler " + ChatFormat.BLUE + 
        butler.getDisplayName() + ChatFormat.DARK_GREEN + " konnte seinem Sir treu dienen!";
      Canary.instance().getServer().broadcastMessage(serverMessage);
      Canary.getServer().removeSynchronousTask(this);
    }
  }

  public Integer calculateDistanceBetweenPlayers(Player sir, Player butler){
    double xs = sir.getX();
    double ys = sir.getY();
    double zs = sir.getZ();
    double xb = butler.getX();
    double yb = butler.getY();
    double zb = butler.getZ();
    double d = Math.sqrt((xs - xb)*(xs - xb) + (ys - yb)*(ys - yb) + (zs - zb)*(zs - zb));
    int distance = (int)d;
    return distance;
  }
}
