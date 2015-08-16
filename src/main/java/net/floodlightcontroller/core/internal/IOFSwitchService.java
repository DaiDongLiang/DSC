package net.floodlightcontroller.core.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchDriver;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.LogicalOFMessageCategory;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.rest.SwitchRepresentation;

import org.projectfloodlight.openflow.types.DatapathId;

public interface IOFSwitchService extends IFloodlightService {

    /**
     * Get's the switch map stored in the switch manager
     * 获得储存在交换机管理器里的交换机map
     * @return the map of switches known by the switch manager
     */
    Map<DatapathId, IOFSwitch> getAllSwitchMap();

    /**
     * If the switch with the given DPID is known to any controller in the
     * cluster, this method returns the associated IOFSwitch instance. As such
     * the returned switches not necessarily connected or in master role for
     * the local controller.
     *	如果给予的DPID在集群的任意一台控制器中已知，这个方法将会返回相应的IOFSwitch实例
     *	在这种情况下，返回的交换机没有必要已连接或者处于master角色。
     * Multiple calls to this method with the same DPID may return different
     * IOFSwitch references. A caller must not store or otherwise rely on
     * IOFSwitch references to be constant over the lifecycle of a switch.
     *	使用相同的DPID多次调用这个方法会可能返回不同的IOFSwitch引用。
     *	在整个交换机的生命周期中，调用者不得保存或者依赖IOFSwtich引用为常量。
     * @param dpid the dpid of the switch to query
     * @return the IOFSwitch instance associated with the dpid, null if no
     * switch with the dpid is known to the cluster
     */
    IOFSwitch getSwitch(DatapathId dpid);

    /**
     * If the switch with the given DPID is known to any controller in the
     * cluster, this method returns the associated IOFSwitch instance. As such
     * the returned switches not necessarily connected or in master role for
     * the local controller.
     *
     * Multiple calls to this method with the same DPID may return different
     * IOFSwitch references. A caller must not store or otherwise rely on
     * IOFSwitch references to be constant over the lifecycle of a switch.
     *
     * @param dpid the dpid of the switch to query
     * @return the IOFSwitch instance associated with the dpid, null if no
     * switch with the dpid is known to the cluster OR if the switch status
     * is not considered visible.
     */
    IOFSwitch getActiveSwitch(DatapathId dpid);

    /**
     * Add a switch listener
     * @param listener The module that wants to listen for events
     */
    void addOFSwitchListener(IOFSwitchListener listener);
    
    /**
     * Add a switch driver
     * 添加一个交换机驱动
     * @param manufacturerDescriptionPrefix
     * @param driver
     */
    void addOFSwitchDriver(String manufacturerDescriptionPrefix, IOFSwitchDriver driver);

    /**
     * Remove a switch listener
     * 移除一个交换机监听器
     * @param listener The The module that no longer wants to listen for events
     */
    void removeOFSwitchListener(IOFSwitchListener listener);

    /**
     * Registers a logical OFMessage category to be used by an application
     * 注册一个OFMessage逻辑分类来被一个应用使用
     * @param category the logical OFMessage category
     */
    void registerLogicalOFMessageCategory(LogicalOFMessageCategory category);

    /**
     * Registers an app handshake plugin to be used during switch handshaking.
     * 注册一个应用握手插件在交换机握手过程中被使用
     * @param plugin the app handshake plugin to be used during switch handshaking.
     */
    void registerHandshakePlugin(IAppHandshakePluginFactory plugin);

    /**
     * Get the REST representations of the active switches.
     * 获得活动交换机的REST表述
     * @return Representation wrappers of the active switches.
     */
    List<SwitchRepresentation> getSwitchRepresentations();

    /**
     * Get the REST representation of a switch.
     * 获得指定交换机的REST表述
     * @param dpid the dpid of the desired switch representation.
     * @return The switch representation.
     */
    SwitchRepresentation getSwitchRepresentation(DatapathId dpid);

    /**
     * Returns a snapshot of the set DPIDs for all known switches.
     *	返回所有DPID set集合的副本
     * The returned set is owned by the caller: the caller can modify it at
     * will and changes to the known switches are not reflected in the returned
     * set. The caller needs to call getAllSwitchDpids() if an updated
     * version is needed.
     *	返回的set集合属于调用者:调用者能修改或更换已知的交换机，不会影响到原始数据。
     *	如果需要新的数据，调用者需要调用getAllSwitchDpids()
     * See {@link #getSwitch(long)} for what  "known" switch is.
     * @return the set of DPIDs of all known switches
     */
    Set<DatapathId> getAllSwitchDpids();

    /**
     * Gets an immutable list of handshake handlers.
     * 获得一个处理握手的不变的列表
     * @return an immutable list of handshake handlers.
     */
    List<OFSwitchHandshakeHandler> getSwitchHandshakeHandlers();

}
