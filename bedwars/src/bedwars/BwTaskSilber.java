package bedwars;

import net.canarymod.Canary;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.tasks.ServerTask;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.World;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.inventory.Item;

public class BwTaskSilber extends ServerTask {

    private World world;

    public BwTaskSilber(World myWorld) {

        super(Canary.getServer(), 17 * 20, true);
        world = myWorld;

                                  }
    
    public void run() {

      ItemFactory factory1 = Canary.factory().getItemFactory();
      Item eisengelb = factory1.newItem(ItemType.IronIngot);
      eisengelb.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");

      ItemFactory factory2 = Canary.factory().getItemFactory();
      Item eisenrot = factory2.newItem(ItemType.IronIngot);
      eisenrot.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");

      ItemFactory factory3 = Canary.factory().getItemFactory();
      Item eisenlila = factory3.newItem(ItemType.IronIngot);
      eisenlila.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");

      ItemFactory factory4 = Canary.factory().getItemFactory();
      Item eisengruen = factory4.newItem(ItemType.IronIngot);
      eisengruen.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");

      int xeisengelb = 436;
      int yeisengelb = 227;
      int zeisengelb = 502;

      world.dropItem(xeisengelb, yeisengelb + 1, zeisengelb, eisengelb);

      int xrot = 421;
      int yrot = 227;
      int zrot = 289;

      world.dropItem(xrot, yrot + 1, zrot, eisenrot);

      int xlila = 323;
      int ylila = 227;
      int zlila = 402;

      world.dropItem(xlila, ylila + 1, zlila, eisenlila);

      int xgruen = 534;
      int ygruen = 227;
      int zgruen = 388;

      world.dropItem(xgruen, ygruen + 1, zgruen, eisengruen);

                       }
}
