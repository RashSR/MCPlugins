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
import utils.Utils;

import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.world.ExplosionHook;
import net.canarymod.api.entity.EntityType;

public class myfirebow extends EZPlugin implements PluginListener {

  private static boolean isEnabled = false;
  private static float explosionStrength = 1.0f;
  private static final String pluginName = "[Firebow]";

  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler 
  public void ExplosionHookEvent(ExplosionHook event){
    if(isEnabled)
      event.setCanceled();
  }

  @HookHandler
  public void ProjectileHitHookEvent(ProjectileHitHook event){
    if (isEnabled){
      Entity projectile = event.getProjectile();

      if(projectile.getEntityType() == EntityType.ARROW){
        World world = projectile.getWorld();
        Entity victim = event.getEntityHit();
        Location loc = projectile.getLocation();

        world.makeExplosion(victim, loc.getX(), loc.getY(), loc.getZ(), explosionStrength, true);
        projectile.destroy();
      }
    }
  }
  
  @Command(aliases = { "firebow" },
            description = "Enable firebow behavior",
            permissions = { "" },
            toolTip = "/firebow, or /firebow info, or /firebow off, or /firebow commands")
  public void firebowCommand(MessageReceiver caller, String[] args) {
    if(args.length >= 3){
      tomuchargsmessage();
      isEnabled = false;
    }
    else if(args.length == 1 && !isEnabled){
      isEnabled = true;
      activemessage();
      explosionStrength = 1.0f;
    }
    else if(args.length == 1 && isEnabled){
      isEnabled = false;
      inactivemessage();
    }
    else if(args.length == 2){
      String firstUserArgument = args[1];
      if(firstUserArgument.equalsIgnoreCase("off")){
        inactivemessage();
        explosionStrength = 1.0f;
        isEnabled = false;
        return;
      }

      if(firstUserArgument.equalsIgnoreCase("commands")){
        showfirebowcommands();
        return;
      }

      if(firstUserArgument.equalsIgnoreCase("info")){
        if(isEnabled)
          activemessage();
        else
          inactivemessage();
        return;
      }
      
      String userInputExplosionStrength = firstUserArgument;
      float explosionPower = (float)Integer.parseInt(userInputExplosionStrength);
      explosionStrength = explosionPower;

      if(explosionPower == 0){
        invalid0message();
        explosionStrength = 1.0f;
        isEnabled = false;
      }
      else if(explosionPower > 0 && explosionPower <= 10){ 
        isEnabled = true;
        setmessage(explosionPower);
      }
      else if(explosionPower > 10){
        explosionStrength = 1.0f;
        invalid10message();
        isEnabled = false;
      }
    }  
  }

  public void activemessage(){
    String msg2 = "Pfeile sind ";
    String msg3 = "explosiv";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void inactivemessage(){
    String msg2 = "Pfeile sind ";
    String msg3 = "nicht";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + " explosiv.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void setmessage(float explosionPower){
    String msg2 = "Explosionsstaerke wurde auf ";
    String msg3 = " gesetzt.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + (int)explosionPower + ChatFormat.DARK_GREEN + msg3;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void invalid0message(){
    String msg2 = "Falsche Eingabe. Explosionsstaerke muss ";
    String msg3 = "staerker";
    String msg4 = "als";
    String msg5 = " 0 ";
    String msg6 = "sein.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + msg6;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void invalid10message(){
    String msg2 = "Falsche Eingabe. Explosionsstaerke muss ";
    String msg3 = "schwaecher";
    String msg4 = "als";
    String msg5 = " 10 ";
    String msg6 = "sein.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + msg6;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void tomuchargsmessage(){
    String msg2 = "Falsche Eingabe. Zu ";
    String msg3 = "viele";
    String msg4 = " Argumente.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void showfirebowcommands(){
    String msg2 = "Zurzeit gibt es folgende Befehle fuer Firebow: ";
    String msg3 = "/firebow";
    String msg4 = "/firebow off";
    String msg5 = "/firebow <explosionsstaerke>";
    String msg6 = "/firebow info";
    String komma = ChatFormat.DARK_GREEN + ", ";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + 
      komma + ChatFormat.GOLD + msg4 + komma + ChatFormat.GOLD + msg5 + 
      ChatFormat.DARK_GREEN + " und " + ChatFormat.GOLD + msg6 + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }
}