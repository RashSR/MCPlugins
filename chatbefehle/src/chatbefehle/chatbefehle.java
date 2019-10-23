package chatbefehle;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.*;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.entity.living.humanoid.Human;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.Server;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.PlayerReference;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;

public class chatbefehle extends EZPlugin {
  
  @Command(aliases = { "hub" },
            description = "Teleportiert den Spieler in die Hauptlobby.",
            permissions = { "" },
            toolTip = "/hub")

  public void teleporttohub(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player) { 

        Player player = (Player)caller;
        Location where = player.getLocation();
        Location whereNow = new Location(251, 71, 262);
        player.teleportTo(whereNow);
        player.setModeId(2);

                                  }
                                                                          }                                                                     

  @Command(aliases = { "zombie"},
           description = "Teleportiert den Spieler ins Zombiestarthaus.",
           permissions = {""},
           toolTip = "/zombie")

  public void teleporttozombie(MessageReceiver caller, String[] parameters) {
    
    if (caller instanceof Player) {

       Player player = (Player)caller;
       Location where = player.getLocation();
       Location whereNow = new Location(256, 71, 546);
       player.teleportTo(whereNow);
       player.setModeId(2);

                                  }
                                                                            }

  @Command(aliases = {"1vs1"},
           description = "Teleportiert den Spieler in die 1vs1 Lobby.",
           permissions = {""},
           toolTip = "/1vs1, or /1vs1 maps, or /1vs1 map quidditch, or /1vs1 map nether, or /1vs1 map weihnachten")

  public void teleportto1vs1(MessageReceiver caller, String[] args) {

    if (caller instanceof Player) {

       Player player = (Player)caller;

       Location quidditchfeld = new Location(163, 138, 309);
       Location nethermap = new Location(163, 149, 364);
       Location weihnachtsmap = new Location(207, 135, 309);
       Location peitschendeweide = new Location(145, 157, 276);

       if(args.length == 1){
       Location where = player.getLocation();
       Location whereNow = new Location(107, 151, 309);
       player.teleportTo(whereNow);
       player.setModeId(2); 
       ItemFactory factory = Canary.factory().getItemFactory();

       Item quidditchmapbesichtigen = factory.newItem(ItemType.GoldNugget);
       quidditchmapbesichtigen.setDisplayName(ChatFormat.GREEN + "Quidditch-Map besichtigen");

       Item nethermapbesichtigen = factory.newItem(ItemType.NetherWart);
       nethermapbesichtigen.setDisplayName(ChatFormat.GREEN + "Nether-Map besichtigen");

       Item weihnachtsmapbesichtigen = factory.newItem(ItemType.SpruceSapling);
       weihnachtsmapbesichtigen.setDisplayName(ChatFormat.GREEN + "Weihnachts-Map besichtigen");

       Item weidemapbesichtigen =factory.newItem(ItemType.DarkOakSapling);
       weidemapbesichtigen.setDisplayName(ChatFormat.GREEN + "Peitschende-Weide-Map besichtigen");

       Item backfeder = factory.newItem(ItemType.Feather);
       backfeder.setDisplayName(ChatFormat.RED + "Besichtigung beenden!");

       player.getInventory().setSlot(1, quidditchmapbesichtigen);
       player.getInventory().setSlot(2, nethermapbesichtigen);
       player.getInventory().setSlot(3, weihnachtsmapbesichtigen);
       player.getInventory().setSlot(4, weidemapbesichtigen);
       player.getInventory().setSlot(8, backfeder);

     }

     if(args.length == 2){

      if(args[1].equalsIgnoreCase("maps")){

        tell1vs1maps();
      }
     }

    if(args.length == 3 && args[1].equalsIgnoreCase("map")){

      if(args[2].equalsIgnoreCase("Quidditch") || args[2].equalsIgnoreCase("1")){

        player.teleportTo(quidditchfeld);
        player.setModeId(1);

      }

      if(args[2].equalsIgnoreCase("nether") || args[2].equalsIgnoreCase("2")){

        player.teleportTo(nethermap);
        player.setModeId(1);

      }

     if(args[2].equalsIgnoreCase("weihnachten") || args[2].equalsIgnoreCase("3")){

      player.teleportTo(weihnachtsmap);
      player.setModeId(1);

     }

     if(args[2].equalsIgnoreCase("peitschende") || args[2].equalsIgnoreCase("weide") || args[2].equalsIgnoreCase("4")){

      player.teleportTo(peitschendeweide);
      player.setModeId(1);
     }
    }

                                  }  
                                                                          }

  @Command(aliases = {"buildit"},
           description = "Teleportiert den Spieler zum Minigame Buildit.",
           permissions = {""},
           toolTip = "/buildit")

    public void teleporttobuildit(MessageReceiver caller, String[] parameters) {
      if (caller instanceof Player) {

          Player player = (Player)caller;
          player.getInventory().clearInventory();
          Location where = player.getLocation();
          Location whereNow = new Location(93, 79, 327);
          player.teleportTo(whereNow);
          player.setModeId(2);

                                    }
                                                                                }

  @Command(aliases = {"stadt"},
           description = "Teleportiert den Spieler zur Stadt von Aileen und Rash.",
           permissions = {""},
           toolTip = "/stadt")

    public void teleporttostadt(MessageReceiver caller, String[] parameters) {
      if (caller instanceof Player) {

          Player player = (Player)caller;
          Location where = player.getLocation();
          Location whereNow = new Location(4928, 64, 4899);
          player.teleportTo(whereNow);
          player.setModeId(0);

                                     }
                                                                              }

  @Command(aliases = {"ping"},
           description = "Zeigt den Ping des Spielers in ms an.",
           permissions = {""},
           toolTip = "/ping")

    public void showping(MessageReceiver caller, String[] parameters) {
      if (caller instanceof Player) {

         Player player = (Player)caller;

         String msg1 = "[PING]";
         String name = player.getDisplayName();
         String msg2 = " hat einen Ping von ";
         int ping = player.getPing();
         String msg3 = " ms";
         String msg4 = ".";

         Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.BLUE + name + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + ping + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4);

                                   }
                                                                       }

 @Command(aliases = {"teleportto"},
          description = "Zeigt dem Spieler die verfügbaren Schnellreisepunkte im Chat an.",
          permissions = {""},
          toolTip = "/teleportto")

    public void teleportpunkte(MessageReceiver caller, String[] parameters) {

      if (caller instanceof Player) {

         Player player = (Player)caller;

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

         Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD +  msg3 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg6 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + dna + ChatFormat.DARK_GREEN + msg7 + ChatFormat.GOLD + msg8 + ChatFormat.DARK_GREEN + msg9);

                                   }
                                                                            }

 @Command(aliases = {"eingabebefehle"},
          description = "Zeigt dem Spieler die verfügbaren Eingabebefehle.",
          permissions = {""},
          toolTip = "/eingabebefehle")

   public void eingabebefehle(MessageReceiver caller, String[] parameters) {

     if (caller instanceof Player) {

       Player player = (Player)caller;

       String msg1 = "[SERVER]";
       String msg2 = "Mit ";
       String msg3 = "/teleportto ";
       String msg4 = "erfährst du alle Teleportpunkte, mit ";
       String msg5 = "/ping ";
       String msg6 = "deinen Ping und mit ";
       String msg7 = "/koordinaten ";
       String msg8 = "deine XYZ-Koordinaten.";

       Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + msg6 + ChatFormat.GOLD + msg7 + ChatFormat.DARK_GREEN + msg8);

                                   }
                                                                            }


