package quidditch;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.entity.ProjectileHitHook;
import net.canarymod.api.entity.Entity;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Enchantment;
import net.canarymod.api.inventory.Enchantment.Type;
import net.canarymod.api.inventory.Inventory;
import utils.Utils;

public class Quidditch extends EZPlugin implements PluginListener {

  private BlockType snitchBlockType = BlockType.GoldBlock;
  int i = 1;
  int score = 0;
  private final String pluginName = "[Quidditch]";
  private boolean isEnabled = false;
  Inventory playerinv;
  
  @Override 
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }
  
  @Command(aliases = { "quidditch" },
            description = "quidditch plugin",
            permissions = { "*" },
            toolTip = "/quidditch schnatz")
  public void quidditchschnatzCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player) {
      if(args.length == 2){
        if(args[1].equalsIgnoreCase("schnatz")){
            displayStartMessage();
            placeSnitch();
            giveEquipToPlayer(player);
            isEnabled = true;
        }
      }
    }
  }

  private void giveEquipToPlayer(Player player){
    playerinv = player.getInventory();

    ItemFactory factory = Canary.factory().getItemFactory();
    Item infinitybogen = factory.newItem(ItemType.Bow);
    short f = 1;

    ItemFactory infinityfactory = Canary.factory().getItemFactory();
    Enchantment infinity = infinityfactory.newEnchantment(Enchantment.Type.Infinity, f);

    infinitybogen.setDisplayName(ChatFormat.GOLD + "Schnatzfaenger");
    infinitybogen.addEnchantments(infinity);

    playerinv.setSlot(1, infinitybogen);
    playerinv.setSlot(ItemType.Arrow, 1, 8);
    player.setModeId(Utils.ADVENTURE_MODE);
  }

  private void placeSnitch(){
    boolean solangkeinluftblock = true;

    double anfangswertx = 136;
    double anfangswerty = 122;
    double anfangswertz = 290;
    double endwertx = 190;
    double endwerty = 154;
    double endwertz = 328;

    while(solangkeinluftblock){
      double schnatzx = anfangswertx + Math.random() * (endwertx - anfangswertx);
      double schnatzy = anfangswerty + Math.random() * (endwerty - anfangswerty);
      double schnatzz = anfangswertz + Math.random() * (endwertz - anfangswertz);

      int xschnatz = (int)schnatzx;
      int yschnatz = (int)schnatzy;
      int zschnatz = (int)schnatzz;

      Location snitch = new Location(schnatzx, schnatzy, schnatzz);
      Block temporarySnitch = snitch.getWorld().getBlockAt(xschnatz, yschnatz, zschnatz);

      if(temporarySnitch.getType() == BlockType.Air){
        snitch.getWorld().setBlockAt(snitch, snitchBlockType);
        solangkeinluftblock = false;
      }
    }
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    if(isEnabled){
      Block geklickterblock = event.getBlockClicked();
      Location loc = geklickterblock.getLocation();
      World world = loc.getWorld();
      int rechtsklickzahl = 1;
      Player player = event.getPlayer();

      if(geklickterblock.getType() == snitchBlockType){
        world.setBlockAt(geklickterblock.getLocation(), BlockType.Air);
        i = i + 1;
        score = score + 150;
        if(i < 11){
          placeSnitch();
          displayScoreMessage(rechtsklickzahl);
        }

        if(i >= 11){
          displayScoreMessage(rechtsklickzahl);
          playerinv = player.getInventory();
          playerinv.removeItem(ItemType.Bow);
          displayWinnerMessage(player);
          i = 1;
          return;
        }
      }
    } 
  }

  @HookHandler
  public void schnatzmitbogengetroffen(ProjectileHitHook event){
    if(isEnabled){
      Entity arrow = event.getProjectile();
      World world = arrow.getWorld();
      Location loc = arrow.getLocation();
      int bogengetroffenzahl = 2;
      arrow.destroy();
      Player player = world.getClosestPlayer(loc.getX(), loc.getY(), loc.getZ(), 100);

      int x = (int)loc.getX();
      int y = (int)loc.getY();
      int z = (int)loc.getZ();

      int zaehlx = x + 3;
      int zahely = y + 3;
      int zahelz = z + 3;

      for (int scanx = x - 3; scanx <= zaehlx ; scanx++) {
        for (int scany = y - 3; scany <= zahely ; scany++){
          for (int scanz = z - 3; scanz <= zahelz ; scanz++){
            Block vorlauefigerschnatz = world.getBlockAt(scanx, scany, scanz);

            if(vorlauefigerschnatz.getType() == snitchBlockType){
              double betrag = (vorlauefigerschnatz.getX() + 0.5 - loc.getX()) * (vorlauefigerschnatz.getX() + 0.5 - loc.getX()) + (vorlauefigerschnatz.getY() + 0.5 - loc.getY()) * (vorlauefigerschnatz.getY() + 0.5 - loc.getY()) + (vorlauefigerschnatz.getZ() + 0.5 - loc.getZ()) * (vorlauefigerschnatz.getZ() + 0.5 - loc.getZ());
              double abstand = Math.sqrt(betrag);

              if(abstand <= 3.5){
                world.setBlockAt(vorlauefigerschnatz.getLocation(), BlockType.Air);
                i = i + 1;
                score = score + 50;
                if(i < 11){
                  placeSnitch();
                  displayScoreMessage(bogengetroffenzahl);
                }

                if(i >= 11){
                  displayScoreMessage(bogengetroffenzahl);
                  displayWinnerMessage(player);
                  i = 1;
                  return;
                }
              }
            }
          }
        }
      }
    }
  }

  private void displayStartMessage(){
    String msg2 = "Versuche jeden ";
    String msg3 = "goldenen Schnatz";
    String msg4 = " zu fangen.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayScoreMessage(int zahl){
    int blockanzahl = i - 1;
    String msg2 = "Das war Nummer ";
    String msg3 = "/10. ";
    String msg4 ="";
    String msg5 = " Punkte.";
    
    if(zahl == 1)
      msg4 = "+150";
    else if(zahl == 2)
      msg4 = "+50";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + blockanzahl + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + msg5;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayWinnerMessage(Player player){
    String msg2 = "Du hast jeden Schnatz ";
    String msg3 = "gefangen ";
    String msg4 = " Punkte geholt.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + "und " + ChatFormat.GOLD + score + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);

    playerinv = player.getInventory();
    playerinv.removeItem(ItemType.Bow);
    playerinv = player.getInventory();
    playerinv.removeItem(ItemType.Arrow);
    score = 0;
    isEnabled = false;
  }
}