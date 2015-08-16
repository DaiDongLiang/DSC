package net.floodlightcontroller.core;

/**
 * This listener is called when floodlight is about to terminate to enable
 * interested parties to perform final steps, e.g., persisting debug
 * information.
 * Listeners are unordered.
 * floodflight终止时被调用
 * @author gregor
 *
 */
public interface IShutdownListener {
    public void floodlightIsShuttingDown();
}
