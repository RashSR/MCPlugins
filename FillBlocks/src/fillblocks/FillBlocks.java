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
import utils.Utils;

public class FillBlocks extends EZPlugin implements PluginListener{

  private static boolean fill = false;
  private Block firstBlock = null;
  private Block secondBlock = null;
  int blockzahl = 0;
  int xmax;
  int xmin;
  int ymax;
  int ymin;
  int zmax;
  int zmin;
  private int totalBlocks = 0;
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
        Item blazerodair = factory.newItem(ItemType.BlazeRod);
        Item restartstick = factory.newItem(ItemType.Stick);
        Item resetbone = factory.newItem(ItemType.Bone);

        blazerodair.setDisplayName(ChatFormat.BLUE + "Bloecke zu Luft");
        restartstick.setDisplayName(ChatFormat.GREEN + "Neustart");
        resetbone.setDisplayName(ChatFormat.RED + "Reset");

        player.getInventory().setSlot(0, restartstick);
        player.getInventory().setSlot(1, blazerodair);
        player.getInventory().setSlot(8, resetbone);                     
      }
      else if(args.length == 2){
        if(args[1].equalsIgnoreCase("reset")){
          resetPlugin();
          totalBlocks = 0;
          Utils.BroadcastServerMessage(pluginName, ChatFormat.RED + "Plugin wurde zurueckgesetzt.");
        }
      }
    }
  }

  @HookHandler
  public void BlockLeftClickHookEvent(BlockLeftClickHook event){
    if(fill){
      Block clickedBlock = event.getBlock();
      handleBlockSelection(clickedBlock);
    }
  }

  @HookHandler
  public void BlockRightClickHookEvent(BlockRightClickHook event){
    if(fill){
      Block clickedBlock = event.getBlockClicked();
      handleBlockSelection(clickedBlock);

      //Is needed to determine to which blocktype the selected blocks are changed
      if(blockzahl == 3)
        blockChange(clickedBlock.getType());
    }
 }

 private void handleBlockSelection(Block clickedBlock){
  blockzahl++;

  if(blockzahl == 1){
    firstBlock = clickedBlock;
    String serverMessage = ChatFormat.GOLD + "Startblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + 
      ChatFormat.GOLD + firstBlock.getX() + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstBlock.getY() + 
      ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstBlock.getZ() + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    serverMessage = ChatFormat.DARK_GREEN + "Geben sie den " + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " an.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }
  else if(blockzahl == 2){
    secondBlock = clickedBlock;
    String serverMessage = ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " +
      ChatFormat.GOLD + secondBlock.getX() + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondBlock.getY() +
      ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondBlock.getZ() + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    howmuchblocks();
  }
 }

  @HookHandler
   public void ItemUseHookEvent(ItemUseHook event){
    Player player = event.getPlayer();
    Item heldItem = player.getItemHeld();

    if(heldItem.getType() == ItemType.Bone && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Reset") && fill){
      resetPlugin();
      totalBlocks = 0;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.RED + "Plugin wurde zurueckgesetzt.");
    }

    if(!fill){
      if(heldItem.getType() == ItemType.Stick && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Neustart"))
        startPlugin();
    }

    if(fill && blockzahl >= 2){
      if(heldItem.getType() == ItemType.BlazeRod && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.BLUE + "Bloecke zu Luft")){
        blockChange(BlockType.Air);
      }
    }
  }

  private void howmuchblocks(){
    int x1 = firstBlock.getX();
    int y1 = firstBlock.getY();
    int z1 = firstBlock.getZ();

    int x2 = firstBlock.getX();
    int y2 = firstBlock.getY();
    int z2 = firstBlock.getZ();

    if(x1 > x2){
      xmax = x1;
      xmin = x2;
    }
    else{
      xmax = x2;
      xmin = x1;
    }

    if(y1 > y2){
      ymax = y1;
      ymin = y2;
    }
    else{
      ymax = y2;
      ymin = y1;
    }

    if(z1 > z2){
      zmax = z1; 
      zmin = z2;
    }
    else{
      zmax = z2;
      zmin = z1;
    }

    for(int x = xmin; x <= xmax; x++){
      for(int y = ymin; y <= ymax; y++){
        for(int z = zmin; z <= zmax; z++){
          totalBlocks = totalBlocks + 1;
          Location loc = new Location(x, y, z);
        }
      }
    }
    String serverMessage = ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + totalBlocks + ChatFormat.DARK_GREEN + " Bloecke erfasst.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
  }

  public void startPlugin(){
    String serverMessage = ChatFormat.DARK_GREEN + "FillBlocks ist jetzt " + ChatFormat.GOLD + 
    "aktiviert" + ChatFormat.DARK_GREEN + ". \nGeben sie den" + ChatFormat.GOLD + 
    "Startblock" + ChatFormat.DARK_GREEN + " an.";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
    fill = true;
    totalBlocks = 0;
  }

  public void resetPlugin(){
    fill = false;
    blockzahl = 0;
  }

  public void blockChange(BlockType newblock){
    for(int x = xmin; x <= xmax; x++){
      for(int y = ymin; y <= ymax; y++){
        for(int z = zmin; z <= zmax; z++){
          Location loc = new Location(x, y, z);
          loc.getWorld().setBlockAt(loc, newblock);
        }
      }
    }

    resetPlugin();
    String serverMessage = ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + 
      totalBlocks + ChatFormat.DARK_GREEN + " Bloecke ersetzt. \nFillBlocks ist jetzt " + 
      ChatFormat.GOLD + "deaktiviert" + ChatFormat.DARK_GREEN + ".";
    Utils.BroadcastServerMessage(pluginName, serverMessage);
   }
}