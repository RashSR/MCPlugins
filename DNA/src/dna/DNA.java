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
import utils.Utils;

public class DNA extends EZPlugin implements PluginListener{

	public static int levelhoehe;
  	public static boolean harrypotterevent; // vorsicht 0 fails werden nicht getriggert falls hier false!

 	int fails = 0;
 	int i = 0;
 	int zaeler = 1;
 	int hohe;
	String msg1 = "[DNA] ";
	int gespieltespiele = 0;
 	int nullfailrunden = 0;
	int absolviertebloecke = 0;
 	int gesamtfails = 0;

 	public static BlockType zuspringenderblock = BlockType.AcaciaLeaves;
 	public static BlockType gesprungenerblock = BlockType.AcaciaLog; 
	public static BlockType zielblock = BlockType.RedstoneBlock;
	public static boolean an = false;
  	public static boolean luftblock = false;
  	public static boolean letzterblock = true;
  	public static List<Location> eachblock = new ArrayList<Location>();
  	public static boolean arrayan = false;
  	public static boolean viererb = false;
  
  	@Override 
  	public boolean enable() {
    	Canary.hooks().registerListener(this, this);
    	super.enable();
    	logger.info("Getting config data");
    	PropertiesFile config = getConfig();
    	levelhoehe = config.getInt("levelhoehe", 16);
    	harrypotterevent = config.getBoolean("harrypotter", false);
    	config.save();
    	return true;
    }

    @Command(aliases = {"dna"},
           	description = "Teleportiert den Spieler zu DNA.",
           	permissions = {""},
           	toolTip = "/dna")
	public void dna(MessageReceiver caller, String[] parameters){
    	if (caller instanceof Player){
      		Player player = (Player)caller;
      		playerteleportvorstart(player);
      		setglassanfang();
      		an = true;
      		fails = 0;
      		loadStats(player);
    	}
  	}

  	public static void setBlockType(BlockType jumpedBlock,BlockType jumpingBlock, BlockType finishBlock){
    	gesprungenerblock=jumpedBlock;
    	zuspringenderblock=jumpingBlock;
    	zielblock=finishBlock;
  	}

