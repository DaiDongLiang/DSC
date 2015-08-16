/**
*    Originally created by David Erickson, Stanford University
*
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package net.floodlightcontroller.core;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import org.projectfloodlight.openflow.protocol.OFActionType;
import org.projectfloodlight.openflow.protocol.OFCapabilities;
import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFRequest;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import net.floodlightcontroller.core.web.serializers.IOFSwitchSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * An openflow switch connecting to the controller.  This interface offers
 * methods for interacting with switches using OpenFlow, and retrieving
 * information about the switches.
 * 一个openflow交换机连接控制器。这个接口提供方法通过OpenFlow和交换机进行交互。
 * 并且检索关于交换机的信息
 */
@JsonSerialize(using=IOFSwitchSerializer.class)//Json序列化
public interface IOFSwitch extends IOFMessageWriter {
    // Attribute keys
    // These match our YANG schema, make sure they are in sync
    public static final String SWITCH_DESCRIPTION_FUTURE = "description-future";
    public static final String SWITCH_DESCRIPTION_DATA = "description-data";
    public static final String SWITCH_SUPPORTS_NX_ROLE = "supports-nx-role";
    public static final String PROP_FASTWILDCARDS = "fast-wildcards";
    public static final String PROP_REQUIRES_L3_MATCH = "requires-l3-match";
    public static final String PROP_SUPPORTS_OFPP_TABLE = "supports-ofpp-table";
    public static final String PROP_SUPPORTS_OFPP_FLOOD = "supports-ofpp-flood";
    public static final String PROP_SUPPORTS_NETMASK_TBL = "supports-netmask-table";
    public static final String PROP_SUPPORTS_BSN_SET_TUNNEL_DST_ACTION =
            "supports-set-tunnel-dst-action";
    public static final String PROP_SUPPORTS_NX_TTL_DECREMENT = "supports-nx-ttl-decrement";

    public enum SwitchStatus {
       /** this switch is in the process of being handshaked. Initial State. 
        * 这个交换机在握手进程中初始状态
       */
    	HANDSHAKE(false),
       /** the OF channel to this switch is currently in SLAVE role - the switch will not accept
        *  state-mutating messages from this controller node.
        *  OF链路连接的控制器当前是SLAVE角色，交换机将不承认此控制器修改的信息
        */
       SLAVE(true),
       /** the OF channel to this switch is currently in MASTER (or EQUALS) role - the switch is
        *  controllable from this controller node.
        *  OF链路连接的交换机当前是MASTER或者EQUALS角色。控制器能够控制交换机
        */
       MASTER(true),
       /** the switch has been sorted out and quarantined by the handshake. It does not show up
        *  in normal switch listings
        *  这个交换机通过握手被挑选和隔离。它不能呈现在正常的交换机表
        */
       QUARANTINED(false),
       /** the switch has disconnected, and will soon be removed from the switch database 
        * 这个交换机已断开连接，并且不就将会从交换机表中移除
        * */
       DISCONNECTED(false);

       private final boolean visible;

       SwitchStatus(boolean visible) {
        this.visible = visible;
       }

       /** wether this switch is currently visible for normal operation 
        * 对于正常运行当前交换机是否可见
        * */
       public boolean isVisible() {
            return visible;
       }

       /** wether this switch is currently ready to be controlled by this controller
        * 当前交换机是否已经准备好被控制器控制
        *  */
       public boolean isControllable() {
            return this == MASTER;
       }
    }
    /**
     * 得到交换机状态
     * @return
     */
    SwitchStatus getStatus();

    /**
     * Returns switch features from features Reply
     * 在特征回复中回复交换机特征
     * @return
     */
    long getBuffers();

    /**
     * Disconnect all the switch's channels and mark the switch as disconnected
     * 断开所有交换机连接,并标记交换机为disconnected
     */
    void disconnect();
    /**
     * 得到Actions集合
     * @return 
     */
    Set<OFActionType> getActions();
    /**
     * 得到Capabilities集合
     * @return
     */
    Set<OFCapabilities> getCapabilities();
    /**
     * 得到流表
     * @return
     */
    short getTables();

