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
  
  private final String pluginName = "[Quidditch]";
  private final BlockType SNITCH_BLOCK_TYPE = BlockType.GoldBlock;
  private final int POINTS_PER_RIGHTCLICK = 150;
  private final int POINT_PER_ARROW_HIT = 50;
  private boolean isEnabled = false;
  private Player player;
  int i = 1;
  private int score = 0;
  
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
            startGame(player);
        }
      }
    }
  }

  private void startGame(Player player){
    player.teleportTo(Utils.QuidditchFieldLocation);
    displayStartMessage();
    isEnabled = true;
    placeSnitch();
    this.player = player;
    giveEquipToPlayer();
    player.setModeId(Utils.ADVENTURE_MODE);
  }

  private void giveEquipToPlayer(){
    ItemFactory factory = Canary.factory().getItemFactory();
    Item infinityBow = factory.newItem(ItemType.Bow);
    infinityBow.setDisplayName(ChatFormat.GOLD + "Schnatzfaenger");

    short enchantmentLevel = 1;
    Enchantment infinity = factory.newEnchantment(Enchantment.Type.Infinity, enchantmentLevel);
    infinityBow.addEnchantments(infinity);

    Inventory playerInventory = player.getInventory();
    playerInventory.setSlot(1, infinityBow);
    playerInventory.setSlot(ItemType.Arrow, 1, 8);
  }

  private void placeSnitch(){
    boolean hasAirBlockBeenSelected = false;

    while(!hasAirBlockBeenSelected){
      Location startLocation = new Location(136, 122, 290);
      Location endLocation = new Location(190, 154, 328);
      Location randomLocation = getRandomLocationInsideVolume(startLocation, endLocation);
      Block possibleSnitch = randomLocation.getWorld().getBlockAt((int)randomLocation.getX(), (int)randomLocation.getY(), (int)randomLocation.getZ());
      
      if(possibleSnitch.getType() == BlockType.Air){
        randomLocation.getWorld().setBlockAt(randomLocation, SNITCH_BLOCK_TYPE);
        hasAirBlockBeenSelected = true;
      }
    }
  }

  private Location getRandomLocationInsideVolume(Location startLocation, Location endLocation){
    double x = startLocation.getX() + Math.random() * (endLocation.getX() - startLocation.getX());
    double y = startLocation.getY() + Math.random() * (endLocation.getY() - startLocation.getY());
    double z = startLocation.getZ() + Math.random() * (endLocation.getZ() - startLocation.getZ());

    Location randomLocation = new Location(x, y, z);
    return randomLocation;
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    if(isEnabled){
      Block clickedBlock = event.getBlockClicked();
      World world = clickedBlock.getLocation().getWorld();
      Player player = event.getPlayer();

      if(this.player == player && clickedBlock.getType() == SNITCH_BLOCK_TYPE){
        world.setBlockAt(clickedBlock.getLocation(), BlockType.Air);
        i = i + 1;
        score += POINTS_PER_RIGHTCLICK;
        if(i < 11){
          placeSnitch();
          displayScoreMessage(POINTS_PER_RIGHTCLICK);
        }

        if(i >= 11){
          displayScoreMessage(POINTS_PER_RIGHTCLICK);
          Inventory playerInventory = player.getInventory();
          playerInventory.removeItem(ItemType.Bow);
          displayWinnerMessage();
          i = 1;
          return;
        }
      }
    } 
  }

  @HookHandler
  public void ProjectileHitHookEvent(ProjectileHitHook event){
    if(isEnabled){
      Entity arrow = event.getProjectile();
      World world = arrow.getWorld();
      Location loc = arrow.getLocation();
      int bogengetroffenzahl = 2;
      arrow.destroy();

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

            if(vorlauefigerschnatz.getType() == SNITCH_BLOCK_TYPE){
              double betrag = (vorlauefigerschnatz.getX() + 0.5 - loc.getX()) * (vorlauefigerschnatz.getX() + 0.5 - loc.getX()) + (vorlauefigerschnatz.getY() + 0.5 - loc.getY()) * (vorlauefigerschnatz.getY() + 0.5 - loc.getY()) + (vorlauefigerschnatz.getZ() + 0.5 - loc.getZ()) * (vorlauefigerschnatz.getZ() + 0.5 - loc.getZ());
              double abstand = Math.sqrt(betrag);

              if(abstand <= 3.5){
                world.setBlockAt(vorlauefigerschnatz.getLocation(), BlockType.Air);
                i++;
                score += POINT_PER_ARROW_HIT;
                if(i < 11){
                  placeSnitch();
                  displayScoreMessage(POINT_PER_ARROW_HIT);
                }

                if(i >= 11){
                  displayScoreMessage(POINT_PER_ARROW_HIT);
                  displayWinnerMessage();
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
    String msg4 = "zu fangen.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayScoreMessage(int scoredPoints){
    int totalCatchedSnitchCount = i - 1;
    String msg2 = "Das war Nummer ";
    String msg3 = "/10. ";
    String msg4 ="+" + scoredPoints;
    String msg5 = " Punkte.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + totalCatchedSnitchCount + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + msg5;
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  private void displayWinnerMessage(){
    String msg2 = "Du hast jeden Schnatz ";
    String msg3 = "gefangen ";
    String msg4 = " Punkte geholt.";
    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + "und " + ChatFormat.GOLD + score + ChatFormat.DARK_GREEN + msg4;
    Utils.BroadcastServerMessage(pluginName, serverMessage);

    Inventory playerInventory = player.getInventory();
    playerInventory.removeItem(ItemType.Bow);
    playerInventory.removeItem(ItemType.Arrow);
    score = 0;
    isEnabled = false;
  }
}