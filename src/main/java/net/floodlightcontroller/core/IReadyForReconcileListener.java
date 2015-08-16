package net.floodlightcontroller.core;

/**
 * This listener is a temporary solution to start flow reconciliation
 * after a Slave -> Master transition. It is fired once all known switches are
 * active (either because they all reconnected or because they did not
 * reconnect and have timed out).
 *	这个监听器是一个临时的方案在SLave-Master转换中发送消息。
 *	一旦所有的交换机活动将会被触发
 *	或者因为他们全部已经重新连接或他们不重新连接并且连接超时
 * Modules should generally not rely on this notification unless there are
 * strong and compelling reasons to do so. I general modules should handle
 * the fact that some known switches are not active!
 * 模块通常应该不依赖于这个通知除非有令人信服的原因。
 * 我认为模块应该处理一些已知交换机失效的现实。
 * @author gregor
 *
 */
public interface IReadyForReconcileListener {
    public void readyForReconcile();
}