    /**
     * 获取一个对这台交换机描述性统计的副本 
     * @return a copy of the description statistics for this switch
     */
    SwitchDescription getSwitchDescription();

    /**
     * Get the IP address of the remote (switch) end of the connection
     * 得到交换机IP
     * @return the inet address
     */
    SocketAddress getInetAddress();

    /**
     * Get list of all enabled ports. This will typically be different from
     * the list of ports in the OFFeaturesReply, since that one is a static
     * snapshot of the ports at the time the switch connected to the controller
     * whereas this port list also reflects the port status messages that have
     * been received.
     * 得到全部使能端口列表。
     * 这通常与OFFeaturesReply回复中的端口列表不同，因为这是此刻交换机连接控制器的端口快照。
     * 然而这个端口列表也能反映出端口状态信息已经收到
     * 
     * @return Unmodifiable list of ports not backed by the underlying collection
     * 不可修改的列表
     */
    Collection<OFPortDesc> getEnabledPorts();

    /**
     * Get list of the port numbers of all enabled ports. This will typically
     * be different from the list of ports in the OFFeaturesReply, since that
     * one is a static snapshot of the ports at the time the switch connected
     * to the controller whereas this port list also reflects the port status
     * messages that have been received.
     * 获得所有活动端口的端口号。
     * @return Unmodifiable list of ports not backed by the underlying collection
     * 不可修改的列表
     */
    Collection<OFPort> getEnabledPortNumbers();

    /**
     * Retrieve the port object by the port number. The port object
     * is the one that reflects the port status updates that have been
     * received, not the one from the features reply.
     * 通过端口号获取端口对象.
     * 这个端口对象反映端口状态更新已被接收，不来自features回复
     * @param portNumber 端口号
     * @return port object 端口对象
     */
    OFPortDesc getPort(OFPort portNumber);

    /**
     * Retrieve the port object by the port name. The port object
     * is the one that reflects the port status updates that have been
     * received, not the one from the features reply.
     * Port names are case insentive
     * 通过端口名获取端口对象.
     * @param portName
     * @return port object
     */
    OFPortDesc getPort(String portName);

    /**
     * Get list of all ports. This will typically be different from
     * the list of ports in the OFFeaturesReply, since that one is a static
     * snapshot of the ports at the time the switch connected to the controller
     * whereas this port list also reflects the port status messages that have
     * been received.
     * 获得所有端口列表
     * @return Unmodifiable list of ports
     */
    Collection<OFPortDesc> getPorts();

    /**
     * This is mainly for the benefit of the DB code which currently has the
     * requirement that list elements be sorted by key. Hopefully soon the
     * DB will handle the sorting automatically or not require it at all, in
     * which case we could get rid of this method.
     * 这主要是为了当前数据库代码要求列表元素根据key排序。
     * 希望很快数据库能自动排序或不再需要排序，这种情况下我们可以拜托这个方法。
     * @return 已排序的端口列表
     */
    Collection<OFPortDesc> getSortedPorts();

    /**
     * 端口是否处在活动状态
     * 该端口是否被启用。（configured down、link down和生成树端口阻塞的情况不在此列） 
     * @param portNumber
     * @return Whether a port is enabled per latest port status message
     * (not configured down nor link down nor in spanning tree blocking state)
     */
    boolean portEnabled(OFPort portNumber);

    /**
     * @param portNumber
     * @return Whether a port is enabled per latest port status message
     * (not configured down nor link down nor in spanning tree blocking state)
     */
    boolean portEnabled(String portName);

    /**
     * Check is switch is connected
     * 检查是否连接
     * @return Whether or not this switch is connected
     */
    boolean isConnected();

    /**
     * Retrieves the date the switch connected to this controller
     * 获取此交换机连接到控制器的时间
     * @return the date
     */
    Date getConnectedSince();

    /**
     * Get the datapathId of the switch
     * 获取此交换机的DPID
     * @return
     */
    DatapathId getId();

    /**
     * Retrieves attributes of this switch
     * 获取此交换机的属性
     * @return
     */
    Map<Object, Object> getAttributes();

    /**
     * 检查该交换机是否处于活动状态。
     * 交换机连接这个控制器并且处于master角色
     * Check if the switch is active. I.e., the switch is connected to this
     * controller and is in master role
     * @return
     */
    boolean isActive();

