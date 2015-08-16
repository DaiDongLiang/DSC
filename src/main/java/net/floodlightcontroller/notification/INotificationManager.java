package net.floodlightcontroller.notification;

/**
 * Base interface for managing notifications.
 *	管理通知的基本接口
 * Notification is used to alert or inform notification receiver.
 * Notification can be a message written into log file or an SNMP trap or
 * SNMP notification.
 *	通知是用来提醒或报告给通知信息接受者
 *	通知可以是一个消息写入日志文件，或者一个SNMP消息或SNMP通知
 * @author kevin.wang@bigswitch.com
 *
 */
public interface INotificationManager {

    /**
     * Post a notification. Depending on the underline implementation, it
     * may write the notes to log file or send an SNMP notification/trap.
     *	发送一个通知
     *	依赖于强制执行，它可能写入一个消息到日志文件或发送SNMP通知
     * @param notes   string message to be sent to receiver
     */
    public void postNotification(String notes);

}
