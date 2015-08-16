package net.floodlightcontroller.notification;

/**
 * This factory interface produce INotificationManager instance.
 *	这个工厂接口提供了INotificationManager实例
 * @author kevin.wang@bigswitch.com
 *
 */
public interface INotificationManagerFactory {

    /**
     * Produce and returns a NotificationManager based on the name
     *	根据名字返回一个NotificationManager
     * @param clazz
     * @return NotificationManager instance
     */
    <T> INotificationManager getNotificationManager(Class<T> clazz);
}
