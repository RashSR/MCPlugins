package chatbefehle;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import utils.Utils;

public class chatbefehle extends EZPlugin {
  
  @Command(aliases = { "hub" },
            description = "Teleportiert den Spieler in die Hauptlobby.",
            permissions = { "" },
            toolTip = "/hub")
  public void TeleportToHub(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){ 
        player.teleportTo(Utils.HubLocation);
        player.setModeId(Utils.ADVENTURE_MODE);
    }
  }                                                                     

  @Command(aliases = { "zombie"},
           description = "Teleportiert den Spieler ins Zombiestarthaus.",
           permissions = {""},
           toolTip = "/zombie")
  public void TeleportToZombie(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
       player.teleportTo(Utils.ZombieLocation);
       player.setModeId(Utils.ADVENTURE_MODE);
    }
  }

  @Command(aliases = {"1vs1"},
           description = "Teleportiert den Spieler in die 1vs1 Lobby.",
           permissions = {""},
           toolTip = "/1vs1, or /1vs1 maps, or /1vs1 map quidditch, or /1vs1 map nether, or /1vs1 map weihnachten")
  public void TeleportTo1vs1(MessageReceiver caller, String[] args){
    if(caller instanceof Player player){
      if(args.length == 1){
        player.teleportTo(Utils.Location1vs1);
        player.setModeId(Utils.ADVENTURE_MODE);

        ItemFactory factory = Canary.factory().getItemFactory();
        Item quidditchTeleportItem = factory.newItem(ItemType.GoldNugget);
        Item netherTeleportItem = factory.newItem(ItemType.NetherWart);
        Item christmasTeleportItem = factory.newItem(ItemType.SpruceSapling);
        Item shrieckingShackTeleportItem =factory.newItem(ItemType.DarkOakSapling);
        Item teleportBackItem = factory.newItem(ItemType.Feather);

        quidditchTeleportItem.setDisplayName(ChatFormat.GREEN + "Quidditch-Map besichtigen");
        netherTeleportItem.setDisplayName(ChatFormat.GREEN + "Nether-Map besichtigen");     
        christmasTeleportItem.setDisplayName(ChatFormat.GREEN + "Weihnachts-Map besichtigen");
        shrieckingShackTeleportItem.setDisplayName(ChatFormat.GREEN + "Peitschende-Weide-Map besichtigen");
        teleportBackItem.setDisplayName(ChatFormat.RED + "Besichtigung beenden!");

        player.getInventory().setSlot(1, quidditchTeleportItem);
        player.getInventory().setSlot(2, netherTeleportItem);
        player.getInventory().setSlot(3, christmasTeleportItem);
        player.getInventory().setSlot(4, shrieckingShackTeleportItem);
        player.getInventory().setSlot(8, teleportBackItem);
      }
      else if(args.length == 2 && args[1].equalsIgnoreCase("maps"))
          display1vs1MapOptions();
      else if(args.length == 3 && args[1].equalsIgnoreCase("map")){
        if(args[2].equalsIgnoreCase("quidditch") || args[2].equalsIgnoreCase("1")){
          player.teleportTo(Utils.QuidditchFieldLocation);
          player.setModeId(Utils.CREATIVE_MODE);
        }
        else if(args[2].equalsIgnoreCase("nether") || args[2].equalsIgnoreCase("2")){
          player.teleportTo(Utils.NetherMapLocation);
          player.setModeId(Utils.CREATIVE_MODE);
        }
        else if(args[2].equalsIgnoreCase("weihnachten") || args[2].equalsIgnoreCase("3")){
          player.teleportTo(Utils.ChristmasMapLocation);
          player.setModeId(Utils.CREATIVE_MODE);
        }
        else if(args[2].equalsIgnoreCase("peitschende") || args[2].equalsIgnoreCase("weide") || args[2].equalsIgnoreCase("4")){
          player.teleportTo(Utils.ShriekingShackLocation);
          player.setModeId(Utils.CREATIVE_MODE);
        }
      }
    }  
  }

  @Command(aliases = {"buildit"},
           description = "Teleportiert den Spieler zum Minigame Buildit.",
           permissions = {""},
           toolTip = "/buildit")
  public void TeleportToBuildIt(MessageReceiver caller, String[] parameters){
    if(caller instanceof Player player){
      Utils.ClearPlayerInventory(player);
      player.teleportTo(Utils.BuildItLocation);
      player.setModeId(Utils.ADVENTURE_MODE);
    }
  }

  @Command(aliases = {"stadt"},
           description = "Teleportiert den Spieler zur Stadt von Aileen und Rash.",
           permissions = {""},
           toolTip = "/stadt")
  public void TeleportToCity(MessageReceiver caller, String[] parameters){
    if (caller instanceof Player player) {
      player.teleportTo(Utils.CityLocation);
      player.setModeId(Utils.SURVIVAL_MODE);
    }
  }

  @Command(aliases = {"ping"},
           description = "Zeigt den Ping des Spielers in ms an.",
           permissions = {""},
           toolTip = "/ping")
  public void ShowPing(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) {
      String msg1 = "[PING]";
      String name = player.getDisplayName();
      String msg2 = " hat einen Ping von ";
      int ping = player.getPing();
      String msg3 = " ms";
      String msg4 = ".";
      String serverMessage = ChatFormat.BLUE + name + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + ping + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4;
      Utils.BroadcastServerMessage(msg1, serverMessage);
    }
  }

 @Command(aliases = {"teleportto"},
          description = "Zeigt dem Spieler die verfügbaren Schnellreisepunkte im Chat an.",
          permissions = {""},
          toolTip = "/teleportto")
  public void DisplayTeleportOptions(MessageReceiver caller, String[] parameters) {
    String msg1 = "[SERVER]";
    String msg2 = "Du kannst zu folgenden Orten reisen: ";
    String msg3 = "/hub";
    String komma = ", ";
    String msg4 = "/1vs1";
    String msg5 = "/zombie";
    String msg6 = "/buildit";
    String dna = "/dna";
    String msg7 = " und ";
    String msg8 = "/stadt";
    String msg9 = ".";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD +  msg3 + 
      ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + 
      komma + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + 
      msg6 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + dna + ChatFormat.DARK_GREEN + 
      msg7 + ChatFormat.GOLD + msg8 + ChatFormat.DARK_GREEN + msg9;
    Utils.BroadcastServerMessage(msg1, serverMessage);
  }

 @Command(aliases = {"eingabebefehle"},
          description = "Zeigt dem Spieler die verfügbaren Eingabebefehle.",
          permissions = {""},
          toolTip = "/eingabebefehle")
  public void DisplayChatInputOptions(MessageReceiver caller, String[] parameters) {
    String msg1 = "[SERVER]";
    String msg2 = "Mit ";
    String msg3 = "/teleportto ";
    String msg4 = "erfährst du alle Teleportpunkte, mit ";
    String msg5 = "/ping ";
    String msg6 = "deinen Ping und mit ";
    String msg7 = "/koordinaten ";
    String msg8 = "deine XYZ-Koordinaten.";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + 
      ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + 
      msg6 + ChatFormat.GOLD + msg7 + ChatFormat.DARK_GREEN + msg8;
    Utils.BroadcastServerMessage(msg1, serverMessage);
  }

  @Command(aliases = {"koordinaten"},
          description = "Zeigt die koordinaten des Spielers an.",
          permissions = {""},
          toolTip = "/koordinaten")
  public void DisplayPlayerCoordinates(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) {
      Location playerLocation = player.getLocation();
      int x = (int) playerLocation.getX();
      int y = (int) playerLocation.getY();
      int z = (int) playerLocation.getZ();

      String message = "Meine Koordinaten sind " + ChatFormat.DARK_GREEN + "X:" + ChatFormat.GOLD 
        + x + ChatFormat.DARK_GREEN + " Y:" + ChatFormat.GOLD + y + ChatFormat.WHITE + " und " 
        + ChatFormat.DARK_GREEN + "Z: " + ChatFormat.GOLD + z + ChatFormat.WHITE + ".";
      player.chat(message);
    }
  }

  private void display1vs1MapOptions(){
    String msg1 = "[1vs1]";
    String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
    String msg3 = "Quidditch";
    String msg4 = "Nether";
    String msg5 = "Weihnachten";
    String msg6 = "Peitschende Weide";
    String komma = ChatFormat.DARK_GREEN + ", ";

    String serverMessage = ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + 
      komma + ChatFormat.GOLD + msg4 + komma + ChatFormat.GOLD + msg5 + 
      komma + ChatFormat.GOLD + msg6 + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(msg1, serverMessage);
  }

  @Command(aliases = {"randblocks"},
          description = "randblocks.",
          permissions = {""},
          toolTip = "/randblocks")
  public void randblocks(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) {
      Location loc = player.getLocation();
      World world = player.getWorld();
      int x = loc.getBlockX();
      int y = loc.getBlockY() + 3; // start 2 blocks above head
      int z = loc.getBlockZ();

      for(int i = 0; i < 3; i++){
        world.setBlockAt(x, y+i, z, BlockType.Dirt);
        Block toChange = world.getBlockAt(x, y+i, z);
        toChange.setData((short)i);
        toChange.update();
        //player.chat(toChange.toString());
      }

      //How to clone a block!
      Position newPos = new Position(x, y+5, z);
      Block toClone = world.getBlockAt(x, y+2, z);
      player.chat(toClone.toString());
      world.setBlockAt(newPos, toClone);
      Block cloned = world.getBlockAt(newPos.getBlockX(), newPos.getBlockY(), newPos.getBlockZ());
      if(toClone.getData() != 0){
        cloned.setData(toClone.getData());
        cloned.update();
      }
    }
  }  
}