package fillblocks;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.player.BlockLeftClickHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.World;
import utils.Utils;

public class FillBlocks extends EZPlugin implements PluginListener{

  public enum PluginState {
    NOT_ENABLED, ENABLED, FIRST_BLOCK_SELECTED, SECOND_BLOCK_SELECTED
  }

  private Block firstBlock = null;
  private Block secondBlock = null;
  private PluginState state = PluginState.NOT_ENABLED;
  private static final String pluginName = "[FillBlocks]";

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
  }
  
  @Command(aliases = { "fillblocks" },
            description = "fillblocks plugin",
            permissions = { "*" },
            toolTip = "/fillblocks")
  public void FillBlocksCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player player) { 
      if(args.length == 1){
        startPlugin();

        ItemFactory factory = Canary.factory().getItemFactory();
        Item airItem = factory.newItem(ItemType.BlazeRod);
        Item restartItem = factory.newItem(ItemType.Stick);
        Item resetItem = factory.newItem(ItemType.Bone);

        airItem.setDisplayName(ChatFormat.BLUE + "Bloecke zu Luft");
        restartItem.setDisplayName(ChatFormat.GREEN + "Neustart");
        resetItem.setDisplayName(ChatFormat.RED + "Reset");

        player.getInventory().setSlot(0, restartItem);
        player.getInventory().setSlot(1, airItem);
        player.getInventory().setSlot(8, resetItem);                   
      }
      else if(args.length == 2 && args[1].equalsIgnoreCase("reset"))
        resetPlugin();
    }
  }

  @HookHandler
  public void BlockLeftClickHookEvent(BlockLeftClickHook event){
    if(this.state != PluginState.NOT_ENABLED){
      Block clickedBlock = event.getBlock();
      handleBlockSelection(clickedBlock);
    }
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    if(this.state != PluginState.NOT_ENABLED){
      Block clickedBlock = event.getBlockClicked();
      handleBlockSelection(clickedBlock);
    }
  }

  private void handleBlockSelection(Block clickedBlock){
    if(this.state == PluginState.ENABLED){
      firstBlock = clickedBlock;
      this.state = PluginState.FIRST_BLOCK_SELECTED;
      String serverMessage = ChatFormat.GOLD + "Startblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + 
        ChatFormat.GOLD + firstBlock.getX() + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstBlock.getY() + 
        ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstBlock.getZ() + ChatFormat.DARK_GREEN + ".";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      serverMessage = ChatFormat.DARK_GREEN + "Geben sie den " + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " an.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
    }
    else if(this.state == PluginState.FIRST_BLOCK_SELECTED){
      secondBlock = clickedBlock;
      this.state = PluginState.SECOND_BLOCK_SELECTED;
      String serverMessage = ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " +
        ChatFormat.GOLD + secondBlock.getX() + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondBlock.getY() +
        ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondBlock.getZ() + ChatFormat.DARK_GREEN + ".";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      int totalBlocks = Utils.CalculateBlockCountInVolume(firstBlock, secondBlock);
      serverMessage = ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + totalBlocks + ChatFormat.DARK_GREEN + " Bloecke erfasst.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
      serverMessage = ChatFormat.DARK_GREEN + "Geben sie den " + ChatFormat.GOLD + "BlockTyp" + ChatFormat.DARK_GREEN + 
        " an. Klicken sie hierfür auf den gewünschten Block oder das BlazeRod Item in der Hotbar.";
      Utils.BroadcastServerMessage(pluginName, serverMessage);
    }
    //Is needed to determine to which blocktype the selected blocks are changed
    else if(this.state == PluginState.SECOND_BLOCK_SELECTED){
      changeBlocks(clickedBlock.getType(), clickedBlock);
    }
  }

  @HookHandler
  public void ItemUseHookEvent(ItemUseHook event){
    Player player = event.getPlayer();
    Item heldItem = player.getItemHeld();

    if(heldItem.getType() == ItemType.Stick && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Neustart"))
        startPlugin();
    else if(this.state != PluginState.NOT_ENABLED){
      if(heldItem.getType() == ItemType.Bone && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Reset"))
        resetPlugin();
      else if(heldItem.getType() == ItemType.BlazeRod && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.BLUE + "Bloecke zu Luft") && this.state == PluginState.SECOND_BLOCK_SELECTED)
        changeBlocks(BlockType.Air, null);
    }
  }
  
  public void startPlugin(){
    String serverMessage = ChatFormat.DARK_GREEN + "FillBlocks ist jetzt " + ChatFormat.GOLD + 
    "aktiviert" + ChatFormat.DARK_GREEN + ". \nGeben sie den" + ChatFormat.GOLD + 
    "Startblock" + ChatFormat.DARK_GREEN + " an.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    this.state = PluginState.ENABLED;
  }

  public void resetPlugin(){
    this.state = PluginState.NOT_ENABLED;
    Utils.BroadcastServerMessage(pluginName, ChatFormat.RED + "Plugin wurde zurueckgesetzt.");
  }

  private void changeBlocks(BlockType blockType, Block clickedBlock){
    int blocksChanged = changeVolumeToBlockType(blockType, clickedBlock);

    //the result of getMachineName is e.g. "minecraft:grass" -> show user only the blockType after minecraft:
    String typeString = ChatFormat.GOLD + blockType.getMachineName().split(":")[1];
    String serverMessage = ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + blocksChanged + 
      ChatFormat.DARK_GREEN + " Bloecke durch " + typeString + ChatFormat.DARK_GREEN + 
      " ersetzt. \nFillBlocks ist jetzt " + ChatFormat.GOLD + "deaktiviert" + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    resetPlugin();
  }

  private int changeVolumeToBlockType(BlockType blockType, Block clickedBlock){
    int xmin = Math.min(firstBlock.getX(), secondBlock.getX());
    int xmax = Math.max(firstBlock.getX(), secondBlock.getX());
    int ymin = Math.min(firstBlock.getY(), secondBlock.getY());
    int ymax = Math.max(firstBlock.getY(), secondBlock.getY());
    int zmin = Math.min(firstBlock.getZ(), secondBlock.getZ());
    int zmax = Math.max(firstBlock.getZ(), secondBlock.getZ());

    for(int x = xmin; x <= xmax; x++){
      for(int y = ymin; y <= ymax; y++){
        for(int z = zmin; z <= zmax; z++){
          Location loc = new Location(x, y, z);
          World world = loc.getWorld();
          world.setBlockAt(loc, blockType);
          if(clickedBlock != null && clickedBlock.getData() != 0){
            Block placedBlock = world.getBlockAt(x, y, z);
            placedBlock.setData(clickedBlock.getData());
            placedBlock.update();
          }
        }
      }
    }

    int blocksChanged = (xmax - xmin + 1) * (ymax - ymin + 1) * (zmax - zmin + 1);
    return blocksChanged;
  }
}