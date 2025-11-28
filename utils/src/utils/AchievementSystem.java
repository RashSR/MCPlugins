package utils;
import net.canarymod.Canary;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.inventory.CustomStorageInventory;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;

public class AchievementSystem<E extends Enum<E> & IDescriableAchievment>{
    //TODO: add pagination if too much achievements are present, test if a hook is possible to autodetect if it is closing
    private final Player player;
    private final E[] values;
    private final String pluginName;
    private Location fallbackLocation;
    private boolean isCustomInventoryOpen;
    private final ObjectFactory objectFactory;
    private final ItemFactory itemFactory;

    private final ItemType COLLECTED_ACHIEVEMENT_TYPE = ItemType.LimeDye;
    private final ItemType UNCOLLECTED_ACHIEVEMENT_TYPE = ItemType.GrayDye;
    
    public AchievementSystem(Player player, Class<E> enumClass, String pluginName, Location fallbackLocation){
        this.player = player;
        this.fallbackLocation = fallbackLocation;
        this.pluginName = pluginName;
        values = enumClass.getEnumConstants();
        objectFactory = Canary.factory().getObjectFactory();
        itemFactory = Canary.factory().getItemFactory();
        isCustomInventoryOpen = false;
    }

    public void displayAchievementInventory(DatabaseUtils database, boolean hasObscuredName){
        int inventoryRows = (values.length + 8) / 9; //This ensures correct size for the custom Inventory
        CustomStorageInventory customInventory = objectFactory.newCustomStorageInventory(ChatFormat.DARK_AQUA + "Achievements", inventoryRows);
        fillInventoryWithAchievements(customInventory, database, hasObscuredName);
        player.openInventory(customInventory);
        isCustomInventoryOpen = true;
    }

    public boolean IsAchievementInventoryOpen(){
        return isCustomInventoryOpen;
    }

    public void AchievementInventoryIsClosed(){
        isCustomInventoryOpen = false;
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

    public void tryEarnAchievement(E achievement, DatabaseUtils database){
        if(!database.hasPlayerQuidditchAchievement(player.getDisplayName(), achievement.toString())){
            database.InsertQuidditchAchievementIntoDbForPlayer(player.getDisplayName(), achievement.toString());

            String serverMessage = "Du hast das Achievement " + ChatFormat.GOLD + achievement.toString() + ChatFormat.DARK_GREEN + " erspielt!";
            Utils.BroadcastServerMessage(pluginName, serverMessage);
            //Play sounds on both location because it is not clear how fast the teleport gets executed
            Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ORB, 1.0f, 0.9f);
            Utils.playSoundAtLocation(fallbackLocation, SoundEffect.Type.ORB, 1.0f, 0.9f);
        }
    }
}
