package snowballarena;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.world.position.Location;
import net.canarymod.plugin.PluginListener;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.HookHandler;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.entity.DamageHook;
import net.canarymod.api.DamageType;
import net.canarymod.hook.entity.ProjectileHitHook;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.hook.player.HealthChangeHook;
import net.canarymod.hook.player.FoodLevelHook;
import net.canarymod.hook.entity.EntitySpawnHook;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

public class Snowballarena extends EZPlugin implements PluginListener{
    public static Game game;
    public static int healCounter;
    private HashMap<String, Integer> wins = new HashMap<String, Integer>();
    private int winsToFinish = 6;
    private int timeToNextGame = 3;
    private int mapIterator = 1;

    @Command(aliases = { "snowballarena" },
            description = "snowballarena plugin",
            permissions = { "*" },
            toolTip = "/snowballarena")
    public void snowballarenaCommand(MessageReceiver caller, String[] args) {
        if (caller instanceof Player) { 
            Player player = (Player)caller;
            if(args.length==1){
                player.teleportTo(new Location(player.getWorld(), 35.5, 67, 261.5, 0f, 90f));
            }else if(args.length>=2&&game!=null){
                if(args[1].equals("off")){
                    game.endGame(null);
                    game=null;
                }else if(args[1].equals("on")){
                    //game.startWaitTask();
                    int time = Integer.parseInt(args[2]);
                    game.getWaitTask().setTimeInS(time);
                }
            }
        }
    }

    @Override
    public boolean enable() { 
        Canary.hooks().registerListener(this, this);
        return super.enable(); 
    }

    @HookHandler 
    public void rightClickevent(BlockRightClickHook event){
        Block blockClicked = event.getBlockClicked();
        Player player = event.getPlayer();
        int locX = blockClicked.getX();
        int locZ = blockClicked.getZ();
        if(blockClicked.getType() == BlockType.WallSign && isInHub(locX, blockClicked.getY(), locZ)){
            if(locX==36){
                if(locZ==260){
                    addingPlayer(player, 1);
                }else if(locZ==262){
                    addingPlayer(player, 2);
                }
            }else if(locX==26&&game!=null){
                if(locZ==260){
                    game.setFallDmg(false);
                }else if(locZ==262){
                    game.setFallDmg(true);
                }
            }else if(locZ==256&&game!=null){
                if(locX==30){
                    game.setSnowballDmg(2);
                }else if(locX==32){
                    game.setSnowballDmg(4);
                }
            }else if(locZ==266&&game!=null){
                if(locX==30){
                    game.setPvp(false);
                }else if(locX==32){
                    game.setPvp(true);
                }
            }else{
                Canary.instance().getServer().broadcastMessage(Game.tag+ChatFormat.DARK_GREEN+"Zum Spielstart bitte einen "+ChatFormat.GOLD+"Spieler"+ChatFormat.DARK_GREEN+" auswaehlen.");
            }    
        }
    }

    @HookHandler
    public void noFallDmg(DamageHook event){
            if(event.getAttacker() instanceof Player){
                    event.setCanceled(); //Spieler können sich nicht gegenseitig schlagen
            }
            if(event.getDamageSource().getDamagetype()==DamageType.FALL&&(!game.hasFallDmg())) {
                    event.setCanceled();
            }
            else if(game.hasStarted()){
                Entity ent = event.getDefender();
                if(ent instanceof Player){
                    Player p = (Player)ent;
                    if(p.getHealth()<=event.getDamageDealt()){
                        event.setCanceled();
                        if(game.isPlayer1(p)){
                            Canary.instance().getServer().broadcastMessage(game.tag + ChatFormat.GOLD + game.getPlayer2().getName() + ChatFormat.DARK_GREEN + " hat mit "+ChatFormat.GOLD+game.getPlayer2().getHealth()/2+ChatFormat.DARK_GREEN+" verbleibenden Herzen gewonnen.");
                            game.endGame(game.getPlayer2());
                        }else{
                            Canary.instance().getServer().broadcastMessage(game.tag + ChatFormat.GOLD + game.getPlayer1().getName() + ChatFormat.DARK_GREEN + " hat mit "+ChatFormat.GOLD+game.getPlayer1().getHealth()/2+ChatFormat.DARK_GREEN+" verbleibenden Herzen gewonnen.");
                            game.endGame(game.getPlayer1());
                        }
                        //game.endGame();
                    }
                }
            }
        }