 	@Command(aliases = {"statsdna"},
    	     description = "Zeigt dem Spieler seine Stats",
          	 permissions = {""},
          	 toolTip = "/statsdna")
	public void statsdnaCommand(MessageReceiver caller, String[] args){
    	if(caller instanceof Player){
    		Player player = (Player)caller;
    		loadStats(player);
    		double durchschnitt = (double)gesamtfails/gespieltespiele;
    		durchschnitt = durchschnitt * 100;
    		durchschnitt = Math.round(durchschnitt);
    		durchschnitt = durchschnitt / 100;
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Das sind die Stats von " + ChatFormat.BLUE + player.getDisplayName() + ChatFormat.DARK_GREEN + ":");
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Gespielte Spiele: " + ChatFormat.GOLD + gespieltespiele);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Gesprungene Bloecke: " + ChatFormat.GOLD + absolviertebloecke);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - Fails pro Runde: " + ChatFormat.GOLD + durchschnitt);
    		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_GREEN + " - 0 Fails: " + ChatFormat.GOLD + nullfailrunden + "\n");
        }
 	}

 	@HookHandler
  	public void leavesbleiben(LeafDecayHook event){
    	Block blatt = event.getBlock();
    	Location blattloc = blatt.getLocation();
    	double x = blattloc.getX();
    	double y = blattloc.getY();
    	double z = blattloc.getZ();
    	if (x >= 267 && x <= 295 && y >= 18 && y <= 53 && z >= 199 && z <= 236){
      		event.setCanceled();
    	}
  	}

 	@HookHandler
  	public void teleportausdnaraum(TeleportHook event){
    	Player player = event.getPlayer();
    	Location ausgangloc = event.getCurrentLocation();
    	double xa = ausgangloc.getX();
    	double ya = ausgangloc.getY();
    	double za = ausgangloc.getZ();
    	World world = ausgangloc.getWorld();
    	Location zielloc = event.getDestination();
    	double xz = zielloc.getX();
    	double zz = zielloc.getZ(); 
    	if (xa >= 267 && xa <= 295 && za >= 199 && za <= 236 && an == true){
    		if(xz > 295 || xz < 267){
            	clearbasic(world);
            	loosemessage(player);
            	cleararraylist();
            	arrayan = false;
            	an = false;
            	return;
      		}
			if(zz > 236 || zz < 199){
        		clearbasic(world);
            	cleararraylist();
            	loosemessage(player);
            	arrayan = false;
            	an = false;
            	return;
      		}
   		}
  	}

	@HookHandler
	public void telemitdruckplatte(RedstoneChangeHook event){
    	Block druckplatte = event.getSourceBlock();
    	Location locdruckplatte = druckplatte.getLocation();
    	double x = locdruckplatte.getX();
    	double y = locdruckplatte.getY();
    	double z = locdruckplatte.getZ();
    	World world = locdruckplatte.getWorld();

    	int xdruckplatte = (int)x;
    	int ydruckplatte = (int)y;
    	int zdruckplatte = (int)z;
    	if(xdruckplatte == 244 && ydruckplatte == 71 && zdruckplatte == 258 && an == false){
      		an = true;
      		setglassanfang();
      		fails = 0;
      		Player player = world.getClosestPlayer(244, 71, 258, 5);
      		loadStats(player);
   		}
   		if(xdruckplatte == 267 && ydruckplatte == 18 && zdruckplatte == 199 && an == true){
    		clearbasic(world);
    		Player player = world.getClosestPlayer(267, 18, 199, 5);
    		loosemessage(player);
    		if(arrayan){
      			cleararraylist();
      			arrayan = false;
    		}
    	an = false;
   		}
  	}

 @HookHandler
  public void ichlaufe(PlayerMoveHook event) {
    if (an){
    	Player player = event.getPlayer();
      	Location loc = player.getLocation();
      	double x = loc.getX();
      	double y = loc.getY();
      	double z = loc.getZ();
      	World world = loc.getWorld();
      	Location blockuntermir = new Location(x, y-1, z);

      	int x1 = (int)x;
      	int y1 = (int)y;
      	int z1 = (int)z;

      	Block b = world.getBlockAt(x1, y1 - 1, z1);

      	double setof = Math.random() * 10;
      	int ofset = (int)setof;
    	if (b.getType() == BlockType.Glass) {
    		i = 0;
      		hohe = y1 + levelhoehe + ofset;
      		if(hohe > 51){
        		hohe = 51;
      		}
      		startmessage(player);
      		blockuntermir.getWorld().setBlockAt(blockuntermir, BlockType.WhiteGlass);
      		makerightblocks(player);
      		arrayan = true;
      		playSound(blockuntermir, SoundEffect.Type.NOTE_PLING, 2.0f, 3.0f);
    	}
    	if (b.getType() == zuspringenderblock){
    		blockuntermir.getWorld().setBlockAt(blockuntermir, gesprungenerblock);
      		absolviertebloecke = absolviertebloecke + 1;
      		savestats(player);
      		if (y < hohe){
        	double sprungauswahl = Math.random();
        		if(eachblock.size() > 1 && (eachblock.get(eachblock.size() - 1).getY()  > eachblock.get(eachblock.size() - 2).getY())){
        			levelanzeige(player);
				}
        		if(sprungauswahl < 0.95){
        			makerightblocks(player);
          		}
        		if(sprungauswahl >= 0.95){
         			vierersprung(player);
            	}
        	}
      		if (y >= hohe) {
        		makelastblock(player);
        	}
		}  
    	if (b.getType() == zielblock && b.getY() > 25){
      		gewinnmessage(player);
      		absolviertebloecke = absolviertebloecke + 1;
      		savestats(player);
      		cleararraylist();
      		blockuntermir.getWorld().setBlockAt(blockuntermir, BlockType.DiamondBlock);
      		winteleport(player);
      		clearbasic(world);
      		arrayan = false;
      		an = false;
    	}
    	while(arrayan){
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
        		fails = fails + 1;
        		playSound(vorletzterblock, SoundEffect.Type.BAT_DEATH, 1.0f, 0.75f);
        		gesamtfails = gesamtfails + 1;
        		savestats(player);
        		return;
        	}else{
       			return;
        	}
    	}
	}
}

  public void setglassanfang(){
    FileLoader fl = new FileLoader("C:/Users/R/Desktop/server/config/events.txt");
    Location nul = new Location(0, 0, 0);
    Location startglas = new Location(281, 18, 213);
    startglas.getWorld().setBlockAt(startglas, BlockType.Glass);
    eachblock.add(nul);
    eachblock.add(startglas);
  }



	public void makerightblocks(Player player){
		Location loc = player.getLocation();
		double xp = loc.getX();
		double yp = loc.getY();
		double zp = loc.getZ();
		int xplayer = (int) xp;
		int yplayer = (int) yp;
		int zplayer = (int) zp;
		World world = loc.getWorld();
		luftblock = true;

		while (luftblock){

			double x = -4.5 + Math.random() * 10;
			double y = -1.3 + Math.random();
			double z = -4.5 + Math.random() * 10;
			int xa = (int) x;
			int ya = (int) y;
			int za = (int) z;
		
			double betrag = (xa*xa) + ((ya-1)*(ya-1)) + (za*za);
			double wurzel = Math.sqrt(betrag);

			if(wurzel > 2.5 && wurzel <= 5){
				int xblock = xplayer + xa;
				int yblock = yplayer + ya;
				int zblock = zplayer + za;

				Block a = world.getBlockAt(xblock, yblock, zblock);
				Block b = world.getBlockAt(xblock, yblock + 1, zblock);
				Block c = world.getBlockAt(xblock, yblock + 2, zblock); 

				if (a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
					Location guterblock = new Location(xblock, yblock, zblock);
					eachblock.add(guterblock);

					if(eachblock.size() > 3){
						Location blockweg = eachblock.get(i);
						blockweg.getWorld().setBlockAt(blockweg, BlockType.Air);
						i = i + 1;
					}

					guterblock.getWorld().setBlockAt(guterblock, zuspringenderblock);
					playSound(guterblock, SoundEffect.Type.NOTE_HAT, 1.0f, 1.0f);
					luftblock = false;
				}
			}
		}
	}

	public void vierersprung(Player player){
		Location loc = player.getLocation();
		double xp = loc.getX();
		double yp = loc.getY();
		double zp = loc.getZ();
		int xplayer = (int) xp;
		int yplayer = (int) yp;
		int zplayer = (int) zp;
		World world = loc.getWorld();
		viererb = true;

		while(viererb){
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
			
			Location guterblock = new Location(xp + xanteil, yplayer - 1, zp + zanteil);

			Block a = world.getBlockAt((int)guterblock.getX(), (int)guterblock.getY(), (int)guterblock.getZ());
			Block b = world.getBlockAt((int)guterblock.getX(), (int)guterblock.getY() + 1, (int)guterblock.getZ());
			Block c = world.getBlockAt((int)guterblock.getX(), (int)guterblock.getY() + 2, (int)guterblock.getZ());

			if (a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
				eachblock.add(guterblock);

				if(eachblock.size() > 3){
				Location blockweg = eachblock.get(i);
				blockweg.getWorld().setBlockAt(blockweg, BlockType.Air);
				i = i + 1;
				}

				guterblock.getWorld().setBlockAt(guterblock, zuspringenderblock);
				playSound(guterblock, SoundEffect.Type.NOTE_HAT, 1.0f, 1.0f);
				viererb = false;
			}
		}
	}

    public void makelastblock(Player player) {
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
			double wurzel = Math.sqrt(betrag);

			if(wurzel > 2.5 && wurzel <= 5){
				int xblock = xplayer + xa;
				int yblock = yplayer + ya;
				int zblock = zplayer + za;

				Block a = world.getBlockAt(xblock, yblock, zblock);
				Block b = world.getBlockAt(xblock, yblock + 1, zblock);
				Block c = world.getBlockAt(xblock, yblock + 2, zblock); 

				if (a.getType() == BlockType.Air && b.getType() == BlockType.Air && c.getType() == BlockType.Air){
					Location guterblock = new Location(xblock, yblock, zblock);
					eachblock.add(guterblock);
					guterblock.getWorld().setBlockAt(guterblock, zielblock);
					playSound(guterblock, SoundEffect.Type.ORB, 3.0f, 3.0f);
					letzterblock = false;
				}
			}
		}
	}

	public void levelanzeige(Player player){
		int levelzahl = player.getLevel();
		if(levelzahl < 16)
			player.addExperience(7 + 2 * levelzahl);
		else if(levelzahl >= 16)
			player.addExperience(37 + 5 * (levelzahl - 15));
	}


	public void clearbasic(World world){
		for (Location clearblock : eachblock)
		clearblock.getWorld().setBlockAt(clearblock, BlockType.Air);
		
		for (int x = 267; x <= 295; x++) {
			for(int y = 18; y <= 53; y++) {
				for (int z = 199; z <= 227; z++) { 
					Block b = world.getBlockAt(x, y, z);
					if(b.getType() == BlockType.DiamondBlock){
						b.getLocation().getWorld().setBlockAt(b.getLocation(), BlockType.Air);
					}
				}
			}
		}    
    }

	@HookHandler
	public void ItemUseHookEvent(ItemUseHook event) {
		Player player = event.getPlayer();
		if (player.getItemHeld().getType() == ItemType.Feather && player.getItemHeld().getDisplayName().equalsIgnoreCase(ChatFormat.RED + "Hub"))
			player.teleportTo(Utils.HubLocation); 
  	}

  	public void loosemessage(Player player){
		String msg2 = player.getDisplayName();
		String msg3 = " hat ";
		String msg4 = "aufgegeben";
		player.removeExperience(player.getExperience());

		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.BLUE + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + msg4 + ChatFormat.DARK_GREEN + ".");
	}

  	public void playerteleportvorstart(Player player){
		Location where = player.getLocation();
		Location whereNow = new Location(281, 18, 235);
		player.teleportTo(new Location(player.getWorld(), 281, 18, 235, 0f, 180f));
		player.setModeId(Utils.ADVENTURE_MODE);
   	}

 	public void cleararraylist(){
    	eachblock.clear();
  	}

	public void loadStats(Player player){
		String playerName = player.getDisplayName();
		StatsDna sd = new StatsDna();
		HashMap<String, Object> search = new HashMap<String, Object>();
		search.put("player_name", playerName);

		try {
			Database.get().load(sd, search);
		} catch (DatabaseReadException e) {
			logger.info(playerName + " is not online");
		}

		gespieltespiele = sd.playedgames;
		nullfailrunden = sd.perfectwin;
		absolviertebloecke = sd.jumpedblocks;
		gesamtfails = sd.allfails;
  	}

  public void savestats(Player player){

    StatsDna sd = new StatsDna();
    sd.player_name = player.getDisplayName();
    sd.playedgames = gespieltespiele;
    sd.perfectwin = nullfailrunden;
    sd.jumpedblocks = absolviertebloecke;
    sd.allfails = gesamtfails;

    HashMap<String, Object> search = new HashMap<String, Object>();
    search.put("player_name", player.getDisplayName());

    try {
        Database.get().update(sd, search);//(2) 
      } catch (DatabaseWriteException e) {
        logger.error(e);
        logger.info("error");
      }
  }

  	public void winteleport(Player player){
		ItemFactory factory = Canary.factory().getItemFactory();
		Item backfeder = factory.newItem(ItemType.Feather);
		backfeder.setDisplayName(ChatFormat.RED + "Hub");

		Location siegerpodest = new Location(277, 72, 214);
		player.teleportTo(siegerpodest);
		playSound(siegerpodest, SoundEffect.Type.LEVEL_UP, 2.0f, 2.0f);
		player.getInventory().setSlot(8, backfeder);
		player.removeExperience(player.getExperience());
	}                            

  	public void startmessage(Player player) {
		String msg2 = "Das Spiel beginnt!";
		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg2);
		gespieltespiele = gespieltespiele + 1;
		savestats(player);
    }

	public void gewinnmessage(Player player) {
    	clearbasic(player.getWorld());
    	cleararraylist();
    	String msg2 = player.getDisplayName();
    	String msg3 = " hat DNA mit ";
    	String msg4 = " Fehlern ";
    	String msg5 = "gewonnen ";
    	if(fails == 1)
      		msg4 = " Fehler ";
    	if(gespieltespiele == 20)
       		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + "Du hast 20 Spiele absolviert. Herzlichen Glueckwunsch!");

    	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.BLUE + msg2 + ChatFormat.DARK_GREEN + msg3 + ChatFormat.GOLD + fails + ChatFormat.DARK_GREEN + msg4 + ChatFormat.GOLD + msg5 + ChatFormat.DARK_GREEN + "!");
    	if(fails == 0){
      		if(harrypotterevent){
      			ItemFactory factory = Canary.factory().getItemFactory();
      			Item hinweisschaufel = factory.newItem(ItemType.GoldSpade);
      			hinweisschaufel.setDisplayName(ChatFormat.YELLOW + "Hinweis..");
      			hinweisschaufel.setDamage(32);
      			player.getInventory().setSlot(7, hinweisschaufel);
      			nullfailrunden = nullfailrunden + 1;
      			savestats(player);
      			loadStats(player);
      			String msg6 = "Fuer diese gute Leistung bekommst du einen ";
      			String msg7 = "Hinweis";
      			String msg8 = " fuer ";
      			String msg9 = "[HP-PS1]";
      			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + msg1 + ChatFormat.DARK_GREEN + msg6 + ChatFormat.GOLD + msg7 + ChatFormat.DARK_GREEN + msg8 + ChatFormat.DARK_AQUA + msg9 + ChatFormat.DARK_GREEN + ".");
    		}
  		}
    }
}