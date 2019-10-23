package fillblocks;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
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

public class FillBlocks extends EZPlugin implements PluginListener{

  public static boolean fill = false;
  int blockzahl = 0;
  int firstx;
  int firsty;
  int firstz;
  int secondx;
  int secondy;
  int secondz;
  int xmax;
  int xmin;
  int ymax;
  int ymin;
  int zmax;
  int zmin;
  int blockanzahl = 0;
  String msg1 = "[FillBlocks] ";

  @Override
    public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); 
                            }
  
  @Command(aliases = { "fillblocks" },
            description = "fillblocks plugin",
            permissions = { "*" },
            toolTip = "/fillblocks")
  public void fillblocksCommand(MessageReceiver caller, String[] args) {
    if (caller instanceof Player) { 
      Player player = (Player)caller;

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
      if(args.length == 2){

        if(args[1].equalsIgnoreCase("reset")){
          resetPlugin();
          blockanzahl = 0;
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.RED + "Plugin wurde zurueckgesetzt.");

                          }
      }

                                   }
                                                                             }
  @HookHandler
   public void blockdetectleft(BlockLeftClickHook event){
    if(fill){
    blockzahl = blockzahl + 1;
    Block geklickterblock = event.getBlock();
    Player player = event.getPlayer();

    if(blockzahl == 1){

      firstx = geklickterblock.getX();
      firsty = geklickterblock.getY();
      firstz = geklickterblock.getZ();
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.GOLD + "Startblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + ChatFormat.GOLD + firstx + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firsty + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstz + ChatFormat.DARK_GREEN + ".");
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Geben sie den " + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " an." );
                     }

    if(blockzahl == 2){

      secondx = geklickterblock.getX();
      secondy = geklickterblock.getY();
      secondz = geklickterblock.getZ();
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + ChatFormat.GOLD + secondx + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondy + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondz + ChatFormat.DARK_GREEN + ".");
      howmuchblocks(firstx, firsty, firstz, secondx, secondy, secondz);

                      }
    
            }
                                                      }
  @HookHandler
   public void blockdetectright(BlockRightClickHook event){
    if(fill){
    blockzahl = blockzahl + 1;
    Block geklickterblock = event.getBlockClicked();
    Player player = event.getPlayer();

    if(blockzahl == 1){

      firstx = geklickterblock.getX();
      firsty = geklickterblock.getY();
      firstz = geklickterblock.getZ();
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.GOLD + "Startblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + ChatFormat.GOLD + firstx + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firsty + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + firstz + ChatFormat.DARK_GREEN + ".");
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Geben sie den " + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " an." );

                     }

    if(blockzahl == 2){

      secondx = geklickterblock.getX();
      secondy = geklickterblock.getY();
      secondz = geklickterblock.getZ();
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.GOLD + "Zielblock" + ChatFormat.DARK_GREEN + " hat die Koordinaten " + ChatFormat.GOLD + secondx + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondy + ChatFormat.DARK_GREEN + ", " + ChatFormat.GOLD + secondz + ChatFormat.DARK_GREEN + ".");
      howmuchblocks(firstx, firsty, firstz, secondx, secondy, secondz);

                      }
    if(blockzahl == 3){

      blockChange(geklickterblock.getType());

    }
    
            }
                                                      }

  @HookHandler
   public void geklickterblock(ItemUseHook event){
    Player player = event.getPlayer();

    if(player.getItemHeld().getType() == ItemType.Bone && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Reset") && fill){

      resetPlugin();
      blockanzahl = 0;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.RED + "Plugin wurde zurueckgesetzt.");

    }

    if(!fill){
      if(player.getItemHeld().getType() == ItemType.Stick && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.GREEN + "Neustart")){

        startPlugin();

                                                        }
    }

    if(fill && blockzahl >= 2){
    if(player.getItemHeld().getType() == ItemType.BlazeRod && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.BLUE + "Bloecke zu Luft")){

      blockChange(BlockType.Air);

                                                           }
                             }

                                                 }

  public void howmuchblocks(int x1, int y1, int z1, int x2, int y2, int z2){

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
          blockanzahl = blockanzahl + 1;
          Location loc = new Location(x, y, z);
        }
      }
    }
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + blockanzahl + ChatFormat.DARK_GREEN + " Bloecke erfasst.");

                                                                              }
  public void startPlugin(){

    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "FillBlocks ist jetzt " + ChatFormat.GOLD + "aktiviert" + ChatFormat.DARK_GREEN + ". \nGeben sie den" + ChatFormat.GOLD + "Startblock" + ChatFormat.DARK_GREEN + " an." );
    fill = true;
    blockanzahl = 0;

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
    Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Es wurden " + ChatFormat.GOLD + blockanzahl + ChatFormat.DARK_GREEN + " Bloecke ersetzt. \nFillBlocks ist jetzt " + ChatFormat.GOLD + "deaktiviert" + ChatFormat.DARK_GREEN + ".");
   }
}
