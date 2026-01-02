package dna;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.PlayerMoveHook;
import net.canarymod.api.world.World;
import net.canarymod.api.inventory.Item;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.hook.world.RedstoneChangeHook;
import net.canarymod.hook.player.TeleportHook;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import net.canarymod.hook.world.LeafDecayHook;
import net.visualillusionsent.utils.PropertiesFile;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.database.Database;
import net.canarymod.database.DataAccess;
import net.canarymod.database.exceptions.*;
import utils.ScoreboardTimerTask;
import utils.Utils;
import net.canarymod.api.scoreboard.*;

public class DNA extends EZPlugin implements PluginListener{

	public int LEVEL_HEIGHT;
  	public boolean harrypotterevent; // vorsicht 0 fails werden nicht getriggert falls hier false!

 	private int jumpFails = 0;
 	private int height;
	public static final String pluginName = "[DNA] ";
	private int totalGamesPlayed = 0;
 	private int zeroFailRound = 0;
	private int totalJumpedBlocks = 0;
 	private int totalFails = 0;

 	public static BlockType BlockToJumpType = BlockType.AcaciaLeaves;
 	public static BlockType JumpedBlock = BlockType.AcaciaLog; 
	public static BlockType DestinationBlock = BlockType.RedstoneBlock;
	public boolean isEnabled = false;
  	public boolean letzterblock = true;
  	public List<Location> eachblock = new ArrayList<Location>();
  	public boolean isArrayEnabled = false;
  	public boolean isFourJumpBlock = false;
  
  	@Override 
  	public boolean enable() {
    	Canary.hooks().registerListener(this, this);
    	super.enable();
    	logger.info(pluginName + "Getting config data.");
    	PropertiesFile config = getConfig();
    	LEVEL_HEIGHT = config.getInt("levelhoehe", 16);
    	harrypotterevent = config.getBoolean("harrypotter", false);
    	config.save();
    	return true;
    }

    @Command(aliases = {"dna"},
           	description = "Teleportiert den Spieler zu DNA.",
           	permissions = {""},
           	toolTip = "/dna")
	public void dna(MessageReceiver caller, String[] parameters){
    	if (caller instanceof Player player)
      		enableGame(player);
  	}

	private void enableGame(Player player){
		teleportPlayerBeforeStart(player);
		placeStartBlock();
		isEnabled = true;
		jumpFails = 0;
		loadStats(player);
	}

  	public static void setBlockType(BlockType jumpedBlock,BlockType jumpingBlock, BlockType finishBlock){
    	JumpedBlock = jumpedBlock;
    	BlockToJumpType = jumpingBlock;
    	DestinationBlock = finishBlock;
  	}

