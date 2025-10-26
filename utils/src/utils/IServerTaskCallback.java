package utils;
import net.canarymod.tasks.ServerTask;

public interface IServerTaskCallback{
    public void ExecuteTaskCallback(ServerTask callingTask);
}