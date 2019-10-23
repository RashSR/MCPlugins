package schneeballschlacht;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.entity.living.LivingBase;
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

public class Schneeballschlacht extends EZPlugin implements PluginListener {

	public static List<Player> spielerliste = new ArrayList<Player>();
	private static Map<String,String> teamfarbe = new HashMap<String,String>();
	public HashMap<Integer, StartGame1> alltasks = new HashMap<Integer, StartGame1>();
	boolean gameon = false;
	boolean nofalldmg;
	boolean fallgeklickt = false; //ob einstellung schon ausgewählt wurde
	int schneeballschaden = 2;
	boolean schneeballschadengeklickt =false;
	boolean pvp;
	boolean pvpgeklickt = false;
	boolean player2; //true = 2 Spieler
	boolean player2geklickt = false;
	boolean farbwahlgetroffen = false;

  @Override
  public boolean enable() {  
    Canary.hooks().registerListener(this, this);
    return super.enable(); // Call parent class's version too.
  }  

  	    @Command(aliases = { "schneeballarena"},
           description = "Teleportiert den Spieler zur Schneeballarena_hub.",
           permissions = {""},
           toolTip = "/schneeballarena, or /schneeballarena map <mapname>, or /schneeballarena maps")

  public void teleporttosnowballmap(MessageReceiver caller, String[] args) {
    
    if (caller instanceof Player) {
      Player player = (Player)caller;

      if(args.length == 1){

        Location snowballhub = new Location(35, 67, 259);
        player.teleportTo(snowballhub);
        teamfarbe.put(player.getDisplayName(), "");
        return;

                          }

      if(args.length == 2 && args[1].equalsIgnoreCase("maps")){

      String msg2 = "Zurzeit sind folgende Maps verfuegbar: ";
      String msg3 = "Arena";
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + msg2 + ChatFormat.GOLD + msg3 + ChatFormat.DARK_GREEN + ".");

                          }
      if(args[1].equalsIgnoreCase("map") && args[2].equalsIgnoreCase("arena")){

       Location arenamap = new Location(28, 110, 254);
       player.teleportTo(arenamap);

       }

                                  }
                                                                            }
 @HookHandler
 public void connectedinsnowhub(ConnectionHook event){
 	Player player = event.getPlayer();
 	double x = player.getX();
 	double z = player.getZ();
 	if(x>26 && x<36 && z>256 && z<266){
	 	teamfarbe.put(player.getDisplayName(), "");
 	}
 }

 @HookHandler
  public void disconnect(DisconnectionHook event){
  	Player player = event.getPlayer();
  	double x = player.getX();
  	double z = player.getZ();
  	 	if(x>26 && x<36 && z>256 && z<266){
    teamfarbe.remove(player.getDisplayName());
    player.setPrefix(ChatFormat.WHITE + "");
 	}

  }

 @HookHandler
 public void pressurefromandbackhub(RedstoneChangeHook event){

  Block druckplatte = event.getSourceBlock();
  int dx = druckplatte.getX();
  int dy = druckplatte.getY();
  int dz = druckplatte.getZ();
  World world = druckplatte.getWorld();

  if(dx == 245 && dy == 71 && dz == 259){

    Player player = world.getClosestPlayer(245, 71, 259, 5);
    teamfarbe.put(player.getDisplayName(), "");

                                         }

  if(dx == 27 && dy == 67 && dz == 265){

    Player player = world.getClosestPlayer(27, 67, 265, 5);
    teamfarbe.remove(player.getDisplayName());
    player.setPrefix(ChatFormat.WHITE + "");

                                        }
                                                             }
  
  @HookHandler
 public void teleportaussnowballhub(TeleportHook event){
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

        teamfarbe.remove(player.getDisplayName());
        player.setPrefix(ChatFormat.WHITE + "");

                              }}

                                                         }
 

  @HookHandler
  public void schneeballtrifft(ProjectileHitHook event){
  	if(gameon){
     Entity schneeball = event.getProjectile();
     Entity ent = event.getEntityHit();

     if (schneeball.getEntityType() == EntityType.SNOWBALL) {
      if(ent instanceof Player){
        Player player = (Player)ent;
        if(player.getHealth() > schneeballschaden){
        player.setHealth(player.getHealth()-schneeballschaden);
        return;
        }
        if(player.getHealth() <= schneeballschaden){
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + wichcolour(player.getName()) + ChatFormat.DARK_GREEN + " ist gestorben.");
        Location snowballhub = new Location(35, 67, 259);
        player.teleportTo(snowballhub);
      	resetgame();
        }
      }
    }
  }
}


  @HookHandler
  public void noreg(HealthChangeHook event){
  	Player player = event.getPlayer();
  	float lebenvorher = event.getOldValue();
  	float lebennacher = event.getNewValue();
  	if(lebennacher > lebenvorher && lebenvorher >0 && gameon){
  		player.setHealth(lebenvorher);
  	}	
  }

  @HookHandler
  public void onEntityDamage(DamageHook event) {
  	if(gameon){

    Entity ent = event.getDefender();
    Entity at = event.getAttacker();

    if (ent instanceof Player) {

      Player player = (Player) ent;
      if(event.getDamageDealt()>=player.getHealth()){
      	event.setDamageDealt(0f);
      	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + wichcolour(player.getName()) + ChatFormat.DARK_GREEN +" ist gestorben.");
      	Location snowballhub = new Location(35, 67, 259);
        player.teleportTo(snowballhub);
      	resetgame();
      }
      if(at instanceof Player){
      	event.setCanceled();
      }
      if (event.getDamageSource().getDamagetype() == DamageType.FALL && nofalldmg) {
      	event.setCanceled();
      }
  	}
  }
}

    @HookHandler
  public void einstellungen_teamauswahl(BlockRightClickHook event){

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

    if(teamfarbe.size() > 1){

      spielerliste = Canary.getServer().getPlayerList();

                            }

    if(y==67){// alle bei x = 37
      if(z==260){
      	if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("purple")){
      		          if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("purple")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

      	teamfarbe.put(player.getDisplayName(), "purple");
      	farbwahlgetroffen = true;
        player.setPrefix(ChatFormat.DARK_PURPLE + "");
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_PURPLE + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.DARK_PURPLE + "lila" + ChatFormat.DARK_GREEN + ".");
      	startgame();
      }}

      if(z==262){
      	if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("yellow")){

      	if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("yellow")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

      	teamfarbe.put(player.getDisplayName(), "yellow");
      	farbwahlgetroffen = true;
        player.setPrefix(ChatFormat.YELLOW + "");
        Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.YELLOW + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.YELLOW + "gelb" + ChatFormat.DARK_GREEN + ".");
      	startgame();
      }}         
    }

    if(y==69){
    	if(z==260){
    		if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("red")){

    		 if(teamfarbe.size() > 1){
              for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
               if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                 if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("red")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 
    	  teamfarbe.put(player.getDisplayName(), "red");
    	  farbwahlgetroffen = true;
          player.setPrefix(ChatFormat.RED + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.RED + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.RED + "rot" + ChatFormat.DARK_GREEN + ".");
    	  startgame();
    	}}
    	if(z==262){
    		if(!teamfarbe.get(player.getDisplayName()).equalsIgnoreCase("green")){

    	if(teamfarbe.size() > 1){
            for(Player spieler : spielerliste){
              double xs = spieler.getX();
              double zs = spieler.getZ();
              if(xs >= 25 && xs <= 37 && zs >= 255 && zs <= 267){
                if(teamfarbe.get(spieler.getDisplayName()).equalsIgnoreCase("green")){

                  teamschonvergebenmessage();
                  return;

                                                                                   }
                                                                  }
                                              }
                                  } 

    	  teamfarbe.put(player.getDisplayName(), "green");
    	  farbwahlgetroffen = true;
          player.setPrefix(ChatFormat.DARK_GREEN + "");
          Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.GREEN + player.getDisplayName() + ChatFormat.DARK_GREEN + " ist jetzt in Team " + ChatFormat.GREEN + "gruen" + ChatFormat.DARK_GREEN + ".");
          startgame();
    	}}
    } 









    if(geklickterblock.getType() == BlockType.WallSign){
    	
    	if(fallgeklickt){
     if(x == 26 && z == 262 && nofalldmg){
      nofalldmg = false;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Fallschaden ist " + ChatFormat.GOLD + "an" + ChatFormat.DARK_GREEN +".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
           }
      if(x == 26 && z == 260 && !nofalldmg){     
      nofalldmg = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " +ChatFormat.DARK_GREEN + "Fallschaden ist " +ChatFormat.GOLD + "aus" + ChatFormat.DARK_GREEN + ".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
  }}
  if(!fallgeklickt){
  	     if(x == 26 && z == 262){
      nofalldmg = false;
      fallgeklickt = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Fallschaden ist " + ChatFormat.GOLD + "an" + ChatFormat.DARK_GREEN +".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
           }
      if(x == 26 && z == 260){     
      nofalldmg = true;
      fallgeklickt = true;
      Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " +ChatFormat.DARK_GREEN + "Fallschaden ist " +ChatFormat.GOLD + "aus" + ChatFormat.DARK_GREEN + ".");
      lampfall.getWorld().setBlockAt(lampfall, lampean);
  }
  }
  	if(schneeballschadengeklickt){
  	  if(x == 30 && z == 256 && schneeballschaden !=2){
  	  	schneeballschaden = 2;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Herz Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  if(x == 32 && z == 256 && schneeballschaden !=4){
  	  	schneeballschaden = 4;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Herzen Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }}
  	  if(!schneeballschadengeklickt){
  	  	  	  if(x == 30 && z == 256){
  	  	schneeballschaden = 2;
  	  	schneeballschadengeklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Herz Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  if(x == 32 && z == 256){
  	  	schneeballschaden = 4;
  	  	schneeballschadengeklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Schneeball macht " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Herzen Schaden.");
  	  	lampeasyhard.getWorld().setBlockAt(lampeasyhard, lampean);
  	  }
  	  }
  	  if(player2geklickt){
  	  if(x == 36 && z == 262 && !player2){
  	  	player2=true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielen " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }
  	  if(x == 36 && z == 260 && player2){
  	  	player2=false;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielt " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }}
  	  if(!player2geklickt){
  	   	  if(x == 36 && z == 262){
  	  	player2=true;
  	  	player2geklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielen " + ChatFormat.GOLD + "zwei" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  }
  	  if(x == 36 && z == 260){
  	  	player2=false;
  	  	player2geklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Es spielt " + ChatFormat.GOLD + "ein" + ChatFormat.DARK_GREEN + " Spieler.");
  	  	lampplayer12.getWorld().setBlockAt(lampplayer12, lampean);
  	  } 	
  	  }
  	  if(pvpgeklickt){
  	  if(x == 32 && z == 266 && !pvp){
  	  	pvp = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }
  	  if(x == 30 && z == 266 && pvp){
  	  	pvp = false;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVE" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }}
  	  if(!pvpgeklickt){

  	  if(x == 32 && z == 266){
  	  	pvp = true;
  	  	pvpgeklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }
  	  if(x == 30 && z == 266){
  	  	pvp = false;
  	  	pvpgeklickt = true;
  	  	Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA+"[Schneeballarena] "+ ChatFormat.DARK_GREEN + "Der Modus " + ChatFormat.GOLD + "PVE" + ChatFormat.DARK_GREEN+" wird gespielt.");
  	  	lamppvppve.getWorld().setBlockAt(lamppvppve, lampean);
  	  }}
  	  startgame();
  	  }

  

    

                                                                       }
        

public void resetredblocks(){
	Location lampfall = new Location(25, 68, 261);
    Location lampeasyhard = new Location(31, 68, 255);
    Location lampplayer12 = new Location(37, 68, 261);
    Location lamppvppve = new Location(31, 68, 267);
    BlockType lampeaus = BlockType.RedstoneBlock;
    World world = lampfall.getWorld();

    world.setBlockAt(lampfall, lampeaus);
    world.setBlockAt(lampeasyhard, lampeaus);
    world.setBlockAt(lampplayer12, lampeaus);
    world.setBlockAt(lamppvppve, lampeaus);
    world.setRaining(false);
}   

public void startgame(){

	gamenostart();

	if(fallgeklickt && schneeballschadengeklickt && pvpgeklickt && player2geklickt && farbwahlgetroffen){

		gameon = true;
		StartGame1 task = new StartGame1(this, player2);  
        Canary.getServer().addSynchronousTask(task);
        alltasks.put(1, task);
 }

}

public void resetgame(){
	    fallgeklickt = false;
    	schneeballschadengeklickt = false;
    	pvpgeklickt = false;
    	player2geklickt = false;
    	farbwahlgetroffen = false;
    	gameon = false;
    	resetredblocks();
}

public void teamschonvergebenmessage(){

  Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Diese Farbe ist bereits vergeben.");

                                       }

public void gamenostart(){

	  	if(pvp && !player2){
		Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Spielmodus " + ChatFormat.GOLD + "PVP" + ChatFormat.DARK_GREEN + " braucht mindestens 2 Spieler.");
		pvpgeklickt = false;
		player2geklickt = false;
		pvp = false;
		player2 = false;
		Location lampplayer12 = new Location(37, 68, 261);
        Location lamppvppve = new Location(31, 68, 267);
        World world = lamppvppve.getWorld();
        BlockType lampeaus = BlockType.RedstoneBlock;
        world.setBlockAt(lampplayer12, lampeaus);
    	world.setBlockAt(lamppvppve, lampeaus);
		return;
	}


	int booleanzahl = 0;

		if(fallgeklickt){
		booleanzahl++;
	}
	if(schneeballschadengeklickt){
		booleanzahl++;
	}
	if(pvpgeklickt){
		booleanzahl++;
	}
	if(player2geklickt){
		booleanzahl++;
	}
	if(farbwahlgetroffen){
		booleanzahl++;
	}
	if(booleanzahl == 4){

		if(!fallgeklickt){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] "+ ChatFormat.GOLD + "Fallschaden " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
		}
		if(!schneeballschadengeklickt){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] "+ ChatFormat.GOLD + "EASY/HARD " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
		}
		if(!pvpgeklickt){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] "+ ChatFormat.GOLD + "PVE/PVP " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
		}
		if(!player2geklickt){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] "+ ChatFormat.GOLD + "Spieleranzahl " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
		}
		if(!farbwahlgetroffen){
			Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] "+ ChatFormat.GOLD + "Spielerfarbe " + ChatFormat.DARK_GREEN + "muss noch eingestellt werden.");
		}
				for(Player player : spielerliste){
			if(teamfarbe.get(player.getName()).equalsIgnoreCase("")){
				if(pvp){
				Canary.instance().getServer().broadcastMessage(ChatFormat.DARK_AQUA + "[Schneeballarena] " + ChatFormat.DARK_GREEN + "Der Spieler " + player.getName() + " hat seine Farbe noch nicht gewaehlt.");
				farbwahlgetroffen=false;
				return;
			 }
			 if(!pvp&&player2){
			 	for(Player playerf : spielerliste){
			 		String cocap=teamfarbe.get(playerf.getName());
			 		if(!cocap.equalsIgnoreCase("")) {
			 			teamfarbe.put(player.getDisplayName(), cocap);
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

public String wichcolour(String name){
	String colorname = "";
	if(teamfarbe.get(name).equalsIgnoreCase("green")){
		 colorname = ChatFormat.GREEN + name;
	}
	if(teamfarbe.get(name).equalsIgnoreCase("red")){
		colorname = ChatFormat.RED + name;
	}
	if(teamfarbe.get(name).equalsIgnoreCase("yellow")){
		colorname = ChatFormat.YELLOW + name;
	}
	if(teamfarbe.get(name).equalsIgnoreCase("purple")){
		colorname = ChatFormat.DARK_PURPLE + name;
	}
	return colorname;
}


}
