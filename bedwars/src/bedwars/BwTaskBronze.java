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

public class BwTaskBronze extends ServerTask {

    private World world;

    public BwTaskBronze(World myWorld) {

        super(Canary.getServer(), 2 * 20, true);
        world = myWorld;

                                  }
    
    public void run() {

      ItemFactory factory1 = Canary.factory().getItemFactory();
      Item bronzegelb = factory1.newItem(ItemType.ClayBrick);
      bronzegelb.setDisplayName(ChatFormat.GRAY + "Bronze");

      ItemFactory factory2 = Canary.factory().getItemFactory();
      Item bronzerot = factory2.newItem(ItemType.ClayBrick);
      bronzerot.setDisplayName(ChatFormat.GRAY + "Bronze");

      ItemFactory factory3 = Canary.factory().getItemFactory();
      Item bronzelila = factory3.newItem(ItemType.ClayBrick);
      bronzelila.setDisplayName(ChatFormat.GRAY + "Bronze");

      ItemFactory factory4 = Canary.factory().getItemFactory();
      Item bronzegruen = factory4.newItem(ItemType.ClayBrick);
      bronzegruen.setDisplayName(ChatFormat.GRAY + "Bronze");

      int xbronzegelb = 427;
      int ybronzegelb = 226;
      int zbronzegelb = 493;

      world.dropItem(xbronzegelb, ybronzegelb + 1, zbronzegelb, bronzegelb);

      int xbronzerot = 430;
      int ybronzerot = 226;
      int zbronzerot = 298;

      world.dropItem(xbronzerot, ybronzerot + 1, zbronzerot, bronzerot);

      int xbronzelila = 332;
      int ybronzelila = 226;
      int zbronzelila = 393;

      world.dropItem(xbronzelila, ybronzelila + 1, zbronzelila, bronzelila);

      int xbronzegruen = 525;
      int ybronzegruen = 226;
      int zbronzegruen = 397;

      world.dropItem(xbronzegruen, ybronzegruen + 1, zbronzegruen, bronzegruen);

                       }
}
