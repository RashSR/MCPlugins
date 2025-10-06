package utils;
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
    private int scoreboardSlot;

    public ScoreboardTimerTask(Scoreboard scoreboard, ScoreObjective objective, Score timeScore, int scoreboardSlot) {
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.timeScore = timeScore;
        this.timeScore = scoreboard.getScore("§aTime: §f00:00", this.objective);
        this.scoreboardSlot = scoreboardSlot;
        this.timeScore.setScore(scoreboardSlot);
        this.timeScore.update();
    }

    public void run(){
        passedSeconds++;
        this.scoreboard.removeScore(this.timeScore.getName(), this.objective);
        this.timeScore = this.scoreboard.getScore("§aTime: §f" + formatPassedTime(), this.objective);
        this.timeScore.setScore(this.scoreboardSlot);
        this.timeScore.update();
    }

    public String getElapsedTime(){
        return formatPassedTime();
    }

    private String formatPassedTime(){
        String result = "";

        int minutes = 0;
        int mySeconds = passedSeconds;
        while(mySeconds >= 60){
            mySeconds -= 60;
            minutes++;
        }
        
        //Add leading zero for mm:ss
        if(minutes < 10)
            result += "0";

        result += minutes + ":";

        if(mySeconds < 10)
            result += "0";

        result += mySeconds;
        return result;
    }
}
