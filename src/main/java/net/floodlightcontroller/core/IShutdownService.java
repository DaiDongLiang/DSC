package net.floodlightcontroller.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IShutdownService extends IFloodlightService {
    /**
     * Terminate floodlight process by calling System.exit() with the given
     * exitCode. If reason is non-null, reason will be logged. Before
     * terminating the shutdownListeners are called
     * 通过调用System.exit()并传入一个exitCode来终止floodlight线程。
     * 如果原因不为null，原因将会被记录。
     * 在终止前shutdownListeners将会被调用
     * If exitCode == 0 the termination cause is deemed "normal" and info
     * level log is used. In all other cases the exit is abnormal and an error
     * is logged.
     * 如果exitCode==0终止的原因是正常
     * 在所有其他反常的退出原因中错误将会被记录
     * @param reason 终止原因
     * @param exitCode 退出码
     */
    public void terminate(@Nullable String reason, int exitCode);

    /**
     * Terminate floodlight process by calling System.exit() with the given
     * exitCode. If reason is non-null, reason will be logged. In addition,
     * the throwable will be logged as well.
     * Before terminating the shutdownListeners are called
     *
     * This method is generally used to terminate floodlight due to an
     * unhandled Exception. As such all messages are logged as error and it is
     * recommended that an exitCode != 0 is used.
     * 这个方法通常用用来终止floodflight由于一个不能处理的异常。
     * 因此所有的信息被记录为错误并且推荐使用exitCode不为0
     * @param reason
     * @param e The throwable causing floodlight to terminate
     * @param exitCode
     */
    public void terminate(@Nullable String reason,
                          @Nonnull Throwable e, int exitCode);

    /**
     * Register a shutdown listener. Registered listeners are called when
     * floodlight is about to terminate due to a call to terminate()
     * 注册一个监听器。
     * @param listener
     */
    public void registerShutdownListener(@Nonnull IShutdownListener listener);
}
