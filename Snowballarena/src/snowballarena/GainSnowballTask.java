package snowballarena;
import net.canarymod.tasks.ServerTask;
import utils.Utils;
import net.canarymod.Canary;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.effects.SoundEffect;

public class GainSnowballTask extends ServerTask{
	private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;
	
	private Game game;
	private int elapsedTime;

	public GainSnowballTask(Game game){
		super(Canary.getServer(), TaskDelay, isContinousTask);
		this.game = game;
	}

	public void run(){
		if(elapsedTime > 0){
			Player player1 = game.getPlayer1();
			Player player2 = game.getPlayer2();

			if(elapsedTime % 7 == 0 && elapsedTime > 0){
				givePlayerSnowball(player1);
				givePlayerSnowball(player2);
			}
			else if(elapsedTime % 45 == 0){
   				float p1health = player1.getHealth();
   				float p2health = player2.getHealth();
   				
				if(p1health < 20 || p2health < 20){
					game.setHeal(true);
					healPlayer(player1);
					healPlayer(player2);
				}
			}
		}

		elapsedTime++;
	}

	private void givePlayerSnowball(Player player){
		player.getInventory().addItem(ItemType.SnowBall);
		Snowballarena.playSoundByPlayer(player, SoundEffect.Type.ITEM_PICKUP, 0.2f, 0f);
	}

	private void healPlayer(Player player){
		if(player.getHealth() < 20){
			Snowballarena.healCounter++;
			player.setHealth(player.getHealth() + 2);
			Snowballarena.playSoundByPlayer(player, SoundEffect.Type.DRINK, 0.3f, 0f);
		}
	}

	public void endItemTask(){
		Canary.getServer().removeSynchronousTask(this);
	}
}