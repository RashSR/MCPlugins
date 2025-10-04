package events;

public interface IEvent {
    public void startEvent();
    public void endEvent();

    public EventEnum getEventType();
}