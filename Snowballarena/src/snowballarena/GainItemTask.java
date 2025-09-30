package snowballarena;
import net.canarymod.tasks.ServerTask;
import net.canarymod.Canary;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.effects.SoundEffect;

public class GainItemTask extends ServerTask{
	private Game game;
	private int timeInS;

	public GainItemTask(Game game){
		super(Canary.getServer(), 20, true);
		this.game = game;
	}

	public void run(){
		if(timeInS>0){
			Player player1 = game.getPlayer1();
			Player player2 = game.getPlayer2();
			if(timeInS%7==0&&timeInS>0){
				player1.getInventory().addItem(ItemType.SnowBall);
				player2.getInventory().addItem(ItemType.SnowBall);
				Snowballarena.playSoundByPlayer(player1, SoundEffect.Type.ITEM_PICKUP, 0.2f, 0f);
				Snowballarena.playSoundByPlayer(player2, SoundEffect.Type.ITEM_PICKUP, 0.2f, 0f);
			}
			if(timeInS%45==0){
   				float p1health = player1.getHealth();
   				float p2health = player2.getHealth();
   				int healCounter=0;
   				if(p1health<20 || p2health<20){
   					game.setHeal(true);
   				}
   				if(p1health<20){
   					healCounter++;
					player1.setHealth(player1.getHealth()+2);
					Snowballarena.playSoundByPlayer(player1, SoundEffect.Type.DRINK, 0.3f, 0f);
				}
				if(p2health<20){
					healCounter++;
					player2.setHealth(player2.getHealth()+2);
					Snowballarena.playSoundByPlayer(player2, SoundEffect.Type.DRINK, 0.3f, 0f);
				}
				Snowballarena.healCounter=healCounter;
			}
		}
		/*
		-crafting?
		-besserer Map Randomizer
		-yannick und tobi mode
		*/

		timeInS++;
	}

	public void endItemTask(){
		Canary.getServer().removeSynchronousTask(this);
	}
}