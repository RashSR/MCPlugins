package schneeballschlacht;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.hook.entity.DamageHook;
import net.canarymod.api.DamageSource;
import net.canarymod.api.DamageType;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.hook.entity.ProjectileHitHook;
import net.canarymod.api.entity.living.*;
import net.canarymod.hook.player.HealthChangeHook;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.World;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import net.canarymod.hook.world.RedstoneChangeHook;
import net.canarymod.hook.player.TeleportHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.DisconnectionHook;
import utils.Utils;

public class Schneeballschlacht extends EZPlugin implements PluginListener {

	private List<Player> playerList = new ArrayList<Player>();
	private Map<String,String> teamColor = new HashMap<String,String>();
	private boolean hasGameStarted = false;
	private boolean hasNoFallDamage;
	private boolean isFallDamageClicked = false; //ob einstellung schon ausgewählt wurde
	private boolean isSnowballDamageClicked =false;
	private boolean isPVPGame;
	private boolean isPvpClicked = false;
	private boolean has2Players; //true = 2 Spieler
	private boolean isPlayer2Clicked = false;
	private boolean isColorPicked = false;
	private int DAMAGE_SNOWBALL = 2;

	protected static final String pluginName = "[Schneeballschlacht]";
	private final Location SnowballHubLocation = new Location(35, 67, 259);
	private final int TELEPORT_DELAY_IN_SECONDS = 10;

	@Override
	public boolean enable() {  
		Canary.hooks().registerListener(this, this);
		return super.enable();
	}  

	@Command(aliases = { "schneeballarena"},
		description = "Teleportiert den Spieler zur Schneeballarena_hub.",
		permissions = {""},
		toolTip = "/schneeballarena, or /schneeballarena map <mapname>, or /schneeballarena maps")
  	public void TeleportToSnowballHub(MessageReceiver caller, String[] args) {
		if (caller instanceof Player player) {
			if(args.length == 1){
				player.teleportTo(SnowballHubLocation);
				teamColor.put(player.getDisplayName(), "");
				return;
			}
			if(args.length == 2 && args[1].equalsIgnoreCase("maps")){
				String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
				Utils.BroadcastServerMessage(pluginName, msg2 + ChatFormat.GOLD + "Arena" + ChatFormat.DARK_GREEN + ".");
			}
			if(args[1].equalsIgnoreCase("map") && args[2].equalsIgnoreCase("arena")){
				Location arenamap = new Location(28, 110, 254);
				player.teleportTo(arenamap);
			}
		}
	}

	@HookHandler
	public void ConnectionHookEvent(ConnectionHook event){
		if(hasGameStarted){
			Player player = event.getPlayer();
			double x = player.getX();
			double z = player.getZ();
			if(x>26 && x<36 && z>256 && z<266){
				teamColor.put(player.getDisplayName(), "");
			}
		}
	}

 	@HookHandler
  	public void DisconnectionHookEvent(DisconnectionHook event){
		if(hasGameStarted){
			Player player = event.getPlayer();
			double x = player.getX();
			double z = player.getZ();
			if(x>26 && x<36 && z>256 && z<266){
			teamColor.remove(player.getDisplayName());
			player.setPrefix(ChatFormat.WHITE + "");
			}
		}
  	}

 	@HookHandler
 	public void RedstoneChangeHookEvent(RedstoneChangeHook event){
		Block pressurePlate = event.getSourceBlock();
		int dx = pressurePlate.getX();
		int dy = pressurePlate.getY();
		int dz = pressurePlate.getZ();
		World world = pressurePlate.getWorld();

		if(dx == 245 && dy == 71 && dz == 259){
			Player player = world.getClosestPlayer(245, 71, 259, 5);
			teamColor.put(player.getDisplayName(), "");
		}

		if(dx == 27 && dy == 67 && dz == 265){
			Player player = world.getClosestPlayer(27, 67, 265, 5);
			teamColor.remove(player.getDisplayName());
			player.setPrefix(ChatFormat.WHITE + "");
		}
	}
  
  	@HookHandler
 	public void TeleportHookEvent(TeleportHook event){
		Player player = event.getPlayer();
		Location ausgangloc = event.getCurrentLocation();
		double xa = ausgangloc.getX();
		double za = ausgangloc.getZ();
		World world = ausgangloc.getWorld();
		Location zielloc = event.getDestination();
		double xz = zielloc.getX();
		double zz = zielloc.getZ();

		if (xa >= 26 && xa <= 36 && za >= 256 && za <= 266){
			if(xz>12&&xz<45){
				if(zz>221 && zz<285){
					return;
				}
			}
			else{
			teamColor.remove(player.getDisplayName());
			player.setPrefix(ChatFormat.WHITE + "");
			}
		}
	}
 

