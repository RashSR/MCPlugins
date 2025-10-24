package snowballarena;
import net.canarymod.tasks.ServerTask;
import utils.Utils;
import net.canarymod.Canary;
import net.canarymod.chat.ChatFormat;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.entity.living.humanoid.Player;

public class WaitTask extends ServerTask{
	private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;

	private Game game;
	private int timeInSeconds = 20;
	private float volume = 1f;
	private float pitch = 0.4f;

	public WaitTask(Game game){
		super(Canary.getServer(), TaskDelay, isContinousTask);
		this.game = game;
	}

	public void run(){
		if(timeInSeconds == 0){
			Utils.BroadcastServerMessage(Game.pluginName, "Das Spiel "+ ChatFormat.GOLD + "beginnt" + ChatFormat.DARK_GREEN + "!");
			Canary.getServer().removeSynchronousTask(this);
			game.startGame();
			Player pl1 = game.getPlayer1();
			Player pl2 = game.getPlayer2();
			
			if(pl1 != null)
				Snowballarena.playSoundByPlayer(pl1, SoundEffect.Type.LEVEL_UP, volume, 0);
			if(pl2 != null)
				Snowballarena.playSoundByPlayer(pl2, SoundEffect.Type.LEVEL_UP, volume, 0);

			return;
		}
		
		if(timeInSeconds % 5 == 0|| timeInSeconds < 5){
			pitch += 0.1f;
			if(timeInSeconds == 1)
				Utils.BroadcastServerMessage(Game.pluginName, "Spiel beginnt in " + ChatFormat.GOLD + "einer" + ChatFormat.DARK_GREEN + " Sekunde.");
			else
				Utils.BroadcastServerMessage(Game.pluginName, "Spiel beginnt in " + ChatFormat.GOLD + timeInSeconds + ChatFormat.DARK_GREEN + " Sekunden.");

			game.playSound(new Location(31, 68, 261), SoundEffect.Type.ORB, volume, pitch);
		}

		timeInSeconds--;
	}

	public void setTimeInSeconds(int time){
		if(time >= 0)
			timeInSeconds=time;
	}
}