package quidditch;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import utils.Utils;
import net.canarymod.api.scoreboard.*;

public class ScoreboardTimerTask extends ServerTask{
    private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND; //20 Ticks -> 1 second -> repeats all 5 Seconds
    private static final boolean isContinousTask = true;

    private Scoreboard scoreboard;
    private ScoreObjective objective;
    private Score timeScore;
    private int passedSeconds = 0;

    public ScoreboardTimerTask(Scoreboard scoreboard, ScoreObjective objective, Score timeScore) {
        
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.timeScore = timeScore;
        this.timeScore = scoreboard.getScore("§aTime: §f0", this.objective);
        this.timeScore.setScore(2);
        this.timeScore.update();
    }

    public void run(){
        passedSeconds++;
        this.scoreboard.removeScore(this.timeScore.getName(), this.objective);
        this.timeScore = scoreboard.getScore("§aTime: §f" + passedSeconds, this.objective);
        this.timeScore.setScore(2);
        this.timeScore.update();
    } 
}
