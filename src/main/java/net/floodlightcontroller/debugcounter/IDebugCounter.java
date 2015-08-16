package net.floodlightcontroller.debugcounter;

/**
 * A concurrent counter.
 * 一个并发计数器
 * The counter returned when registering a counter. The counter value
 * is a positive long. The counter cannot be decremented, but it can be
 * reset to 0. The counter does not protect against overflow. If the
 * value exceeds MAX_LONG it will silently overflow to MIN_LONG
 * 注册后一个计数器被返回。
 * 计数器值是long值。
 * 计数器不能削减，但能被重置为0.
 * 计数器不能保护溢出
 * 如果值超出MAX_LONG将会溢出到MIN_LONG
 * @author gregor
 *
 */
public interface IDebugCounter {
    /**
     * Increment this counter by 1.
     * 增加1
     */
    void increment();

    /**
     * Add the given increment to this counter
     * 增加指定的数量
     * @param incr
     */
    void add(long incr);

    /**
     * Retrieve the value of the counter.
     *	得到计数器的值
     */
    long getCounterValue();
    
    /**
     * Retrieve the last-modified date of the counter.
     */
    long getLastModified();

    /**
     * Reset the value of the counter to 0.
     */
	void reset();
}