	@HookHandler
	public void ProjectileHitHookEvent(ProjectileHitHook event){
		if(hasGameStarted){
			Entity schneeball = event.getProjectile();
			Entity ent = event.getEntityHit();

			if (schneeball.getEntityType() == EntityType.SNOWBALL) {
				if(ent instanceof Player player){
					if(player.getHealth() > DAMAGE_SNOWBALL){
						player.setHealth(player.getHealth() - DAMAGE_SNOWBALL);
						return;
					}
					if(player.getHealth() <= DAMAGE_SNOWBALL){
						Utils.BroadcastServerMessage(pluginName, getTeamColorFromPlayer(player) + ChatFormat.DARK_GREEN + " ist gestorben.");
						player.teleportTo(SnowballHubLocation);
						resetGame();
					}
				}
			}
		}
	}


  	@HookHandler
  	public void HealthChangeHookEvent(HealthChangeHook event){
		if(hasGameStarted){
			Player player = event.getPlayer();
			float healthBefore = event.getOldValue();
			float healthAfter = event.getNewValue();
			if(healthAfter > healthBefore && healthBefore > 0 && hasGameStarted){
				player.setHealth(healthBefore);
			}	
		}
	}

  	@HookHandler
  	public void DamageHookEvent(DamageHook event) {
		if(hasGameStarted){
			Entity defender = event.getDefender();
			Entity attacker = event.getAttacker();

			if (defender instanceof Player player) {
				if(event.getDamageDealt()>=player.getHealth()){
					event.setDamageDealt(0f);
					Utils.BroadcastServerMessage(pluginName, getTeamColorFromPlayer(player) + ChatFormat.DARK_GREEN +" ist gestorben.");
					player.teleportTo(SnowballHubLocation);
					resetGame();
				}
				if(attacker instanceof Player){
					event.setCanceled();
				}
				if (event.getDamageSource().getDamagetype() == DamageType.FALL && hasNoFallDamage) {
					event.setCanceled();
				}
			}
		}
	}