    /**
     * Get the current role of the controller for the switch
     * 获得当前控制器对于交换机的角色
     * @return the role of the controller
     */
    OFControllerRole getControllerRole();

    /**
     * Checks if a specific switch property exists for this switch
     * 检查一个特定的属性是否存在于这个交换机
     * @param name name of property
     * @return value for name
     */
    boolean hasAttribute(String name);

    /**
     * Set properties for switch specific behavior
     * 获取交换机特定行为的属性
     * @param name name of property
     * @return value for name
     */
    Object getAttribute(String name);

    /**
     * Check if the given attribute is present and if so whether it is equal
     * to "other"
     * 检查给定的属性是否存在，如果是，该属性是否为“other”
     * @param name the name of the attribute to check
     * @param other the object to compare the attribute against.
     * @return true if the specified attribute is set and equals() the given
     * other object.
     */
    boolean attributeEquals(String name, Object other);

    /**
     * Set properties for switch specific behavior
     * 设置交换机特定行为的属性
     * @param name name of property
     * @param value value for name
     */
    void setAttribute(String name, Object value);

    /**
     * Set properties for switch specific behavior
     * 移除交换机特定行为的属性
     * @param name name of property
     * @return current value for name or null (if not present)
     */
    Object removeAttribute(String name);

    /**
     * Returns a factory object that can be used to create OpenFlow messages.
     * 返回一个能够创造OpenFlow消息的工厂对象
     * @return
     */
    OFFactory getOFFactory();

    /**
     * Flush all flows queued for this switch on all connections that were written by the current thread.
     * 通过当前线程刷新所有连接在此交换机上流队列
     *
     */
    void flush();

    /**
     * Gets the OF connections for this switch instance
     * 获得此交换机实例的OF连接
     * @return Collection of IOFConnection
     */
    ImmutableList<IOFConnection> getConnections();

    /**
     * Writes a message to the connection specified by the logical OFMessage category
     * 写入一个正确的OFMessage到指定的连接
     * @param m an OF Message
     * @param category the category of the OF Message to be sent
     */
    void write(OFMessage m, LogicalOFMessageCategory category);

    /**
     * Writes a message list to the connection specified by the logical OFMessage category
     * 写入一个正确的OFMessage列表到指定的连接
     * @param msglist an OF Message list
     * @param category the category of the OF Message list to be sent
     */
    void write(Iterable<OFMessage> msglist, LogicalOFMessageCategory category);

    /**
     * Get a connection specified by the logical OFMessage category
     * 通过获得一个指定的连接
     * @param category the category for the connection the user desires
     * @return an OF Connection
     */
    OFConnection getConnectionByCategory(LogicalOFMessageCategory category);

    /** write a Stats (Multipart-) request, register for all corresponding reply messages.
     * 写入一个状态请求，登记所有相应的回复信息
     * Returns a Future object that can be used to retrieve the List of asynchronous
     * OFStatsReply messages when it is available.
     * 返回一个Future对象能被用来获得异步OFStatsReply回复报文列表当它可用
     *
     * @param request stats request
     * @param category the category for the connection that this request should be written to
     * @return Future object wrapping OFStatsReply
     *         If the connection is not currently connected, will
     *         return a Future that immediately fails with a @link{SwitchDisconnectedException}.
     */
    <REPLY extends OFStatsReply> ListenableFuture<List<REPLY>> writeStatsRequest(OFStatsRequest<REPLY> request, LogicalOFMessageCategory category);

    /** write an OpenFlow Request message, register for a single corresponding reply message
     *  or error message.
     *	写入一个OpenFlow请求信息，登记单个相应的回复报文
     * @param request
     * @param category the category for the connection that this request should be written to
     * @return a Future object that can be used to retrieve the asynchrounous
     *         response when available.
     *
     *         If the connection is not currently connected, will
     *         return a Future that immediately fails with a @link{SwitchDisconnectedException}.
     */
    <R extends OFMessage> ListenableFuture<R> writeRequest(OFRequest<R> request, LogicalOFMessageCategory category);
}
