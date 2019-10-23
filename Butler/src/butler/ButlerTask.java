package butler;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.tasks.ServerTask;
import net.canarymod.chat.ChatFormat;

public class ButlerTask extends ServerTask {
  
  private Player sir;
  private Player butler;
  int i = 0;
  String msg1 = "[Butler/Sir] ";

  public ButlerTask(Player mysir, Player mybutler) {

        super(Canary.getServer(), 5 * 20, true);
        sir = mysir;
        butler = mybutler;

                                             }

  public void run(){

    i = i + 1;

    if(i < 60){

        int distance = distance2player(sir, butler);
        String msg2 = "Die Distanz zwischen Sir und Butler betraegt ";
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + distance + ChatFormat.DARK_GREEN + " Meter.");

        if (distance > 10){

           if(butler.getHealth() > 4){

           butler.setHealth(butler.getHealth() - 4);

                                     }

           if(butler.getHealth() <= 4){

             Canary.getServer().removeSynchronousTask(this);

                                      }
                           }

        if(i%12 == 0){

              int minute = 5 - (i/12);
              String min = "";

               if((i/12) < 4){

                 min = " Minuten";

                            }

                if((i/12) > 4){

                  min = " Minute";

                            }

              Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Der Butler muss nur noch fuer " + ChatFormat.GOLD + minute + ChatFormat.DARK_GREEN + min + " durchhalten.");

                          }
                         
                 }

    if(i >= 60){

       Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Der Butler " + ChatFormat.BLUE + butler.getDisplayName() + ChatFormat.DARK_GREEN + " konnte seinem Sir treu dienen!");
       Canary.getServer().removeSynchronousTask(this);

               }
                }

  public Integer distance2player(Player sir, Player butler){

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
