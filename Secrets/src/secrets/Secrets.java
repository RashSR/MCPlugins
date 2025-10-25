package secrets;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.CustomStorageInventory;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;
import net.canarymod.database.Database;
import net.canarymod.database.DataAccess;
import java.util.HashMap;
import java.util.Map;
import net.canarymod.database.exceptions.*;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import utils.Utils;
import net.canarymod.hook.player.PlayerMoveHook;
import net.canarymod.api.world.position.Location;
import net.canarymod.hook.player.SlotClickHook;
import net.canarymod.hook.player.InventoryHook;

public class Secrets extends EZPlugin implements PluginListener{
  public static boolean pilz, potter, rd, ld, secret5, secret6, secret7, secret8, secret9, secret10, secret11, secret12, secret13, secret14, secret15, secret16, secret17, secret18, secret19, secret20, secret21, secret22, secret23, secret24, secret25, secret26, secret27;   
  private final String pluginName = "[Secrets]";
  public boolean isEnabled;
  // secret5: umbrella logo, secret6: zum honigtopf

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

  @Command(aliases = { "setzen" },
            description = "secrets plugin",
            permissions = { "*" },
            toolTip = "/setzen")
  public void setzenCommand(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) { 
      if(pilz || potter || rd || ld || secret5 || secret6 || secret7 || secret8 || secret9 || secret10 || secret11 || secret12 || secret13 || secret14 || secret15 || secret16 || secret17 || secret18 || secret19 || secret20 || secret21 || secret22 || secret23 || secret24 || secret25 || secret26 || secret27){
        pilz = false;
        potter = false;
        rd = false;
        ld = false;
        secret5 = false;
        secret6 = false;
        secret7 = false;
        secret8 = false;
        secret9 = false;
        secret10 = false;
        secret11 = false;
        secret12 = false;
        secret13 = false;
        secret14 = false;
        secret15 = false;
        secret16 = false;
        secret17 = false;
        secret18 = false;
        secret19 = false;
        secret20 = false;
        secret21 = false;
        secret22 = false;
        secret23 = false;
        secret24 = false;
        secret25 = false;
        secret26 = false;
        secret27 = false;
        player.chat("keine secrets");
      }
      else{
        pilz = true;
        potter = true;
        rd = true;
        ld = true;
        secret5 = true;
        secret6 = true;
        secret7 = true;
        secret8 = true;
        secret9 = true;
        secret10 = true;
        secret11 = true;
        secret12 = true;
        secret13 = true;
        secret14 = true;
        secret15 = true;
        secret16 = true;
        secret17 = true;
        secret18 = true;
        secret19 = true;
        secret20 = true;
        secret21 = true;
        secret22 = true;
        secret23 = true;
        secret24 = true;
        secret25 = true;
        secret26 = true;
        secret27 = true;
        player.chat("alle secrets");
      }
      savesecrets(player);
      pilz = false;
      potter = false;
      rd = false;
      ld = false;
      secret5 = false;
      secret6 = false;
      secret7 = false;
      secret8 = false;
      secret9 = false;
      secret10 = false;
      secret11 = false;
      secret12 = false;
      secret13 = false;
      secret14 = false;
      secret15 = false;
      secret16 = false;
      secret17 = false;
      secret18 = false;
      secret19 = false;
      secret20 = false;
      secret21 = false;
      secret22 = false;
      secret23 = false;
      secret24 = false;
      secret25 = false;
      secret26 = false;
      secret27 = false;
    }
  }

  @Command(aliases = { "secrets" },
            description = "secrets plugin",
            permissions = { "*" },
            toolTip = "/secrets")
  public void secretsCommand(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player player) { 
      loadsecrets(player);
      ObjectFactory objectFactory = Canary.factory().getObjectFactory();
      CustomStorageInventory customInventory = objectFactory.newCustomStorageInventory(ChatFormat.DARK_AQUA + "Secrets", 3);
      ItemFactory itemFactory = Canary.factory().getItemFactory();
      isEnabled = true;
      Item[] notFoundSecretItem = achieveitems(ItemType.GrayDye, itemFactory);
      Item[] foundSecretItem = achieveitems(ItemType.LimeDye, itemFactory);
      String[] secretNames = getSecretNames();
      Item purpleDye = itemFactory.newItem(ItemType.PurpleDye);

      for(int i = 0; i < 27; i++){
        if(i < 6){
          if(i == 0 && pilz || i == 1 && potter || i == 2 && rd || i == 3 && ld || i == 4 && secret5 || i == 5 && secret6 || i == 6 && secret7 || i == 7 && secret8 || i == 8 && secret9 || i == 9 && secret10 || i == 10 && secret11 || i == 11 && secret12 || i == 12 && secret13 || i == 13 && secret14 || i == 14 && secret15 || i == 15 && secret16 || i == 16 && secret17 || i == 17 && secret18 || i == 18 && secret19 || i == 19 && secret20 || i == 20 && secret21 || i == 21 && secret22 || i == 22 && secret23 || i == 23 && secret24 || i == 24 && secret25 || i == 25 && secret26 || i == 26 && secret27){
            foundSecretItem[i].setDisplayName(ChatFormat.GREEN + secretNames[i]);
            customInventory.setSlot(i, foundSecretItem[i]);
          }
          else{
          notFoundSecretItem[i].setDisplayName(ChatFormat.RED + "Noch nicht entdeckt!");
          customInventory.setSlot(i,notFoundSecretItem[i]);
        }
        }

        else{
          purpleDye.setDisplayName(ChatFormat.DARK_PURPLE + secretNames[i]);
          customInventory.setSlot(i, purpleDye);
        }
      }
      player.openInventory(customInventory);
    }
  }

  private Item[] achieveitems(ItemType itemType, ItemFactory itemFactory){
    Item[] item = new Item[27];
    for(int i = 0; i < 27; i++)
      item[i] = itemFactory.newItem(itemType);
    
    return item;
  }

  private String[] getSecretNames(){
    String[] achievements = new String[27];
    for(int i = 0; i < 27; i++){
      if(i<10){
        if(i == 0){
        achievements[i] = "Riesiger Pilz";
      }
        if(i == 1){
         achievements[i] = "Zauberakademie";
        }
        if(i == 2){
          achievements[i] = "Varo";
        }
        if(i == 3){
          achievements[i] = "Blaue Idylle";
        }
        if(i == 4){
          achievements[i] = "Umbrella-Logo";
        }
        if(i == 5){
          achievements[i] = "Zum Honigtopf";
        }
        if(i == 6){
          achievements[i] = "s7";
        }
        if(i == 7){
          achievements[i] = "s8";
        }
        if(i == 8){
          achievements[i] = "s9";
        }
        if(i == 9){
          achievements[i] = "s10";
        }
      }
      else{
        achievements[i] = "Coming Soon";
      }
    }
    return achievements;
  }

  @HookHandler
  public void SlotClickHookEvent(SlotClickHook event){
    if(isEnabled)
      event.setCanceled();
  }

  @HookHandler
  public void InventoryHookEvent(InventoryHook event){
    if(isEnabled && event.isClosing())
      isEnabled = false;
  }

  @HookHandler
  public void PlayerMoveHookEvent(PlayerMoveHook event) {
    Player player = event.getPlayer();
    loadsecrets(player);
    Location playerLocation = player.getLocation();
    double x = playerLocation.getX();
    double z = playerLocation.getZ();
    double y = playerLocation.getY();
    if(!pilz){
      if(x > 316 && x < 318 && z > 204 && z < 206){
        pilz = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Riesiger Pilz " + ChatFormat.DARK_GREEN + "gefunden.");
        pilz = false;
      }
    }
    if(!rd){
      if(x > 292 && x < 294 && z > 186 && z < 188){
        rd = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Varo " + ChatFormat.DARK_GREEN + "gefunden.");
        rd = false;
      }
    }
    if(!ld){
      if(x > 235 && x < 237 && z > 188 && z < 190){
        ld = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Blaue Idylle " + ChatFormat.DARK_GREEN + "gefunden.");
        ld = false;
      }
      if(x > 233 && x < 235 && z > 187 && z < 189){
        ld = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Blaue Idylle " + ChatFormat.DARK_GREEN + "gefunden.");
        ld = false;
      }     
    }
    if(!potter){
      if(x > -356 && x < -346 && z > 244 && z < 283){
        potter = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Zauberakademie " + ChatFormat.DARK_GREEN + "gefunden.");
        potter = false;
        }           
      }
    if(!secret5){
      if(x > 244 && x < 247 && z > 590 && z < 593 && y >= 76){
        secret5 = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Umbrella-Logo " + ChatFormat.DARK_GREEN + "gefunden.");
        secret5 = false;
      }
    }
    if(!secret6){
      if(x > 143 && x < 145 && y < 141 && z > 275 && z < 277){
        secret6 = true;
        savesecrets(player);
        Utils.BroadcastServerMessage(pluginName, "Secret" + ChatFormat.GOLD + " Zum Honigtopf " + ChatFormat.DARK_GREEN + "gefunden.");
        secret6 = false;
      }
    }
    }

  private void loadsecrets(Player player){
    String playername = player.getDisplayName();
    DatenbankSecrets ds = new DatenbankSecrets();
    HashMap<String, Object> search = new HashMap<String, Object>();
    search.put("player_name", playername);

    try {
      Database.get().load(ds, search);
    } catch (DatabaseReadException e) {
      logger.info(playername + " is not online");
    }

    pilz = ds.bigmush;
    potter = ds.hogwarts;
    rd = ds.rechtsdna;
    ld = ds.linksdna;
    secret5 = ds.umbrella;
    secret6 = ds.honigtopf;
    secret7 = ds.s7;
    secret8 = ds.s8;
    secret9 = ds.s9;
    secret10 = ds.s10;
    secret11 = ds.s11;
    secret12 = ds.s12;
    secret13 = ds.s13;
    secret14 = ds.s14;
    secret15 = ds.s15;
    secret16 = ds.s16;
    secret17 = ds.s17;
    secret18 = ds.s18;
    secret19 = ds.s19;
    secret20 = ds.s20;
    secret21 = ds.s21;
    secret22 = ds.s22;
    secret23 = ds.s23;
    secret24 = ds.s24;
    secret25 = ds.s25;
    secret26 = ds.s26;
    secret27 = ds.s27;
  }

  private void savesecrets(Player player){
    DatenbankSecrets ds = new DatenbankSecrets();
    ds.player_name = player.getDisplayName();
    ds.bigmush = pilz;
    ds.hogwarts = potter;
    ds.rechtsdna = rd;
    ds.linksdna = ld;
    ds.umbrella = secret5;
    ds.honigtopf = secret6;
    ds.s7 = secret7;
    ds.s8 = secret8;
    ds.s9 = secret9;
    ds.s10 = secret10;
    ds.s11 = secret11;
    ds.s12 = secret12;
    ds.s13 = secret13;
    ds.s14 = secret14;
    ds.s15 = secret15;
    ds.s16 = secret16;
    ds.s17 = secret17;
    ds.s18 = secret18;
    ds.s19 = secret19;
    ds.s20 = secret20;
    ds.s21 = secret21;
    ds.s22 = secret22;
    ds.s23 = secret23;
    ds.s24 = secret24;
    ds.s25 = secret25;
    ds.s26 = secret26;
    ds.s27 = secret27;

    HashMap<String, Object> search = new HashMap<String, Object>();
    search.put("player_name", player.getDisplayName());
    try {
      Database.get().update(ds, search); 
    } 
    catch(DatabaseWriteException e){
      logger.error(e);
    }
  }
}