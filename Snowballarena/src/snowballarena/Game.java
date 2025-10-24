package snowballarena;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.Server;
import net.canarymod.api.world.position.Location;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.World;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.inventory.ItemType;

public class Game extends EZPlugin{
	private Player player1;
	private Player player2;
	private boolean hasFallDmg = false;
	static Server server = Canary.instance().getServer();
	protected static String pluginName = ChatFormat.DARK_AQUA + "[Snowballarena] ";
	private boolean isPvp = true;
	private boolean changedFallDmg = false;
	private boolean changedPvp = false;
	private int snowballDmg = 2;
	private boolean hasStarted = false; 
	private Map map; //falls null map = Map.randomMap();
	private WaitTask waitTask;
	private World world;
	private GainSnowballTask gainTask;
	private int amountStartSnowballs = 3;
	private boolean canHeal = false;
	private int MAX_KNOCKBACK = 2;
	private Snowballarena sa;
	private boolean isFirstMatch = true; 

	public Game(World world, Map map, Snowballarena sa){
		this.world=world;
		this.map=map;
		this.sa=sa;
		isFirstMatch=false;
	}

	public Game(World world, Snowballarena sa){
		server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN + "Ein neues Spiel beginnt!"); 
		this.world=world;
		this.sa = sa;
		this.map = Map.QUIDDITCH;
	}

	public void setSnowballDmg(int dmg){
		if(dmg==this.snowballDmg){
			return;
		}
		String endung = "";
		if(dmg!=2){
			endung="en";
		}
		snowballDmg=dmg;
		server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN+"Ein Schneeball macht "+ChatFormat.GOLD+dmg/2+" Herz"+endung+ChatFormat.DARK_GREEN+"Schaden.");
	}

	public void setPvp(boolean isPvp){
		if(isPvp==this.isPvp&&changedPvp){
			return;
		}
		this.isPvp=isPvp;
		String flag;
		if(isPvp){
			flag="PVP";
		}else{
			flag="PVE";
		}
		server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN+"Spielmodus wurde auf "+ChatFormat.GOLD+flag+ChatFormat.DARK_GREEN+" gestellt.");
		changedPvp=true;
	}

	public void setPlayer1(Player player){
		if(player1!=null){
			if(!player.getName().equals(player1.getName())){
				server.broadcastMessage(pluginName+ChatFormat.GRAY+"Spieler 1"+ChatFormat.DARK_GREEN+"ist schon an "+ChatFormat.GOLD+player1.getName()+ChatFormat.DARK_GREEN+" vergeben.");
			}
			return;
		}
		if(!isDuplicate(player)){
			this.player1=player;
			if(isFirstMatch){
				server.broadcastMessage(pluginName+ChatFormat.GRAY+"Spieler 1"+ChatFormat.DARK_GREEN+"ist jetzt: "+ChatFormat.GOLD+player.getName()+ChatFormat.DARK_GREEN+".");
			}
		}
	}

	public void setPlayer2(Player player){
		if(player2!=null){
			if(!player.getName().equals(player2.getName())){
				server.broadcastMessage(pluginName+ChatFormat.GRAY+"Spieler 2"+ChatFormat.DARK_GREEN+"ist schon an "+ChatFormat.GOLD+player2.getName()+ChatFormat.DARK_GREEN+" vergeben.");
			}
			return;
		}
		if(!isDuplicate(player)){
			this.player2=player;
			if(isFirstMatch){
				server.broadcastMessage(pluginName+ChatFormat.GRAY+"Spieler 2"+ChatFormat.DARK_GREEN+" ist jetzt: "+ChatFormat.GOLD+player.getName()+ChatFormat.DARK_GREEN+".");
			}
		}
	}

	public void setFallDmg(boolean hasFallDmg){
		if(hasFallDmg==this.hasFallDmg&&changedFallDmg){
			return;
		}
		this.hasFallDmg = hasFallDmg;
		String flag;
		if(hasFallDmg){
			flag="an";
		}else{
			flag="aus";
		}
		server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN+"Fallschaden ist jetzt "+ChatFormat.GOLD+flag+ChatFormat.DARK_GREEN+".");
		changedFallDmg=true;
	}

	private boolean isDuplicate(Player player){
		String playerName = player.getName();
		if(player1==null&&player2==null){
			return false;
		}
		if(player1 != null){
			if(playerName.equals(player1.getName())){
				server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN+"Spieler "+ChatFormat.GOLD+playerName+ChatFormat.DARK_GREEN+" ist "+ChatFormat.DARK_RED+"bereits"+ChatFormat.DARK_GREEN+" beigetreten!");
				return true;
			}
		}
		if(player2!=null){
			if(playerName.equals(player2.getName())){
				server.broadcastMessage(pluginName+ChatFormat.DARK_GREEN+"Spieler "+ChatFormat.GOLD+playerName+ChatFormat.DARK_GREEN+" ist "+ChatFormat.DARK_RED+"bereits"+ChatFormat.DARK_GREEN+" beigetreten!");
				return true;
			}
		}
		return false;
	}

	public void checkForTwoPlayers(){
		if(player1!=null&&player2!=null){
			logger.info("Beide Spieler sind nicht null");
			if(this.waitTask==null){
				startWaitTask();
				logger.info("Der Task ist gestartet!");
			}
		}
	}

	public void teleportPlayersGame(){
		Location loc1;
		Location loc2;
		if(this.map==null){
			this.map=Map.randomMap();
		}
		switch(this.map){
			case QUIDDITCH:
				loc1 = new Location(this.world, 139.5, 130, 309.5, 0f, -90f);
				loc2 = new Location(this.world, 187.5, 130, 309.5, 0f, 90f);
				break;
			case NETHER:
				loc1 = new Location(this.world, 163.5, 148, 335.5, 0f, 0f);
				loc2 = new Location(this.world, 163.5, 148, 394.5, 0f, 180f);
				break;
			case CHRISTMAS:
				loc1 = new Location(this.world, 226.5, 123, 309.5, 0f, 90f);
				loc2 = new Location(this.world, 191.5, 123, 309.5, 0f, -90f);
				break;
			case SHRIEKING_SHACK:
				loc1 = new Location(this.world, 209.5, 141, 274.5, 0f, 90f);
				loc2 = new Location(this.world, 141.5, 148, 284.5, 0f, -90f);
				break;
			case SNOWARENA: 
				loc1 = new Location(this.world, 28.5, 108, 276.5, 0f, 180f);
				loc2 = new Location(this.world, 29.5, 108, 231.5, 0f, 0f);
				break;
			case BEDWARS:
				loc1 = new Location(this.world, 328, 228, 395, 0f, -90f);
				loc2 = new Location(this.world, 527, 227, 395, 0f, 90f);
				break;
			default: 
				logger.info(pluginName+"Es wurde keine Map zugewiesen.");
				loc1 = new Location(world,0, 0, 0, 0f, 0f);
				loc2 = new Location(world,0, 0, 0, 0f, 0f);
		}
		if(player1!=null){
			player1.teleportTo(loc1);
		}
		if(player2!=null){
			player2.teleportTo(loc2);	
		}
	}

	public void teleportPlayersHome(){
		if(player1!=null){
			player1.teleportTo(new Location(player1.getWorld(), 35.5, 67, 261.5, 0f, 90f));
		}
		if(player2!=null){
			player2.teleportTo(new Location(player2.getWorld(), 35.5, 67, 261.5, 0f, 90f));
		}
	}

	public void hitPlayer(Player hitPlayer){
		Player throwingPlayer;
		if(isPlayer1(hitPlayer)){
            hitPlayer=player1;
            throwingPlayer=player2;
        }else{
            hitPlayer=player2;
            throwingPlayer=player1;
        }
        Snowballarena.playSoundByPlayer(throwingPlayer, SoundEffect.Type.NOTE_PLING, 1f, 0f);
        Snowballarena.playSoundByPlayer(hitPlayer, SoundEffect.Type.HURT_FLESH, 1f, 0f);
        if(hitPlayer.getHealth() > snowballDmg){
            hitPlayer.setHealth(hitPlayer.getHealth()-snowballDmg);
            givePlayerKnockback(hitPlayer, throwingPlayer);
            return;
        }else{
        	Canary.instance().getServer().broadcastMessage(pluginName + ChatFormat.GOLD + throwingPlayer.getName() + ChatFormat.DARK_GREEN + " hat mit "+ChatFormat.GOLD+throwingPlayer.getHealth()/2+ChatFormat.DARK_GREEN+" verbleibenden Herzen gewonnen.");
        	hitPlayer.setHealth(20f);
        	throwingPlayer.setHealth(20f);
        	endGame(throwingPlayer);
        }
	}

	private void givePlayerKnockback(Player hitPlayer, Player throwingPlayer){
		double x = 0;
		double y = Math.random();
		double z = 0;
		Direction dir = whichDirection(hitPlayer, throwingPlayer);
		if(dir!=null){
			switch(dir){
				case POS_X:
					x = Math.random()*MAX_KNOCKBACK;
					break;
				case NEG_X:
					x = Math.random()*(-MAX_KNOCKBACK);
					break;
				case POS_Z:
					z = Math.random()*MAX_KNOCKBACK;
					break;
				case NEG_Z:
					z = Math.random()*(-MAX_KNOCKBACK);
					break;
				case POS_X_POS_Z:
					x = Math.random()*MAX_KNOCKBACK; 
					z = Math.random()*MAX_KNOCKBACK;
					break;
				case NEG_X_NEG_Z:
					x = Math.random()*(-MAX_KNOCKBACK);
					z = Math.random()*(-MAX_KNOCKBACK);
					break;
				case POS_X_NEG_Z:
					x = Math.random()*MAX_KNOCKBACK;
					z = Math.random()*(-MAX_KNOCKBACK);
					break;
				case NEG_X_POS_Z:
					x = Math.random()*(-MAX_KNOCKBACK);
					z = Math.random()*MAX_KNOCKBACK;
					break;
			}
		}
		if(this.map == Map.SNOWARENA){
			y = 0.5;
		}
		hitPlayer.moveEntity(x, y, z);
	}
	
	//gibt Richtung zurück in die der übergebene Player schaut
	private Direction whichDirection(Player hitPlayer, Player throwingPlayer){
		float headRotation = throwingPlayer.getLocation().getRotation();
		headRotation=cleanRot(headRotation);
		Location loc = hitPlayer.getLocation();

		if(headRotation >= 22.5 && headRotation < 67.5){
			return Direction.NEG_X_POS_Z;
		}else if(headRotation >= 67.5 && headRotation < 112.5){
			return Direction.NEG_X;
		}else if(headRotation >= 112.5 && headRotation < 157.5){
			return Direction.NEG_X_NEG_Z;
		}else if(headRotation >= 157.5 && headRotation < 202.5){
			return Direction.NEG_Z;
		}else if(headRotation >= 202.5 && headRotation < 247.5){
			return Direction.POS_X_NEG_Z;
		}else if(headRotation >= 247.5 && headRotation < 292.5){
			return Direction.POS_X;
		}else if(headRotation >= 292.5 && headRotation < 337.5){
			return Direction.POS_X_POS_Z;
		}else{
			return Direction.POS_Z;
		}
	}

	//Erstellt aus einer float-Zahl die bereinigte 360 Grad Zahl
	private float cleanRot(float rot){
		if(rot < 0){
			return rot+360;
		} else {
			return rot;
		}
	}

	public void endGame(Player winner){
		hasStarted=false;
		teleportPlayersHome();
		world.setDifficulty(World.Difficulty.PEACEFUL);
		world.setRaining(false);
		sa.game = null;
		this.waitTask=null;
		player1.getInventory().clearInventory();
		player2.getInventory().clearInventory();
		this.gainTask.endItemTask();
		if(player1.getFireTicks()>0){
			player1.setFireTicks(0);
		}
		if(player2.getFireTicks()>0){
			player2.setFireTicks(0);
		}
		player1.setHealth(20f);
		player2.setHealth(20f);

		sa.createNewGame(this, winner);
	}

	public void startGame(){
		player1.setHealth(20f);
		player2.setHealth(20f);
		hasStarted=true;
		teleportPlayersGame();
		player1.getInventory().addItem(ItemType.SnowBall, amountStartSnowballs);
		player2.getInventory().addItem(ItemType.SnowBall, amountStartSnowballs);
		world.setDifficulty(World.Difficulty.EASY);
		this.gainTask = new GainSnowballTask(this);
		Canary.getServer().addSynchronousTask(this.gainTask);
	}

	public void startWaitTask(){
		world.setRaining(true);
		this.waitTask = new WaitTask(this);
        Canary.getServer().addSynchronousTask(this.waitTask);
	}

	public boolean hasFallDmg(){
		return this.hasFallDmg;
	}

	public WaitTask getWaitTask(){
		return this.waitTask;
	}

	public Player getPlayer1(){
		return this.player1;
	}

	public Player getPlayer2(){
		return this.player2;
	}

	public int getSnowballDmg(){
		return this.snowballDmg;
	}

	public boolean isPlayer1(Player player){
		if(player.getName().equals(player1.getName())){
			return true;
		}
		return false;
	}

	public boolean hasStarted(){
		return this.hasStarted;
	}

	public boolean canHeal(){
		return this.canHeal;
	}

	public void setHeal(boolean canHeal){
		this.canHeal = canHeal;
	}

	public World getWorld(){
		return this.world;
	}

	public void setFall(boolean doDamge){
		this.hasFallDmg=true;
	}
}