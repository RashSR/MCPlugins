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
	private boolean isFallDamageDeactivated;
	private boolean isFallDamageClicked = false; //ob einstellung schon ausgewählt wurde
	private boolean isSnowballDamageClicked =false;
	private boolean isPVPGame;
	private boolean isPvpClicked = false;
	private boolean has2Players; //true = 2 Spieler
	private boolean isPlayer2Clicked = false;
	private boolean isColorPicked = false;
	private int SNOWBALL_DAMAGE = 2;

	protected static final String pluginName = "[Schneeballschlacht]";
	private final Location SnowballHubLocation = new Location(35, 67, 259);
	private final Location redstoneLampFallDamageLocation = new Location(25, 68, 261);
	private final Location redstoneLampDifficultyLocation = new Location(31, 68, 255);
	private final Location redstoneLampPlayerCountLocation = new Location(37, 68, 261);
	private final Location redstoneLampPVPPVELocation = new Location(31, 68, 267);
	private final int TELEPORT_DELAY_IN_SECONDS = 10;
	private final BlockType redstoneLampOffBlockType = BlockType.RedstoneBlock;
	private final BlockType redstoneLampOnBlockType = BlockType.EmeraldBlock;

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
		Location startingLocation = event.getCurrentLocation();
		double xa = startingLocation.getX();
		double za = startingLocation.getZ();
		World world = startingLocation.getWorld();
		Location destinationLocation = event.getDestination();
		double destinationX = destinationLocation.getX();
		double destinationZ = destinationLocation.getZ();

		if (xa >= 26 && xa <= 36 && za >= 256 && za <= 266){
			if(destinationX > 12 && destinationX < 45 && destinationZ > 221 && destinationZ < 285)
				return;
			else{
				Player player = event.getPlayer();
				teamColor.remove(player.getDisplayName());
				player.setPrefix(ChatFormat.WHITE + "");
			}
		}
	}
 

	@HookHandler
	public void ProjectileHitHookEvent(ProjectileHitHook event){
		if(hasGameStarted){
			Entity snowball = event.getProjectile();
			Entity entity = event.getEntityHit();

			if (snowball.getEntityType() == EntityType.SNOWBALL && entity instanceof Player player) {
				if(player.getHealth() > SNOWBALL_DAMAGE){
					player.setHealth(player.getHealth() - SNOWBALL_DAMAGE);
					return;
				}
				if(player.getHealth() <= SNOWBALL_DAMAGE){
					Utils.BroadcastServerMessage(pluginName, getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN + " ist gestorben.");
					player.teleportTo(SnowballHubLocation);
					resetGame();
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
				if(event.getDamageDealt() >= player.getHealth()){
					event.setDamageDealt(0f);
					Utils.BroadcastServerMessage(pluginName, getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN +" ist gestorben.");
					player.teleportTo(SnowballHubLocation);
					resetGame();
				}
				if(attacker instanceof Player || (event.getDamageSource().getDamagetype() == DamageType.FALL && isFallDamageDeactivated))
					event.setCanceled();
			}
		}
	}

    @HookHandler
  	public void BlockRightClickHookEvent(BlockRightClickHook event){
		Block clickedBlock = event.getBlockClicked();
		World world = clickedBlock.getWorld();
		int clickedX = clickedBlock.getX();
		int clickedY = clickedBlock.getY();
		int clickedZ = clickedBlock.getZ();
		Player player = event.getPlayer();

		if(teamColor.size() > 1)
			playerList = Canary.getServer().getPlayerList();

		if(clickedY == 67){// alle bei x = 37
			if(clickedZ == 260){
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
					Utils.BroadcastServerMessage(pluginName,  getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.DARK_PURPLE + "lila" + ChatFormat.DARK_GREEN + ".");
					startGame();
				}
			}

			if(clickedZ == 262){
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
					Utils.BroadcastServerMessage(pluginName,  getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.YELLOW + "gelb" + ChatFormat.DARK_GREEN + ".");
					startGame();
				}
			}         
		}

		if(clickedY == 69){
			if(clickedZ == 260){
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
					Utils.BroadcastServerMessage(pluginName,  getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.RED + "rot" + ChatFormat.DARK_GREEN + ".");
					startGame();
				}
			}

			if(clickedZ==262){
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
					Utils.BroadcastServerMessage(pluginName,  getPlayerInTeamColor(player) + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.GREEN + "gruen" + ChatFormat.DARK_GREEN + ".");
					startGame();
				}		
			}
		} 

    	if(clickedBlock.getType() == BlockType.WallSign){
			if(clickedX == 26 && clickedZ == 262){
				isFallDamageDeactivated = false;
				isFallDamageClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Fallschaden ist " + ChatFormat.GOLD + "an" + ChatFormat.DARK_GREEN +".");
				world.setBlockAt(redstoneLampFallDamageLocation , redstoneLampOnBlockType);
			}
			if(clickedX == 26 && clickedZ == 260){     
				isFallDamageDeactivated = true;
				isFallDamageClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Fallschaden ist " +ChatFormat.GOLD + "aus" + ChatFormat.DARK_GREEN + ".");
				world.setBlockAt(redstoneLampFallDamageLocation , redstoneLampOnBlockType);
			}

			if(clickedX == 30 && clickedZ == 256){
				SNOWBALL_DAMAGE = 2;
				isSnowballDamageClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Schneeball macht " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Herz Schaden.");
				world.setBlockAt(redstoneLampDifficultyLocation, redstoneLampOnBlockType);
			}
			if(clickedX == 32 && clickedZ == 256){
				SNOWBALL_DAMAGE = 4;
				isSnowballDamageClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Schneeball macht " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Herzen Schaden.");
				world.setBlockAt(redstoneLampDifficultyLocation, redstoneLampOnBlockType);
			}

			if(clickedX == 36 && clickedZ == 262){
				has2Players=true;
				isPlayer2Clicked = true;
				Utils.BroadcastServerMessage(pluginName, "Es spielen " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Spieler.");
				world.setBlockAt(redstoneLampPlayerCountLocation, redstoneLampOnBlockType);
			}
			if(clickedX == 36 && clickedZ == 260){
				has2Players=false;
				isPlayer2Clicked = true;
				Utils.BroadcastServerMessage(pluginName, "Es spielt " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Spieler.");
				world.setBlockAt(redstoneLampPlayerCountLocation, redstoneLampOnBlockType);
			} 	

			if(clickedX == 32 && clickedZ == 266){
				isPVPGame = true;
				isPvpClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Der Modus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN+" wird gespielt.");
				world.setBlockAt(redstoneLampPVPPVELocation , redstoneLampOnBlockType);
			}
			if(clickedX == 30 && clickedZ == 266){
				isPVPGame = false;
				isPvpClicked = true;
				Utils.BroadcastServerMessage(pluginName, "Der Modus " + ChatFormat.GOLD + "PVE" + ChatFormat.DARK_GREEN+" wird gespielt.");
				world.setBlockAt(redstoneLampPVPPVELocation , redstoneLampOnBlockType);
			}

			startGame();
		}
	}

	private void resetRedstoneBlocks(){
		World world = redstoneLampFallDamageLocation.getWorld();
		world.setBlockAt(redstoneLampFallDamageLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampDifficultyLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampPlayerCountLocation, redstoneLampOffBlockType);
		world.setBlockAt(redstoneLampPVPPVELocation, redstoneLampOffBlockType);
		world.setRaining(false);
	}   

	private void startGame(){
		if(canGameStart() && isFallDamageClicked && isSnowballDamageClicked && isPvpClicked && isPlayer2Clicked && isColorPicked){
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

	private boolean canGameStart(){
		if(isPVPGame && !has2Players){
			Utils.BroadcastServerMessage(pluginName, "Spielmodus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN + " braucht mindestens 2 Spieler.");
			isPvpClicked = false;
			isPlayer2Clicked = false;
			isPVPGame = false;
			has2Players = false;
			World world = redstoneLampPVPPVELocation .getWorld();
			world.setBlockAt(redstoneLampPlayerCountLocation, redstoneLampOffBlockType);
			world.setBlockAt(redstoneLampPVPPVELocation, redstoneLampOffBlockType);
			return false;
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
						Utils.BroadcastServerMessage(pluginName, "Der Spieler " + player.getName() + " hat seine Farbe noch nicht gewaehlt.");
						isColorPicked = false;
						return false;
					}
					if(!isPVPGame && has2Players){
						for(Player playerf : playerList){
							String teamColorCode = teamColor.get(playerf.getName());

							if(!teamColorCode.equalsIgnoreCase("")) {
								teamColor.put(player.getDisplayName(), teamColorCode);

								if(teamColorCode.equalsIgnoreCase("green"))
									playerf.setPrefix(ChatFormat.DARK_GREEN + "");
								if(teamColorCode.equalsIgnoreCase("purple"))
									playerf.setPrefix(ChatFormat.DARK_PURPLE + "");
								if(teamColorCode.equalsIgnoreCase("red"))
									playerf.setPrefix(ChatFormat.RED + "");
								if(teamColorCode.equalsIgnoreCase("yellow"))
									playerf.setPrefix(ChatFormat.YELLOW + "");
							}
						}
					}
				}
			}
		}

		return true;
	}

	private String getPlayerInTeamColor(Player player){
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