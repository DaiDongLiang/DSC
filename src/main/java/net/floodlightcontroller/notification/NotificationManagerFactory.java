package net.floodlightcontroller.notification;

import net.floodlightcontroller.notification.syslog.SyslogNotificationFactory;

/**
 * This factory is a public utility to get NotificationManager
 * instance.
 *	这个工厂是一个公开的工具来得到NotificationManager实例
 * @author kevinwang
 * @edited Ryan Izard, rizard@g.clemson.edu, ryan.izard@bigswich.com
 *
 */
public class NotificationManagerFactory {

    public static final String  NOTIFICATION_FACTORY_NAME =
            "floodlight.notification.factoryName";

    /**
     * Do not set the default here. Delay until init(), which will be
     * called by the JVM at class load. This will allow the unit tests
     * to test dynamic binding to a factory, then reset to the default
     * factory by clearing the System property and then calling init() 
     * again for subsequent unit tests that actually need a non-mocked 
     * NotificationManagerFactory.
     * 	不要在这里实例化，延迟到JVN加载类时调用init()方法
     * 	这将允许单元测试测试动态绑定到工厂，之后通过扫描系统属性来重置为默认工厂，
     * 	之后为后续单元测试再次调用init()，那需要一个不是模拟的NotificationManagerFactory
     * If a dynamic binding is not specified, init() will fall through
     * to else and the default of SyslogNotifcationFactory will be used.
     * 如果没有指定动态绑定，fall将会失败，默认的SyslogNotifcationFactory将会被使用
     */
    private static INotificationManagerFactory factory; 

    /**
     * Dynamically bind to a factory if there is one specified.
     * This provides a simple and very basic implementation to override
     * with a customized NotificationFactory.
     *	如果有一个指定的会动态绑定到工厂
     *	这里提供一个简单并且很基本的实现来覆盖自定义NotificationFactory
     */
    static {
        NotificationManagerFactory.init();
    }

    /**
     * A simple mechanism to initialize factory with dynamic binding.
     * 	一个简单的机制来初始化工厂通过动态绑定
     * Extended to default to SyslogNotifcationFactory in the event
     * a dynamic binding is not specified via System properties.
     * This allows init() to be called multiple times for the unit tests
     * and select the default or a another factory if the System property
     * is cleared or is set, respectively.
     * 	成为默认的SyslogNotifcationFactory如果没有通过系统属性动态绑定指定的工厂。
     * 这允许init()方法被单元测试重复调用并且选择默认工厂或其他工厂如果系统属性被扫描到
     */
    protected static void init() {
        String notificationfactoryClassName = null;
        try {
            notificationfactoryClassName =
                    System.getProperty(NOTIFICATION_FACTORY_NAME);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        if (notificationfactoryClassName != null) {
            Class<?> nfc;
            try {
                nfc = Class.forName(notificationfactoryClassName);
                factory = (INotificationManagerFactory) nfc.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
         } else {
        	 factory = new SyslogNotificationFactory(); // use as the default
         }
    }

    /**
     * Helper method to create a NotificationManager instance by class
     * with the underline factory
     * 辅助方法通过类来创造NotificationManager实例
     * @param clazz
     * @return
     */
    public static <T> INotificationManager getNotificationManager(Class<T> clazz) {
        return factory.getNotificationManager(clazz);
    }

    /**
     * Helper method to return the factory
     * 辅助方法来返回工厂
     * @return the INotificationManagerFactory instance
     */
    public static <T> INotificationManagerFactory getNotificationManagerFactory() {
        return factory;
    }

}