 	@Command(aliases = {"statsdna"},
    	     description = "Zeigt dem Spieler seine Stats",
          	 permissions = {""},
          	 toolTip = "/statsdna")
	public void statsdnaCommand(MessageReceiver caller, String[] args){
    	if(caller instanceof Player player){
    		loadStats(player);
    		double average = (double)totalFails/totalGamesPlayed;
    		average = average * 100;
    		average = Math.round(average);
    		average = average / 100;
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + pluginName + ChatFormat.DARK_GREEN + "Das sind die Stats von " + ChatFormat.BLUE + player.getDisplayName() + ChatFormat.DARK_GREEN + ":");
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Gespielte Spiele: " + ChatFormat.GOLD + totalGamesPlayed);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Gesprungene Bloecke: " + ChatFormat.GOLD + totalJumpedBlocks);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Fails pro Runde: " + ChatFormat.GOLD + average);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - 0 Fails: " + ChatFormat.GOLD + zeroFailRound + "\n");
        }
 	}

 	@HookHandler
  	public void LeafDecayHookEvent(LeafDecayHook event){
    	Block blatt = event.getBlock();
    	Location blattloc = blatt.getLocation();
    	double x = blattloc.getX();
    	double y = blattloc.getY();
    	double z = blattloc.getZ();
    	if (x >= 267 && x <= 295 && y >= 18 && y <= 53 && z >= 199 && z <= 236)
      		event.setCanceled();
  	}

 	@HookHandler
  	public void TeleportHookEvent(TeleportHook event){
    	Player player = event.getPlayer();
    	Location startingLocation = event.getCurrentLocation();
    	double xa = startingLocation.getX();
    	double ya = startingLocation.getY();
    	double za = startingLocation.getZ();
    	World world = startingLocation.getWorld();
    	Location destinationLocation = event.getDestination();
    	double xz = destinationLocation.getX();
    	double zz = destinationLocation.getZ(); 
    	if (xa >= 267 && xa <= 295 && za >= 199 && za <= 236 && isEnabled == true){
    		if(xz > 295 || xz < 267){
            	clearAllPlacedBlocks(world);
            	displayLoseMessage(player);
            	eachblock.clear();
            	isArrayEnabled = false;
            	isEnabled = false;
            	return;
      		}
			if(zz > 236 || zz < 199){
        		clearAllPlacedBlocks(world);
            	eachblock.clear();
            	displayLoseMessage(player);
            	isArrayEnabled = false;
            	isEnabled = false;
            	return;
      		}
   		}
  	}

	@HookHandler
	public void RedstoneChangeHookEvent(RedstoneChangeHook event){
    	Block pressurePlate = event.getSourceBlock();
    	Location pressurePlateLocation = pressurePlate.getLocation();
    	World world = pressurePlateLocation.getWorld();

    	int pressurePlateX = (int)pressurePlateLocation.getX();
    	int pressurePlateY = (int)pressurePlateLocation.getY();
    	int pressurePlateZ = (int)pressurePlateLocation.getZ();

    	if(!isEnabled && pressurePlateX == 244 && pressurePlateY == 71 && pressurePlateZ == 258){
      		isEnabled = true;
      		placeStartBlock();
      		jumpFails = 0;
      		Player player = world.getClosestPlayer(244, 71, 258, 5);
      		loadStats(player);
   		}
   		if(isEnabled && pressurePlateX == 267 && pressurePlateY == 18 && pressurePlateZ == 199){
    		clearAllPlacedBlocks(world);
    		Player player = world.getClosestPlayer(267, 18, 199, 5);
    		displayLoseMessage(player);
    		if(isArrayEnabled){
      			eachblock.clear();
      			isArrayEnabled = false;
    		}
    	isEnabled = false;
   		}
  	}

 	@HookHandler
  	public void PlayerMoveHookEvent(PlayerMoveHook event){
		if(isEnabled){
			Player player = event.getPlayer();
			Location loc = player.getLocation();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();
			World world = loc.getWorld();
			Location locationBelowPlayer = new Location(x, y - 1, z);

			int x1 = (int)x;
			int y1 = (int)y;
			int z1 = (int)z;

			Block blockBelowPlayer = world.getBlockAt(x1, y1 - 1, z1);

			if(blockBelowPlayer.getType() == BlockType.Glass)
				startGame(player, y1, locationBelowPlayer); //TODO: add a function to Utils that calculates the location below a player

			if(blockBelowPlayer.getType() == BlockToJumpType){
				jumpedBlocksInActiveGame++;
				updateScoreboard();
				
				locationBelowPlayer.getWorld().setBlockAt(locationBelowPlayer, JumpedBlock);
				totalJumpedBlocks = totalJumpedBlocks + 1;
				saveStats(player);
				if (y < height){
					double randomJumpSelection = Math.random();
					if(eachblock.size() > 1 && (eachblock.get(eachblock.size() - 1).getY()  > eachblock.get(eachblock.size() - 2).getY()))
						displayCorrectLevel(player);
					if(randomJumpSelection < 0.95)
						spawnJumpBlock(player);
					if(randomJumpSelection >= 0.95)
						spwanFourBlockJumpBlock(player);
				}
				if (y >= height)
					makeLastBlock(player);
			}  

			if (blockBelowPlayer.getType() == DestinationBlock && blockBelowPlayer.getY() > 25){
				displayWinMessage(player);
				totalJumpedBlocks = totalJumpedBlocks + 1;
				saveStats(player);
				eachblock.clear();
				locationBelowPlayer.getWorld().setBlockAt(locationBelowPlayer, BlockType.DiamondBlock);
				teleportAfterWin(player);
				clearAllPlacedBlocks(world);
				isArrayEnabled = false;
				isEnabled = false;
			}

			while(isArrayEnabled){
				int arraylaenge = eachblock.size();
				int letzterarraywert = arraylaenge - 1; 
				int vorletzterarraywert = arraylaenge - 2;
				double ydarfnichtunter = eachblock.get(letzterarraywert).getY() - 2;
				if(y1 < ydarfnichtunter){
					double xb = eachblock.get(vorletzterarraywert).getX();
					double yb = eachblock.get(vorletzterarraywert).getY() + 1;
					double zb = eachblock.get(vorletzterarraywert).getZ();
					int xlb = (int)xb;
					int ylb = (int)yb;
					int zlb = (int)zb;
					float richtung = player.getLocation().getRotation();
					Location vorletzterblock = new Location(world, xlb, ylb, zlb, 0f, richtung);
					player.teleportTo(vorletzterblock);
					jumpFails = jumpFails + 1;
					updateScoreboard();
					playSound(vorletzterblock, SoundEffect.Type.BAT_DEATH, 1.0f, 0.75f);
					totalFails = totalFails + 1;
					saveStats(player);
					return;
				}
				else
					return;
			}
		}
	}

	private int jumpedBlocksInActiveGame;

	private void startGame(Player player, int y1, Location locationBelowPlayer){
		jumpedBlocksInActiveGame = 0;
		int offset = (int)Math.random() * 10;
		height = y1 + LEVEL_HEIGHT + offset;
		if(height > 51)
			height = 51;

		displayStartMessage(player);
		locationBelowPlayer.getWorld().setBlockAt(locationBelowPlayer, BlockType.WhiteGlass);
		spawnJumpBlock(player);
		isArrayEnabled = true;
		playSound(locationBelowPlayer, SoundEffect.Type.NOTE_PLING, 2.0f, 3.0f);
		createScoreboard(player);
	}

	private Scoreboard scoreboard;
	private ScoreObjective objective;
	private Score jumpedBlocksScore;
	private Score jumpFailsScore;
	private Score timeScore;
	private ScoreboardTimerTask timerTask;

	private void createScoreboard(Player player){
		ScoreboardManager manager = Canary.scoreboards();

		this.scoreboard = manager.getScoreboard("gameboard");
		this.objective = scoreboard.addScoreObjective("dnaJump");
		this.objective.setDisplayName("§6§lGame Info");
		this.scoreboard.setScoreboardPosition(ScorePosition.SIDEBAR, this.objective, player);

		// Initialize score entries
		this.timerTask = new ScoreboardTimerTask(this.scoreboard, this.objective, this.timeScore, 3);
		Canary.getServer().addSynchronousTask(timerTask);

		this.jumpFailsScore = scoreboard.getScore("§cFails: §f" + jumpFails, this.objective);
		this.jumpFailsScore.setScore(2);
		this.jumpFailsScore.update();

		this.jumpedBlocksScore = scoreboard.getScore("§aJumpedBlocks: §f" + jumpedBlocksInActiveGame, this.objective);
		this.jumpedBlocksScore.setScore(1);
		this.jumpedBlocksScore.update();
  	}

	private void updateScoreboard(){
		scoreboard.removeScore(this.jumpFailsScore.getName(), this.objective);
		this.jumpFailsScore = scoreboard.getScore("§cFails: §f" + jumpFails, this.objective);
		this.jumpFailsScore.setScore(2);
		this.jumpFailsScore.update();

		scoreboard.removeScore(this.jumpedBlocksScore.getName(), this.objective);
		this.jumpedBlocksScore = scoreboard.getScore("§aJumpedBlocks: §f" + jumpedBlocksInActiveGame, this.objective);
		this.jumpedBlocksScore.setScore(1);
		this.jumpedBlocksScore.update();
	}

	private void placeStartBlock(){
		FileLoader fl = new FileLoader("C:/Users/R/Desktop/server/config/events.txt");
		Location zeroLocation = new Location(0, 0, 0);
		Location startglas = new Location(281, 18, 213);
		startglas.getWorld().setBlockAt(startglas, BlockType.Glass);
		eachblock.add(zeroLocation);
		eachblock.add(startglas);
	}

	public void spawnJumpBlock(Player player){
		Location loc = player.getLocation();
		double xp = loc.getX();
		double yp = loc.getY();
		double zp = loc.getZ();
		int xplayer = (int) xp;
		int yplayer = (int) yp;
		int zplayer = (int) zp;
		World world = loc.getWorld();
		boolean isValidBlock = false;

		while(!isValidBlock){
			//These coordinates are relative to the player's current position
			double x = -4.5 + Math.random() * 10;
			double y = -1.3 + Math.random();
			double z = -4.5 + Math.random() * 10;
			int xa = (int) x;
			int ya = (int) y;
			int za = (int) z;
		
			double magnitude = (xa*xa) + ((ya-1)*(ya-1)) + (za*za);
			double distance = Math.sqrt(magnitude);

			if(distance > 2.5 && distance <= 5){
				int xblock = xplayer + xa;
				int yblock = yplayer + ya;
				int zblock = zplayer + za;

				Block a = world.getBlockAt(xblock, yblock, zblock);
				Block b = world.getBlockAt(xblock, yblock + 1, zblock);
				Block c = world.getBlockAt(xblock, yblock + 2, zblock); 

				if(a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
					Location validBlockLocation = new Location(xblock, yblock, zblock);
					eachblock.add(validBlockLocation);

					trimJumpedBlocks();
					validBlockLocation.getWorld().setBlockAt(validBlockLocation, BlockToJumpType);
					playSound(validBlockLocation, SoundEffect.Type.NOTE_HAT, 1.0f, 1.0f);
					isValidBlock = true;
				}
			}
		}
	}

	public void spwanFourBlockJumpBlock(Player player){
		Location loc = player.getLocation();
		double xp = loc.getX();
		double yp = loc.getY();
		double zp = loc.getZ();
		int xplayer = (int) xp;
		int yplayer = (int) yp;
		int zplayer = (int) zp;
		World world = loc.getWorld();
		isFourJumpBlock = true;

		while(isFourJumpBlock){
			double richtungvierersprung = Math.random();
			int xanteil = 0;
			int zanteil = 0;

			if(richtungvierersprung <= 0.25)
				xanteil = 5;

			if(richtungvierersprung > 0.25 && richtungvierersprung <= 0.5)
				xanteil = -5;

			if(richtungvierersprung > 0.5 && richtungvierersprung <= 0.75)
			zanteil = 5;

			if(richtungvierersprung > 0.75 && richtungvierersprung <= 1)
			zanteil = - 5;
			
			Location validBlockLocation = new Location(xp + xanteil, yplayer - 1, zp + zanteil);

			Block a = world.getBlockAt((int)validBlockLocation.getX(), (int)validBlockLocation.getY(), (int)validBlockLocation.getZ());
			Block b = world.getBlockAt((int)validBlockLocation.getX(), (int)validBlockLocation.getY() + 1, (int)validBlockLocation.getZ());
			Block c = world.getBlockAt((int)validBlockLocation.getX(), (int)validBlockLocation.getY() + 2, (int)validBlockLocation.getZ());

			if (a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
				eachblock.add(validBlockLocation);

				trimJumpedBlocks();
				validBlockLocation.getWorld().setBlockAt(validBlockLocation, BlockToJumpType);
				playSound(validBlockLocation, SoundEffect.Type.NOTE_HAT, 1.0f, 1.0f);
				isFourJumpBlock = false;
			}
		}
	}

	private void trimJumpedBlocks(){
		if(eachblock.size() > 3){
			Location blockToRemove = eachblock.get(eachblock.size() - 4);
			blockToRemove.getWorld().setBlockAt(blockToRemove, BlockType.Air);
		}
	}

    private void makeLastBlock(Player player) {
     	Location loc = player.getLocation();
		double xp = loc.getX();
		double yp = loc.getY();
		double zp = loc.getZ();
		int xplayer = (int) xp;
		int yplayer = (int) yp;
		int zplayer = (int) zp;
		World world = loc.getWorld();
		letzterblock = true;

		while (letzterblock){ 
			double x = -4.5 + Math.random() * 10;
			double y = -1.3 + Math.random();
			double z = -4.5 + Math.random() * 10;
			int xa = (int) x;
			int ya = (int) y;
			int za = (int) z;
		
			double betrag = (xa*xa) + ((ya-1)*(ya-1)) + (za*za);
			double sqrt = Math.sqrt(betrag);

			if(sqrt > 2.5 && sqrt <= 5){
				int xblock = xplayer + xa;
				int yblock = yplayer + ya;
				int zblock = zplayer + za;

				Block a = world.getBlockAt(xblock, yblock, zblock);
				Block b = world.getBlockAt(xblock, yblock + 1, zblock);
				Block c = world.getBlockAt(xblock, yblock + 2, zblock); 

				if (a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
					Location guterblock = new Location(xblock, yblock, zblock);
					eachblock.add(guterblock);
					guterblock.getWorld().setBlockAt(guterblock, DestinationBlock);
					playSound(guterblock, SoundEffect.Type.ORB, 3.0f, 3.0f);
					letzterblock = false;
				}
			}
		}
	}

	private void displayCorrectLevel(Player player){
		int playerLevel = player.getLevel();
		if(playerLevel < 16)
			player.addExperience(7 + 2 * playerLevel);
		else if(playerLevel >= 16)
			player.addExperience(37 + 5 * (playerLevel - 15));
	}

	private void clearAllPlacedBlocks(World world){
		for (Location clearblock : eachblock)
			clearblock.getWorld().setBlockAt(clearblock, BlockType.Air);
		
		for (int x = 267; x <= 295; x++){
			for(int y = 18; y <= 53; y++){
				for (int z = 199; z <= 227; z++){ 
					Block b = world.getBlockAt(x, y, z);
					if(b.getType() == BlockType.DiamondBlock)
						b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);
				}
			}
		}    
    }

	@HookHandler
	public void ItemUseHookEvent(ItemUseHook event) {
		Player player = event.getPlayer();
		Item heldItem = player.getItemHeld();
		if (heldItem.getType() == ItemType.Feather && heldItem.getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Hub"))
			player.teleportTo(Utils.HubLocation); 
  	}

  	private void displayLoseMessage(Player player){
		String msg2 = player.getDisplayName();
		String msg3 = " hat ";
		String msg4 = "aufgegeben";
		player.removeExperience(player.getExperience());

		String serverMessage = ChatFormat.BLUE + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".";
		Utils.BroadcastServerMessage(pluginName, serverMessage);
		Utils.clearScoreboard(scoreboard, timerTask, objective); //create like in quidditch a cleanUp()-Method
	}

  	private void teleportPlayerBeforeStart(Player player){
		Location whereNow = new Location(281, 18, 235);
		player.teleportTo(new Location(player.getWorld(), 281, 18, 235, 0f, 180f));
		player.setModeId(Utils.ADVENTURE_MODE);
   	}

	private void loadStats(Player player){
		String playerName = player.getDisplayName();
		StatsDna sd = new StatsDna();
		HashMap<String, Object> search = new HashMap<String, Object>();
		search.put("player_name", playerName);

		try {
			Database.get().load(sd, search);
		} catch (DatabaseReadException e) {
			logger.info(playerName + " is not online");
		}

		totalGamesPlayed = sd.playedgames;
		zeroFailRound = sd.perfectwin;
		totalJumpedBlocks = sd.jumpedblocks;
		totalFails = sd.allfails;
  	}

  	private void saveStats(Player player){
		StatsDna sd = new StatsDna();
		sd.player_name = player.getDisplayName();
		sd.playedgames = totalGamesPlayed;
		sd.perfectwin = zeroFailRound;
		sd.jumpedblocks = totalJumpedBlocks;
		sd.allfails = totalFails;

		HashMap<String, Object> search = new HashMap<String, Object>();
		search.put("player_name", player.getDisplayName());

		try {
			Database.get().update(sd, search);//(2) 
		} catch (DatabaseWriteException e) {
			logger.error(e);
			logger.info("error");
		}
  	}

  	private void teleportAfterWin(Player player){
		ItemFactory factory = Canary.factory().getItemFactory();
		Item backFeather = factory.newItem(ItemType.Feather);
		backFeather.setDisplayName(ChatFormat.RED + "Hub");

		Location winPedastalLocation = new Location(277, 72, 214);
		player.teleportTo(winPedastalLocation);
		playSound(winPedastalLocation, SoundEffect.Type.LEVEL_UP, 2.0f, 2.0f);
		player.getInventory().setSlot(8, backFeather);
		player.removeExperience(player.getExperience());
		Utils.clearScoreboard(scoreboard, timerTask, objective);
	}                            

  	private void displayStartMessage(Player player) {
		String msg2 = "Das Spiel beginnt!";
		Utils.BroadcastServerMessage(pluginName, ChatFormat.DARK_GREEN + msg2);
		totalGamesPlayed = totalGamesPlayed + 1;
		saveStats(player);
    }

	private void displayWinMessage(Player player) {
    	clearAllPlacedBlocks(player.getWorld());
    	eachblock.clear();
    	String msg2 = player.getDisplayName();
    	String msg3 = " hat DNA mit ";
    	String msg4 = " Fehlern ";
    	String msg5 = "gewonnen ";
    	if(jumpFails == 1)
      		msg4 = " Fehler ";
    	if(totalGamesPlayed == 20){
			String serverMessage = ChatFormat.DARK_GREEN + "Du hast 20 Spiele absolviert. Herzlichen Glueckwunsch!";
			Utils.BroadcastServerMessage(pluginName, serverMessage);
		}

		String serverMessage = ChatFormat.BLUE + msg2 + ChatFormat.DARK_GREEN + 
			msg3 + ChatFormat.GOLD + jumpFails + ChatFormat.DARK_GREEN + msg4 + 
			ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + "!";
		Utils.BroadcastServerMessage(pluginName, serverMessage);

    	if(jumpFails == 0){
      		if(harrypotterevent){
      			ItemFactory factory = Canary.factory().getItemFactory();
      			Item clueShovel = factory.newItem(ItemType.GoldSpade);
      			clueShovel.setDisplayName(ChatFormat.YELLOW + "Hinweis..");
      			clueShovel.setDamage(32);
      			player.getInventory().setSlot(7, clueShovel);
      			zeroFailRound = zeroFailRound + 1;
      			saveStats(player);
      			loadStats(player);
      			String msg6 = "Fuer diese gute Leistung bekommst du einen ";
      			String msg7 = "Hinweis";
      			String msg8 = " fuer ";
      			String msg9 = "[HP-PS1]";

				serverMessage = ChatFormat.DARK_GREEN + msg6 + ChatFormat.GOLD + msg7 + 
					ChatFormat.DARK_GREEN + msg8 + ChatFormat.DARK_AQUA + msg9 + ChatFormat.DARK_GREEN + ".";
      			Utils.BroadcastServerMessage(pluginName, serverMessage);
    		}
  		}
    }
}