    public void createNewGame(Game oldGame, Player winner){
        wins.put(winner.getName(), wins.get(winner.getName())+1); //erhöht Die Anzahl der Siege des Gewinners um 1
        if(mapIterator==5){
            mapIterator=0;
        }
        Player player1old = oldGame.getPlayer1();
        Player player2old = oldGame.getPlayer2();

        int p1wins = wins.get(player1old.getName());
        int p2wins = wins.get(player2old.getName());
        Canary.instance().getServer().broadcastMessage(game.tag+ChatFormat.RED+"Punktestand: "+ChatFormat.GOLD+player1old.getName()+" "+ChatFormat.GRAY+p1wins+ChatFormat.DARK_GREEN+":"+ChatFormat.GRAY+p2wins+" "+ChatFormat.GOLD+player2old.getName());
        //if(p1wins == winsToFinish || p2wins == winsToFinish){
        if(p1wins+p2wins==11){
            if(p1wins>p2wins){
                winner = player1old;
            }else{
                winner = player2old;
            }
            Canary.instance().getServer().broadcastMessage(game.tag+ChatFormat.DARK_GREEN+"Der Spieler "+ChatFormat.GOLD+winner.getName()+ChatFormat.DARK_GREEN+" hat gewonnen!");
            return;
        }
        if(p1wins+p2wins==10){
            mapIterator=5;
        }
        List<Map> maps = Arrays.asList(Map.values());
        this.game = new Game(oldGame.getWorld(), maps.get(mapIterator), this); //zweites ist map
        if(mapIterator==5){
            game.setFall(true);
        }
        mapIterator++;
        addingPlayer(player1old, 2);
        addingPlayer(player2old, 1);
        game.getWaitTask().setTimeInS(timeToNextGame);

    }

    private void addingPlayer(Player player, int playerID){
        if(game == null){
            game = new Game(player.getWorld(), this);
        }
        if(playerID==1){
            game.setPlayer1(player);
            if(!wins.containsKey(player.getName())){
                wins.put(player.getName(), 0);
            }
        }else if(playerID==2){
            game.setPlayer2(player);
            if(!wins.containsKey(player.getName())){
                wins.put(player.getName(), 0);
            }
        }
        game.checkForTwoPlayers(); 
    }

    private boolean isInHub(int x, int y, int z){
        if(x>=26 && x<=37 && y>=67 && y<=71 && z>=256 && z<=266){
            return true;
        }else{
            return false;
        }
    }

    @HookHandler
    public void snowballHit(ProjectileHitHook event){
        if(game.hasStarted()){
            Entity snowball = event.getProjectile();
            Entity ent = event.getEntityHit();
            if(snowball.getEntityType() == EntityType.SNOWBALL) {
                if(ent instanceof Player){
                    game.hitPlayer((Player)ent);
                }
            }
        }
    }

    @HookHandler
    public void noReg(HealthChangeHook event){
        if(game!=null){
            if(game.hasStarted()){
                Player player = event.getPlayer();
                if(!game.canHeal()){
                    float preHealth = event.getOldValue();
                    float postHealth = event.getNewValue();
                    if(postHealth > preHealth){
                        player.setHealth(preHealth);
                    }
                }else{
                    if(healCounter > 0){
                        healCounter--;
                    }
                    if(healCounter == 0){
                        game.setHeal(false);
                    }
                }   
            }
        }  
    }

    @HookHandler
    public void noStarve(FoodLevelHook event){
        if(game!=null){
            Player player = event.getPlayer();
            float preFoodLevel = event.getOldValue();
            float postFoodLevel = event.getNewValue();
            if(postFoodLevel < preFoodLevel && game.hasStarted()){
                event.setNewValue(20);
            }
        }
    }

    public static void playSoundByPlayer(Player pl, SoundEffect.Type st, float volume, float pitch){
        playSound(new Location(pl.getX(), pl.getY(), pl.getZ()), st, volume, pitch);
    }
    
    @HookHandler
    public void noMobs(EntitySpawnHook event){
        if(game!=null){
            Entity ent = event.getEntity();
            if(ent.isMob()){
                ent.destroy();
            }
        }
    }
}