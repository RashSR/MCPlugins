package events;
import utils.ServerEventType;

public interface IEvent {
    public void startEvent();
    public void endEvent();

    public ServerEventType getEventType();
}