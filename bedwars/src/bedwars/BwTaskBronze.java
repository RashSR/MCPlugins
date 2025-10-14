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
  private Item customBronze;

  public BwTaskBronze(World world) {
    super(Canary.getServer(), 2 * 20, true);
    this.world = world;
    ItemFactory factory = Canary.factory().getItemFactory();
    customBronze = factory.newItem(ItemType.ClayBrick);
    customBronze.setDisplayName(ChatFormat.GRAY + "Bronze");
  }
    
  public void run() {
    int xBronzeYellow = 427;
    int yBronzeYellow = 226;
    int zBronzeYellow = 493;
    world.dropItem(xBronzeYellow, yBronzeYellow + 1, zBronzeYellow, customBronze);

    int xBronzeRed = 430;
    int yBronzeRed = 226;
    int zBronzeRed = 298;
    world.dropItem(xBronzeRed, yBronzeRed + 1, zBronzeRed, customBronze);

    int xBronzePurple = 332;
    int yBronzePurple = 226;
    int zBronzePurple = 393;
    world.dropItem(xBronzePurple, yBronzePurple + 1, zBronzePurple, customBronze);

    int xBronzeGreen = 525;
    int yBronzeGreen = 226;
    int zBronzeGreen = 397;
    world.dropItem(xBronzeGreen, yBronzeGreen + 1, zBronzeGreen, customBronze);
  }
}