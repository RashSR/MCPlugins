package utils;
import java.util.ArrayList;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.Particle.Type;
import net.canarymod.api.world.position.Location;

public class SpawnParticlesTask extends ServerTask{
    private static final long TaskDelay = 1 * Utils.TICKS_PER_SECOND;
    private static final boolean isContinousTask = true;
    
    private int elapsedTimeInSeconds;
    private Location location;
    private ArrayList<Particle.Type> particleTypes;
    private int showAfterDelayInSeconds;
    private IServerTaskCallback callback;

    public SpawnParticlesTask(Location location, ArrayList<Particle.Type> particleTypes, int showAfterDelayInSeconds, IServerTaskCallback callback) {
        super(Canary.getServer(), TaskDelay, isContinousTask);
        this.elapsedTimeInSeconds = 0;
        this.location = location;
        this.particleTypes = particleTypes;
        this.showAfterDelayInSeconds = showAfterDelayInSeconds;
        this.callback = callback;
    }

    public void run(){
        elapsedTimeInSeconds++;
        if(elapsedTimeInSeconds == showAfterDelayInSeconds){
            if(callback != null)
                callback.ExecuteTaskCallback(this);
        }
        
        if(elapsedTimeInSeconds >= showAfterDelayInSeconds)
            for(Particle.Type particleType : particleTypes)
                Utils.SpawnParticleAroundLocation(location, particleType);

    }
}
