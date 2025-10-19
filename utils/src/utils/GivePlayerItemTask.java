package utils;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.effects.SoundEffect;

public class GivePlayerItemTask extends ServerTask{
    private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;

    private Player player;
    private Item item;
    private int itemSlot;
    private int elapsedTimeInSeconds;
    private int itemGiveTimeInSeconds;
    private IServerTaskCallback callback;

    public GivePlayerItemTask(Player player, Item item, int itemSlot, int itemGiveTimeInSeconds, IServerTaskCallback callback) {
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.player = player;
        this.item = item;
        this.itemSlot = itemSlot;
        this.elapsedTimeInSeconds = 0;
        this.itemGiveTimeInSeconds = itemGiveTimeInSeconds;
        this.callback = callback;
    }

    public void run(){
        elapsedTimeInSeconds++;
        if(elapsedTimeInSeconds >= itemGiveTimeInSeconds){
            player.getInventory().setSlot(itemSlot, item);
            Utils.playSoundAtLocation(player.getLocation(), SoundEffect.Type.ITEM_PICKUP, 1.5f, 1.0f);
            Canary.getServer().removeSynchronousTask(this);
            if(callback != null)
                callback.ExecuteTaskCallback();
        }
    }
}