package utils;
import net.canarymod.Canary;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.inventory.CustomStorageInventory;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;

public class AchievementSystem<E extends Enum<E> & IDescriableAchievment>{
    //TODO: add methods like displayAchievementEarnMessage, insertAchievementIntoDb, tryEarnAchievement
    private final E[] values;
    private final ObjectFactory objectFactory;
    private final ItemFactory itemFactory;
    private final Player player;
    private final ItemType COLLECTED_ACHIEVEMENT_TYPE = ItemType.LimeDye;
    private final ItemType UNCOLLECTED_ACHIEVEMENT_TYPE = ItemType.GrayDye;

    public AchievementSystem(Player player, Class<E> enumClass){
        this.player = player;
        this.values = enumClass.getEnumConstants();

        this.objectFactory = Canary.factory().getObjectFactory();
        this.itemFactory = Canary.factory().getItemFactory();
    }

    public void displayAchievementInventory(DatabaseUtils database, boolean hasObscuredName){
        int inventoryRows = (values.length + 8) / 9; //This ensures correct size for the custom Inventory
        CustomStorageInventory customInventory = objectFactory.newCustomStorageInventory(ChatFormat.DARK_AQUA + "Achievements", inventoryRows);
        fillInventoryWithAchievements(customInventory, database, hasObscuredName);
        player.openInventory(customInventory);
    }

    private void fillInventoryWithAchievements(CustomStorageInventory customInventory, DatabaseUtils database, boolean hasObscuredName){
        for(int i = 0; i < values.length; i++){
            Item item;
            E achievement = values[i];
            
            //TODO: generic interface -> hasAchievement()
            if(database.hasPlayerQuidditchAchievement(player.getDisplayName(), achievement.toString())){ 
                item = itemFactory.newItem(COLLECTED_ACHIEVEMENT_TYPE);
                item.setDisplayName(ChatFormat.GREEN + achievement.toString() + " - " + achievement.getDescription());
            }
            else{
                item = itemFactory.newItem(UNCOLLECTED_ACHIEVEMENT_TYPE);
                if(hasObscuredName)
                    item.setDisplayName(ChatFormat.RED + "Noch nicht erspielt!");
                else
                    item.setDisplayName(ChatFormat.RED + achievement.toString());
            }

            customInventory.setSlot(i, item);
        }

        database.CloseConnection();
    }
}
