package utils;
import net.canarymod.tasks.ServerTask;
import java.util.ArrayList;
import net.canarymod.Canary;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.factory.ItemFactory;

public class SpawnItemsTask extends ServerTask{
    private ItemFactory factory;
    private ArrayList<Location> locations;
    private ItemType itemType;
    private String customName;
    
    public SpawnItemsTask(Item item, ArrayList<Location> locations, int delayInSeconds, boolean isContinousTask) {
        super(Canary.getServer(), delayInSeconds * Utils.TICKS_PER_SECOND, isContinousTask);
        this.factory = Canary.factory().getItemFactory();
        this.itemType = item.getType();
        this.customName = item.getDisplayName();
        this.locations = locations;
    }

    public void run(){
        for(Location loc : locations){
            //It is necessary to create a new Item each time. If not -> the item does not spawn or can not be picked up 
            Item customItem = factory.newItem(itemType);
            if(customName != null || !customName.isEmpty())
                customItem.setDisplayName(customName);
            loc.getWorld().dropItem((int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), customItem);
        }
    }
}
