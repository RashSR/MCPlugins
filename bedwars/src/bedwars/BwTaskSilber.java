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
  private Item customIron;

  public BwTaskSilber(World myWorld) {
    super(Canary.getServer(), 17 * 20, true);
    world = myWorld;
    ItemFactory factory = Canary.factory().getItemFactory();
    Item customIron = factory.newItem(ItemType.IronIngot);
    customIron.setDisplayName(ChatFormat.DARK_AQUA + "Eisen");
  }
    
  public void run() {
    int xIronYellow = 436;
    int yIronYellow = 227;
    int zIronYellow = 502;
    world.dropItem(xIronYellow, yIronYellow + 1, zIronYellow, customIron);

    int xIronRed = 421;
    int yIronRed = 227;
    int zIronRed = 289;
    world.dropItem(xIronRed, yIronRed + 1, zIronRed, customIron);

    int xIronPurple = 323;
    int yIronPurple = 227;
    int zIronPurple = 402;
    world.dropItem(xIronPurple, yIronPurple + 1, zIronPurple, customIron);

    int xIronGreen = 534;
    int yIronGreen = 227;
    int zIronGreen = 388;
    world.dropItem(xIronGreen, yIronGreen + 1, zIronGreen, customIron);
  }
}