package net.floodlightcontroller.debugevent;

/**
 * EventCategory is used to log events for pre-registered events.
 * EventCategory被用来记录事件对于每一个已注册的事件
 */
public interface IEventCategory<T> {

    /**
     * Logs the instance of the event thread-locally. Flushing to the global
     * circular buffer for this event is delayed resulting in better
     * performance. This method should typically be used by those events that
     * happen in the packet processing pipeline
     *	记录事件实例的本地线程。
     *	对于这个事件延迟刷新到全局循环buffer会导致更好的性能。
     * @param event
     *            an instance of the user-defined event of type T
     */
    public void newEventNoFlush(T event);

    /**
     * Logs the instance of the event thread-locally and immediately flushes to
     * the global circular buffer for this event. This method should typically
     * be used by those events that happen outside the packet processing
     * pipeline
     *
     * @param event
     *            an instance of the user-defined event of type T
     */
    public void newEventWithFlush(T event);

}
