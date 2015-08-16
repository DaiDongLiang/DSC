package net.floodlightcontroller.core.internal;

/**
 * This interface creates a contract used by the switch handshake handler. Each
 * switch that is connected needs it's own running instance of the registered
 * plugins. Thus is depends on a factory to churn out these instances.
 * 这个接口创建一个契约在处理交换机握手时使用。
 * 每一个交换机被连接需要他们自己的已注册插件的运行实例。
 * 因此依赖于一个工厂大量生产这些实例
 * @author Jason Parraga <Jason.Parraga@bigswitch.com>
 *	
 */
public interface IAppHandshakePluginFactory {

    /**
     * Create an instance of OFSwitchAppHandshakePlugin
     * 创造一个插件
     * @return an instance of OFSwitchAppHandshakePlugin
     */
    OFSwitchAppHandshakePlugin createPlugin();
}


