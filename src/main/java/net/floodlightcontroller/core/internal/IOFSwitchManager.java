package net.floodlightcontroller.core.internal;

import java.util.List;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFConnectionBackend;
import net.floodlightcontroller.core.IOFSwitch.SwitchStatus;
import net.floodlightcontroller.core.IOFSwitchBackend;
import net.floodlightcontroller.core.IOFSwitchDriver;
import net.floodlightcontroller.core.LogicalOFMessageCategory;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.SwitchDescription;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.types.DatapathId;

import com.google.common.collect.ImmutableList;

public interface IOFSwitchManager {

    /**
     * Called when a switch is added.
     * 在交换机添加是被调用
     * @param sw the added switch
     */
    void switchAdded(IOFSwitchBackend sw);

    /**
     * Called when a switch disconnects
     * 交换机断开连接时被调用
     * @param sw the removed switch
     */
    void switchDisconnected(IOFSwitchBackend sw);

    /**
     * Indicates that ports on the given switch have changed. Enqueue a
     * switch update.
     * 表明在指定交换机上端口发生改变
     * 交换机更新入队
     * @param sw the added switch
     */
    void notifyPortChanged(IOFSwitchBackend sw, OFPortDesc port,
                           PortChangeType type);

    /**
     * Relays to ISwitchDriverRegistry
     *	转发到ISwitchDriverRegistry
     * 
     */
    IOFSwitchBackend getOFSwitchInstance(IOFConnectionBackend connection,
                                         SwitchDescription description,
                                         OFFactory factory,
                                         DatapathId datapathId);

    /**
     * Relays an upstream message to the controller to dispatch to listeners.
     * 转发一个上行消息给控制器来调度监听器
     * @param sw The switch the message was received on.
     * @param m The message received.
     * @param bContext the Floodlight context of the message, normally null in this case.
     */
    void handleMessage(IOFSwitchBackend sw, OFMessage m, FloodlightContext bContext);

    /**
     * Gets an unmodifiable collection of OFSwitchHandshakeHandlers
     * 得到一个不可修改的OFSwitchHandshakeHandlers集合
     * @return an unmodifiable collection of OFSwitchHandshakeHandlers
     */
    ImmutableList<OFSwitchHandshakeHandler> getSwitchHandshakeHandlers();

    /**
     * Adds an OFSwitch driver
     * 添加一个OFSwitch驱动
     *  @param manufacturerDescriptionPrefix Register the given prefix
     * with the driver.
     * @param driver A IOFSwitchDriver instance to handle IOFSwitch instantiation
     * for the given manufacturer description prefix
     * @throws IllegalStateException If the the manufacturer description is
     * already registered
     * @throws NullPointerExeption if manufacturerDescriptionPrefix is null
     * @throws NullPointerExeption if driver is null
     */
    void addOFSwitchDriver(String manufacturerDescriptionPrefix,
                           IOFSwitchDriver driver);

    /**
     * Handles all changes to the switch status. Will alert listeners and manage
     * the internal switch map appropriately.
     * 处理所有交换机状态更改
     * 将会通知监听器并适当的处理内部交换机表
     * @param sw the switch that has changed
     * @param oldStatus the switch's old status
     * @param newStatus the switch's new status
     */
    void switchStatusChanged(IOFSwitchBackend sw, SwitchStatus oldStatus,
            SwitchStatus newStatus);

    /**
     * Gets the number of connections required by the application
     * 得到应用所需的连接数
     * @return the number of connections required by the applications
     */
    int getNumRequiredConnections();

    /**
     * Record a switch event in in-memory debug-event
     * 记录一个交换机事件在内存中的debug-event
     * @param switchDpid
     * @param reason Reason for this event
     * @param flushNow see debug-event flushing in IDebugEventService
     */
    public void addSwitchEvent(DatapathId switchDpid, String reason, boolean flushNow);

    /**
     * Get the list of handshake plugins necessary for the switch handshake.
     * 得到握手插件列表对于所需的交换机
     * @return the list of handshake plugins registered by applications.
     */
    List<IAppHandshakePluginFactory> getHandshakePlugins();

    /**
     * Get the switch manager's counters
     * 得到交换机管理器的计数器
     * @return the switch manager's counters
     */
    SwitchManagerCounters getCounters();

    /**
     * Checks to see if the supplied category has been registered with the controller
     * 检查提供的类别是否已经在控制器中注册
     * @param category the logical OF Message category to check or
     * @return true if registered
     */
    boolean isCategoryRegistered(LogicalOFMessageCategory category);

    void handshakeDisconnected(DatapathId dpid);

}
