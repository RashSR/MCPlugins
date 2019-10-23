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

public class BwTaskGold extends ServerTask {

    private World world;

    public BwTaskGold(World myWorld) {

        super(Canary.getServer(), 25 * 20, true);
        world = myWorld;

                                  }
    
    public void run() {

      ItemFactory factory1 = Canary.factory().getItemFactory();
      Item gold1 = factory1.newItem(ItemType.GoldIngot);
      gold1.setDisplayName(ChatFormat.GOLD + "Gold");

      ItemFactory factory2 = Canary.factory().getItemFactory();
      Item gold2 = factory2.newItem(ItemType.GoldIngot);
      gold2.setDisplayName(ChatFormat.GOLD + "Gold");

      ItemFactory factory3 = Canary.factory().getItemFactory();
      Item gold3 = factory3.newItem(ItemType.GoldIngot);
      gold3.setDisplayName(ChatFormat.GOLD + "Gold");

      int xgold1 = 430;
      int ygold1 = 229;
      int zgold1 = 396;

      world.dropItem(xgold1, ygold1 + 1, zgold1, gold1);

      int xgold2 = 431;
      int ygold2 = 227;
      int zgold2 = 395;

      world.dropItem(xgold2, ygold2 + 1, zgold2, gold2);

      int xgold3 = 428;
      int ygold3 = 227;
      int zgold3 = 395;

      world.dropItem(xgold3, ygold3 + 1, zgold3, gold3);

                       }
}
