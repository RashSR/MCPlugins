package snowballarena;
import net.canarymod.tasks.ServerTask;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.entity.living.humanoid.Player;

public class WaitTask extends ServerTask{
	private Game game;
	private int timeInS=20;
	private float volume = 1f;
	private float pitch = 0.4f;

	public WaitTask(Game game){
		super(Canary.getServer(), 20, true);
		this.game=game;
	}

	public void run(){
		if(timeInS==0){
			Canary.instance().getServer().broadcastMessage(Game.tag+ChatFormat.DARK_GREEN+"Das Spiel "+ChatFormat.GOLD+"beginnt"+ChatFormat.DARK_GREEN+"!");
			Canary.getServer().removeSynchronousTask(this);
			game.startGame();
			Player pl1 = game.getPlayer1();
			Player pl2 = game.getPlayer2();
			if(pl1!=null){
				Snowballarena.playSoundByPlayer(pl1, SoundEffect.Type.LEVEL_UP, volume, 0);
			}
			if(pl2!=null){
				Snowballarena.playSoundByPlayer(pl2, SoundEffect.Type.LEVEL_UP, volume, 0);
			}
			return;
		}
		
		if(timeInS%5==0||timeInS<5){
			pitch+=0.1f;
			if(timeInS==1){
				Canary.instance().getServer().broadcastMessage(Game.tag+ChatFormat.DARK_GREEN+"Spiel beginnt in "+ChatFormat.GOLD+"einer"+ChatFormat.DARK_GREEN+" Sekunde.");
			}else{
				Canary.instance().getServer().broadcastMessage(Game.tag+ChatFormat.DARK_GREEN+"Spiel beginnt in "+ChatFormat.GOLD+timeInS+ChatFormat.DARK_GREEN+" Sekunden.");
			}
			game.playSound(new Location(31, 68, 261), SoundEffect.Type.ORB, volume, pitch);
		}
		timeInS--;
	}

	public void setTimeInS(int time){
		if(time>=0){
			this.timeInS=time;
		}
	}
}