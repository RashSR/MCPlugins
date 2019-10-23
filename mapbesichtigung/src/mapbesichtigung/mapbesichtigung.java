package mapbesichtigung;
import net.canarymod.api.entity.*;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.PlayerReference;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.*;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.hook.player.TeleportHook;
import net.canarymod.api.world.World;
import net.canarymod.api.entity.living.humanoid.Human;
import net.canarymod.hook.world.RedstoneChangeHook;
import java.lang.reflect.InvocationTargetException;

public class mapbesichtigung extends EZPlugin implements PluginListener {

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }  

 @HookHandler
  public void mapbesichtigen(ItemUseHook event) {

    Player player = event.getPlayer();
    Location loc = player.getLocation();
    Location quidditchfeld = new Location(163, 138, 309);
    Location nethermap = new Location(163, 149, 364);
    Location weihnachtsmap = new Location(207, 135, 309);
    Location hub1vs1 = new Location(107, 151, 309);
    Location peitschendeweide = new Location(145, 157, 276);

    if (player.getItemHeld().getType() == ItemType.GoldNugget && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Quidditch-Map besichtigen") ) {
     ItemFactory factory = Canary.factory().getItemFactory();
     Item bowl = factory.newItem(ItemType.Bowl);
     bowl.setDisplayName(ChatFormat.BLUE + "esse mich");
     player.getInventory().setSlot(32, 2, 17, 6);
      //player.getInventory().setSlot(ItemType.Bowl, 1, 6);
      player.giveItem(bowl);
      player.getInventory().removeItem(bowl);
      player.giveItem(bowl);
     
      player.setModeId(1);

      player.teleportTo(quidditchfeld);

    }

    if (player.getItemHeld().getType() == ItemType.NetherWart && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Nether-Map besichtigen") ) {

      player.setModeId(1);

      player.teleportTo(nethermap);     

    }

    if (player.getItemHeld().getType() == ItemType.SpruceSapling && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Weihnachts-Map besichtigen") ) {

      player.setModeId(1);

      player.teleportTo(weihnachtsmap);     

    }

    if (player.getItemHeld().getType() == ItemType.Feather && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Besichtigung beenden!") ) {


      player.setModeId(2);

      player.teleportTo(hub1vs1);   
        
    }

    if(player.getItemHeld().getType() == ItemType.DarkOakSapling && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Peitschende-Weide-Map besichtigen")){

      player.setModeId(1);

      player.teleportTo(peitschendeweide);

    }

  } 
 
   @HookHandler
   
  public void telemitdruckplatte1vs1(RedstoneChangeHook event){

    Block druckplatte = event.getSourceBlock();
    Location locdruckplatte = druckplatte.getLocation();
    double x = locdruckplatte.getX();
    double y = locdruckplatte.getY();
    double z = locdruckplatte.getZ();
    World world = locdruckplatte.getWorld();
    Player player = world.getClosestPlayer(243, 71, 266, 5);

    int xdruckplatte = (int)x;
    int ydruckplatte = (int)y;
    int zdruckplatte = (int)z;

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
      try{

    if(xdruckplatte == 243 && ydruckplatte == 71 && zdruckplatte == 266){

      player.getInventory().setSlot(1, quidditchmapbesichtigen);
      player.getInventory().setSlot(2, nethermapbesichtigen);
      player.getInventory().setSlot(3, weihnachtsmapbesichtigen);
      player.getInventory().setSlot(4, weidemapbesichtigen);
      player.getInventory().setSlot(8, backfeder);
   }
}
catch(NullPointerException e){
	logger.info("alles im griff!");
} 

   
}
}