@Command(aliases = {"koordinaten"},
         description = "Zeigt die koordinaten des Spielers an.",
         permissions = {""},
         toolTip = "/koordinaten")

  public void koordinaten(MessageReceiver caller, String[] parameters) {

    if (caller instanceof Player) {

      Player player = (Player)caller;
      Location spieler = player.getLocation();
      double x = spieler.getX();
      double y = spieler.getY();
      double z = spieler.getZ();
      int xi = (int) x;
      int yi = (int) y;
      int zi = (int) z;
      player.chat("Meine Koordinaten sind " + ChatFormat.DARK_GREEN + "X:" + ChatFormat.GOLD + xi + ChatFormat.DARK_GREEN + " Y:" + ChatFormat.GOLD + yi + ChatFormat.WHITE + " und " + ChatFormat.DARK_GREEN + "Z: " + ChatFormat.GOLD + zi + ChatFormat.WHITE + ".");

    }
  }

public void tell1vs1maps(){

  String msg1 = "[1vs1] ";
  String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
  String msg3 = "Quidditch";
  String msg4 = "Nether";
  String msg5 = "Weihnachten";
  String msg6 = "Peitschende Weide";
  String komma = ", ";

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + komma + ChatFormat.GOLD + msg6 + ChatFormat.DARK_GREEN + ".");
 }
}    

