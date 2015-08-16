/**
 *    Copyright 2011, Big Switch Networks, Inc.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.core.internal.Controller.IUpdate;
import net.floodlightcontroller.core.internal.Controller.ModuleLoaderState;
import net.floodlightcontroller.core.internal.RoleManager;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.ha.ControllerModel;
import net.floodlightcontroller.ha.HARole;
import net.floodlightcontroller.ha.IHAListener;
import net.floodlightcontroller.ha.RoleInfo;
import net.floodlightcontroller.packet.Ethernet;

import org.jboss.netty.util.Timer;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
/**
 * The interface exposed by the core bundle that allows you to interact
 * with connected switches.
 *由核心包暴露的接口，它允许你与已连接的交换机进行交互。
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public interface IFloodlightProviderService extends
        IFloodlightService, Runnable {

    /**
     * A value stored in the floodlight context containing a parsed packet
     * representation of the payload of a packet-in message.
     * 存储在floodlight上下文中的一个数值，包含一个对PACKET_IN消息携带的数据解析后的表示。
     */
    public static final String CONTEXT_PI_PAYLOAD =
            "net.floodlightcontroller.core.IFloodlightProvider.piPayload";

    /**
     * A FloodlightContextStore object that can be used to retrieve the
     * packet-in payload
     * 一个FloodlightContextStore对象，该对象可以被用于获取PACKET_IN消息携带的数据。
     */
    public static final FloodlightContextStore<Ethernet> bcStore =
            new FloodlightContextStore<Ethernet>();

    /**
     * Service name used in the service directory representing
     * the OpenFlow controller-switch channel
     *在service目录中的Service名,代表OpenFlow控制器和交换机之间的通道
     * @see  ILocalServiceAddressTracker
     * @see  IClusterServiceAddressDirectory
     */
    public static final String SERVICE_DIRECTORY_SERVICE_NAME = "openflow";

    /**
     * Adds an OpenFlow message listener
     * 添加OpenFlow消息监听器
     * @param type The OFType the component wants to listen for 想监听的openflow消息类型
     * @param listener The component that wants to listen for the message 需要的IOFMessageListener监听器
     */
    public void addOFMessageListener(OFType type, IOFMessageListener listener);

    /**
     * Removes an OpenFlow message listener
     * 移除OpenFlow消息监听器
     * @param type The OFType the component no long wants to listen for
     * @param listener The component that no longer wants to receive the message
     */
    public void removeOFMessageListener(OFType type, IOFMessageListener listener);

    /**
     * Return a non-modifiable list of all current listeners
     * 返回一个当前所有listener的不可修改的列表。
     * @return listeners
     */
    public Map<OFType, List<IOFMessageListener>> getListeners();

    /**
     * Get the current role of the controller
     * 获取当前控制器的角色
     */
    public HARole getRole();

    /**
     * Get the current role of the controller
     * 获取当前控制器的角色信息
     */
    public RoleInfo getRoleInfo();

    /**
     * Get the current mapping of controller IDs to their IP addresses
     * Returns a copy of the current mapping.
     * 获取控制器当前ID到其IP地址的映射，返回一个当前映射的副本。
     * @see IHAListener
     */
    public Map<String,String> getControllerNodeIPs();

    /**
     * Gets the ID of the controller
     * 获得当前控制器ID
     */
    public ControllerModel getControllerModel();

    /**
     * Gets the controller hostname
     * 获得控制器主机名称
     * @return the controller hostname
     */
    public String getOFHostname();

    /**
     * Gets the controller's openflow port
     * 获得控制器的openflow端口
     * @return the controller's openflow port
     */
    public int getOFPort();

    /**
     * Set the role of the controller
     * 设置控制器角色信息
     * @param role The new role for the controller node控制器的新角色
     * @param changeDescription The reason or other information for this role change 进行角色变化的原因或其他信息
     */
    public void setRole(HARole role, String changeDescription);

    /**
     * Add an update task for asynchronous, serialized execution
     * 添加一个异步更新任务，序列化执行
     *
     * @param update
     */
    public void addUpdateToQueue(IUpdate update);

    /**
     * Adds a listener for HA role events
     * 添加一个监听集群事件的监听器。
     * @param listener The module that wants to listen for events
     */
    public void addHAListener(IHAListener listener);

    /**
     * Removes a listener for HA role events
     * 移除一个监听集群事件的监听器。
     * @param listener The module that no longer wants to listen for events
     */
    public void removeHAListener(IHAListener listener);

    /**
     * Process written messages through the message listeners for the controller
     * 进程产生的消息，来源于控制器的消息监听器。
     * @param sw The switch being written to 消息要送达的目的交换机 
     * @param m the message 消息
     * @throws NullPointerException if switch or msg is null 任何所附的上下文对象。可以为null，此时一个新的上下文对象将被分配并传递给监听器。
     */
    public void handleOutgoingMessage(IOFSwitch sw, OFMessage m);

    /**
     * Run the main I/O loop of the Controller.
     * 运行控制器的主要的I/O循环。
     */
    @Override
    public void run();

    /**
     * Add an info provider of a particular type
     * 添加特定类型的信息提供者。
     * @param type
     * @param provider
     */
    public void addInfoProvider(String type, IInfoProvider provider);

   /**
    * Remove an info provider of a particular type
    * 移除特定类型的信息提供者。
    * @param type
    * @param provider
    */
   public void removeInfoProvider(String type, IInfoProvider provider);

   /**
    * Return information of a particular type (for rest services)
    * 返回一个特定类型的信息（REST服务）
    * @param type
    * @return
    */
   public Map<String, Object> getControllerInfo(String type);

   /**
    * Return the controller start time in  milliseconds
    * 返回以毫秒为单位的控制器启动时间
    * @return
    */
   public long getSystemStartTime();

   /**
    * Get controller memory information
    * 获取控制器的内存信息。
    */
   public Map<String, Long> getMemory();

   /**
    * returns the uptime of this controller.
    * 返回此控制器的正常运行时间。
    * @return
    */
   public Long getUptime();

   /**
    * Get the set of port prefixes that will define an UPLINK port.
    * 获取一个端口的前缀的集合，这将定义一个上行端口
    * @return The set of prefixes端口前缀的集合
    */
   public Set<String> getUplinkPortPrefixSet();


   public void handleMessage(IOFSwitch sw, OFMessage m,
                          FloodlightContext bContext);

   /**
    * Gets a hash wheeled timer to be used for for timeout scheduling
    * 获得一个用来执行超时任务的计时器。
    * @return a hash wheeled timer
    */
   public Timer getTimer();

   /**
    * Gets the role manager
    * 获得角色管理器
    * @return the role manager
    */
   public RoleManager getRoleManager();

   /**
    * Gets the current module loading state.
    * 获得当前模块的载入状态
    * @return the current module loading state.
    */
   ModuleLoaderState getModuleLoaderState();

   /**
    * Gets the current number of worker threads
    * 获得当前工作线程数值
    * @return Used for netty setup
    */
   public int getWorkerThreads();

}