    @HookHandler
  	public void BlockRightClickHookEvent(BlockRightClickHook event){

    Block geklickterblock = event.getBlockClicked();
    Location lampfall = new Location(25, 68, 261);
    Location lampeasyhard = new Location(31, 68, 255);
    Location lampplayer12 = new Location(37, 68, 261);
    Location lamppvppve = new Location(31, 68, 267);
    BlockType lampean = BlockType.EmeraldBlock;
    BlockType lampeaus = BlockType.RedstoneBlock;
    int x = geklickterblock.getX();
    int y = geklickterblock.getY();
    int z = geklickterblock.getZ();
    Player player = event.getPlayer();

    if(teamColor.size() > 1){

      playerList = Canary.getServer().getPlayerList();

                            }

    if(y==67){// alle bei x = 37
      if(z==260){
      	if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("purple")){
      		          if(teamColor.size() > 1){
            for(Player spieler : playerList){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("purple")){

                  displayTeamAlreadyUsedMessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

      	teamColor.put(player.getDisplayName(), "purple");
      	isColorPicked = true;
        player.setPrefix(ChatFormat.DARK_PURPLE + "");
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_PURPLE + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.DARK_PURPLE + "lila" + ChatFormat.DARK_GREEN + ".");
      	startGame();
      }}

      if(z==262){
      	if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("yellow")){

      	if(teamColor.size() > 1){
            for(Player spieler : playerList){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("yellow")){

                  displayTeamAlreadyUsedMessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

      	teamColor.put(player.getDisplayName(), "yellow");
      	isColorPicked = true;
        player.setPrefix(ChatFormat.YELLOW + "");
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.YELLOW + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.YELLOW + "gelb" + ChatFormat.DARK_GREEN + ".");
      	startGame();
      }}         
    }

    if(y==69){
    	if(z==260){
    		if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("red")){
				if(teamColor.size() > 1){
					for(Player spieler : playerList){
					double xs = spieler.getX();
					double zs = spieler.getZ();
						if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
							if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("red")){
								displayTeamAlreadyUsedMessage();
								return;
							}
						}
					}
				} 
    	  teamColor.put(player.getDisplayName(), "red");
    	  isColorPicked = true;
          player.setPrefix(ChatFormat.RED + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.RED + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.RED + "rot" + ChatFormat.DARK_GREEN + ".");
    	  startGame();
    	}}
    	if(z==262){
    		if(!teamColor.get(player.getDisplayName()).equalsIgnoreCase("green")){
				if(teamColor.size() > 1){
					for(Player spieler : playerList){
						double xs = spieler.getX();
						double zs = spieler.getZ();
						if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
							if(teamColor.get(spieler.getDisplayName()).equalsIgnoreCase("green")){
								displayTeamAlreadyUsedMessage();
								return;
							}
						}
					}
				} 
				teamColor.put(player.getDisplayName(), "green");
				isColorPicked = true;
				player.setPrefix(ChatFormat.DARK_GREEN + "");
				Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.GREEN + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.GREEN + "gruen" + ChatFormat.DARK_GREEN + ".");
				startGame();
			}		
		}
    } 

    if(geklickterblock.getType() == BlockType.WallSign){
    	
    	if(isFallDamageClicked){
     if(x == 26 && z == 262 && hasNoFallDamage){
      hasNoFallDamage = false;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Fallschaden ist " + ChatFormat.GOLD + "an" + ChatFormat.DARK_GREEN +".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
           }
      if(x == 26 && z == 260 && !hasNoFallDamage){     
      hasNoFallDamage = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " +ChatFormat.DARK_GREEN + "Fallschaden ist " +ChatFormat.GOLD + "aus" + ChatFormat.DARK_GREEN + ".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
  }}
  if(!isFallDamageClicked){
  	     if(x == 26 && z == 262){
      hasNoFallDamage = false;
      isFallDamageClicked = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Fallschaden ist " + ChatFormat.GOLD + "an" + ChatFormat.DARK_GREEN +".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
           }
      if(x == 26 && z == 260){     
      hasNoFallDamage = true;
      isFallDamageClicked = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " +ChatFormat.DARK_GREEN + "Fallschaden ist " +ChatFormat.GOLD + "aus" + ChatFormat.DARK_GREEN + ".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
  }
  }
  	if(isSnowballDamageClicked){
  	  if(x == 30 && z == 256 && DAMAGE_SNOWBALL !=2){
  	  	DAMAGE_SNOWBALL = 2;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Herz Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  if(x == 32 && z == 256 && DAMAGE_SNOWBALL !=4){
  	  	DAMAGE_SNOWBALL = 4;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Herzen Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }}
  	  if(!isSnowballDamageClicked){
  	  	  	  if(x == 30 && z == 256){
  	  	DAMAGE_SNOWBALL = 2;
  	  	isSnowballDamageClicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Herz Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  if(x == 32 && z == 256){
  	  	DAMAGE_SNOWBALL = 4;
  	  	isSnowballDamageClicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Herzen Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  }
  	  if(isPlayer2Clicked){
  	  if(x == 36 && z == 262 && !has2Players){
  	  	has2Players=true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielen " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }
  	  if(x == 36 && z == 260 && has2Players){
  	  	has2Players=false;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielt " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }}
  	  if(!isPlayer2Clicked){
  	   	  if(x == 36 && z == 262){
  	  	has2Players=true;
  	  	isPlayer2Clicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielen " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }
  	  if(x == 36 && z == 260){
  	  	has2Players=false;
  	  	isPlayer2Clicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielt " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  } 	
  	  }
  	  if(isPvpClicked){
  	  if(x == 32 && z == 266 && !isPVPGame){
  	  	isPVPGame = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }
  	  if(x == 30 && z == 266 && isPVPGame){
  	  	isPVPGame = false;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVE" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }}
  	  if(!isPvpClicked){

  	  if(x == 32 && z == 266){
  	  	isPVPGame = true;
  	  	isPvpClicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }
  	  if(x == 30 && z == 266){
  	  	isPVPGame = false;
  	  	isPvpClicked = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVE" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }}
  	  startGame();
  	  }
	}
        

	private void resetRedstoneBlocks(){
		Location redstoneLampFallDamageLocation = new Location(25, 68, 261);
		Location redstoneLampDifficultyLocation = new Location(31, 68, 255);
		Location redstoneLampPlayerCountLocation = new Location(37, 68, 261);
		Location redstoneLampPVPPVELocation = new Location(31, 68, 267);
		BlockType redstoneLampOffBlockType = BlockType.RedstoneBlock;
		World world = redstoneLampFallDamageLocation.getWorld();

		world.setBlockAt(redstoneLampFallDamageLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampDifficultyLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampPlayerCountLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampPVPPVELocation, redstoneLampOffBlockType);
		world.setRaining(false);
	}   

	private void startGame(){
		gamenostart();
		if(isFallDamageClicked && isSnowballDamageClicked && isPvpClicked && isPlayer2Clicked && isColorPicked){
			hasGameStarted = true;
			StartGameTask task = new StartGameTask(this, has2Players, TELEPORT_DELAY_IN_SECONDS);  
			Canary.getServer().addSynchronousTask(task);
		}
	}

	private void resetGame(){
		isFallDamageClicked = false;
		isSnowballDamageClicked = false;
		isPvpClicked = false;
		isPlayer2Clicked = false;
		isColorPicked = false;
		hasGameStarted = false;
		resetRedstoneBlocks();
	}

	private void displayTeamAlreadyUsedMessage(){
		Utils.BroadcastServerMessage(pluginName, "Diese Farbe ist bereits vergeben.");
	}

	private void gamenostart(){
		if(isPVPGame && !has2Players){
			Utils.BroadcastServerMessage(pluginName, "Spielmodus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN + " braucht mindestens 2 Spieler.");
			isPvpClicked = false;
			isPlayer2Clicked = false;
			isPVPGame = false;
			has2Players = false;
			Location player12LampLocation = new Location(37, 68, 261);
			Location pvpPveLampLocation = new Location(31, 68, 267);
			World world = pvpPveLampLocation.getWorld();
			BlockType redStoneLampOffBlockType = BlockType.RedstoneBlock;
			world.setBlockAt(player12LampLocation, redStoneLampOffBlockType);
			world.setBlockAt(pvpPveLampLocation, redStoneLampOffBlockType);
			return;
		}

		int boolCount = 0;
		if(isFallDamageClicked)
			boolCount++;
		if(isSnowballDamageClicked)
			boolCount++;
		if(isPvpClicked)
			boolCount++;
		if(isPlayer2Clicked)
			boolCount++;
		if(isColorPicked)
			boolCount++;

		if(boolCount == 4){

			if(!isFallDamageClicked)
				Utils.BroadcastServerMessage(pluginName, ChatFormat.GOLD + "Fallschaden " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
			if(!isSnowballDamageClicked)
				Utils.BroadcastServerMessage(pluginName, ChatFormat.GOLD + "EASY/HARD " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
			if(!isPvpClicked)
				Utils.BroadcastServerMessage(pluginName, ChatFormat.GOLD + "PVE/PVP " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
			if(!isPlayer2Clicked)
				Utils.BroadcastServerMessage(pluginName, ChatFormat.GOLD + "Spieleranzahl " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
			if(!isColorPicked)
				Utils.BroadcastServerMessage(pluginName, ChatFormat.GOLD + "Spielerfarbe " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");

			for(Player player : playerList){
				if(teamColor.get(player.getName()).equalsIgnoreCase("")){
					if(isPVPGame){
						Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Der Spieler " + player.getName() + " hat seine Farbe noch nicht gewaehlt.");
						isColorPicked=false;
						return;
					}
				if(!isPVPGame&&has2Players){
					for(Player playerf : playerList){
						String cocap = teamColor.get(playerf.getName());
						if(!cocap.equalsIgnoreCase("")) {
							teamColor.put(player.getDisplayName(), cocap);
							if(cocap.equalsIgnoreCase("green")){
								playerf.setPrefix(ChatFormat.DARK_GREEN + "");
							}
							if(cocap.equalsIgnoreCase("purple")){
								playerf.setPrefix(ChatFormat.DARK_PURPLE + "");
							}
							if(cocap.equalsIgnoreCase("red")){
								playerf.setPrefix(ChatFormat.RED + "");
							}
							if(cocap.equalsIgnoreCase("yellow")){
								playerf.setPrefix(ChatFormat.YELLOW+"");
							}
						}
					}
				}
				}
			}
		}
	}

	private String getTeamColorFromPlayer(Player player){
		String name = player.getDisplayName();
		String colorname = "";

		if(teamColor.get(name).equalsIgnoreCase("green"))
			colorname = ChatFormat.GREEN + name;
		else if(teamColor.get(name).equalsIgnoreCase("red"))
			colorname = ChatFormat.RED + name;
		if(teamColor.get(name).equalsIgnoreCase("yellow"))
			colorname = ChatFormat.YELLOW + name;
		if(teamColor.get(name).equalsIgnoreCase("purple"))
			colorname = ChatFormat.DARK_PURPLE + name;
		
		return colorname;
	}
}