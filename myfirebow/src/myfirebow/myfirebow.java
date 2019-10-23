package myfirebow; 

import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.entity.ProjectileHitHook;
import net.canarymod.plugin.PluginListener;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.ExplosionHook;



public class myfirebow extends EZPlugin implements PluginListener {

  public static boolean enabled = false;
  public static float explosionsstärke = 1.0f;
  String msg1 = "[Firebow] ";

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler 
  public void explosionaufhalten(ExplosionHook event){

    if(enabled){

      event.setCanceled();

    }
  }

  @HookHandler
  public void pfeiltrifft(ProjectileHitHook event){

    if (enabled){

    Entity arrow = event.getProjectile();
    World world = arrow.getWorld();
    Entity opfer = event.getEntityHit();
    Location loc = arrow.getLocation();

    world.makeExplosion(opfer, loc.getX(), loc.getY(), loc.getZ(), explosionsstärke, true);

    arrow.destroy();

   }
  }
  
  @Command(aliases = { "firebow" },
            description = "Enable firebow behavior",
            permissions = { "" },
            toolTip = "/firebow, or /firebow info, or /firebow off, or /firebow commands")
  public void firebowCommand(MessageReceiver caller, String[] args) {

    if (caller instanceof Player) { 

      Player me = (Player)caller;

      if(args.length >= 3){

        tomuchargsmessage();
        enabled = false;

                          }

      if(args.length == 1 && enabled == false){

        enabled = true;
        activemessage();
        explosionsstärke = 1.0f;
        return;

                          }

      if(args.length == 1 && enabled == true){

        enabled = false;
        inactivemessage();
        return;

                                             }

     if(args.length == 2){

      if(args[1].equalsIgnoreCase("off")){

        inactivemessage();
        explosionsstärke = 1.0f;
        enabled = false;
        return;

                                         }

      if(args[1].equalsIgnoreCase("commands")){

        showfirebowcommands();
        return;

                                              }

      if(args[1].equalsIgnoreCase("info")){

       if(enabled){

        activemessage();
        return;

                   }

       else{

        inactivemessage();
        return;

           }
                                           }

      String expstärke = args[1];
      int expstaerke = Integer.parseInt(expstärke);
      float explosionpower = (float) expstaerke;
      explosionsstärke = explosionpower;

      if(explosionpower == 0){

        invalid0message();
        explosionsstärke = 1.0f;
        enabled = false;
        return;

      }

      if(explosionpower > 0 && explosionpower <= 10){ 

        enabled = true;
        setmessage(expstaerke);
        return;

      }

      if(explosionpower > 10){

        explosionsstärke = 1.0f;
        invalid10message();
        enabled = false;
        return;
      }
   }
  }   
 }

 public void activemessage(){

  String msg2 = "Pfeile sind ";
  String msg3 = "explosiv";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + ".");

 }

 public void inactivemessage(){

  String msg2 = "Pfeile sind ";
  String msg3 = "nicht";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + " explosiv.");
 }

 public void setmessage(int explosionszahl){

  String msg2 = "Explosionsstaerke wurde auf ";
  String msg3 = " gesetzt.";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + explosionszahl + ChatFormat.DARK_GREEN + msg3);

 }

 public void invalid0message(){

  String msg2 = "Falsche Eingabe. Explosionsstaerke muss ";
  String msg3 = "staerker";
  String msg4 = "als";
  String msg5 = " 0 ";
  String msg6 = "sein.";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + msg6);

 }

public void invalid10message(){

  String msg2 = "Falsche Eingabe. Explosionsstaerke muss ";
  String msg3 = "schwaecher";
  String msg4 = "als";
  String msg5 = " 10 ";
  String msg6 = "sein.";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + msg6);

 }

 public void tomuchargsmessage(){

  String msg2 = "Falsche Eingabe. Zu ";
  String msg3 = "viele";
  String msg4 = " Argumente.";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);
 }

 public void showfirebowcommands(){

   String msg2 = "Zurzeit gibt es folgende Befehle fuer Firebow: ";
   String msg3 = "/firebow";
   String msg4 = "/firebow off";
   String msg5 = "/firebow <explosionsstaerke>";
   String msg6 = "/firebow info";
   String komma = ", ";


   Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + " und " + ChatFormat.GOLD + msg6 + ChatFormat.DARK_GREEN + ".");
 }
}