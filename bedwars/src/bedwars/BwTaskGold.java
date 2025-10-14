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
  private Item customGold;

  public BwTaskGold(World myWorld) {
    super(Canary.getServer(), 25 * 20, true);
    world = myWorld;
    ItemFactory factory1 = Canary.factory().getItemFactory();
    customGold = factory1.newItem(ItemType.GoldIngot);
    customGold.setDisplayName(ChatFormat.GOLD + "Gold");
  }
    
  public void run() {
    int xGold1 = 430;
    int yGold1 = 229;
    int zGold1 = 396;
    world.dropItem(xGold1, yGold1 + 1, zGold1, customGold);

    int xGold2 = 431;
    int yGold2 = 227;
    int zGold2 = 395;
    world.dropItem(xGold2, yGold2 + 1, zGold2, customGold);

    int xGold3 = 428;
    int yGold3 = 227;
    int zGold3 = 395;
    world.dropItem(xGold3, yGold3 + 1, zGold3, customGold);
  }
}