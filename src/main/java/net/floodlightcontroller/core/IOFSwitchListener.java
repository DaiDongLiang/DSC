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

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.types.DatapathId;

/**
 * Switch lifecycle notifications.
 *	交换机生命周期通知
 * These updates /happen-after/ the corresponding changes have been
 * committed. I.e., the changes are visible when
 * {@link IFloodlightProviderService#getSwitch(long)}
 * {@link IFloodlightProviderService#getAllSwitchDpids()}
 * {@link IFloodlightProviderService#getAllSwitchMap()}
 * or any method on the IOFSwitch returned by these methods are
 * called from the notification method or after it.
 *	这些更新应该发生在相应的改变之后。
 *	这些改变对于以上方法是可见的，或者任何IOFSwitch中被通知方法调用的方法
 * Note however, that additional changes could have been committed before
 * the notification for which the notification is still pending. E.g.,
 * {@link IFloodlightProviderService#getSwitch(long)} might return null after
 * a switchAdded() (which happens if the switch has been added and then
 * removed and the remove hasn't been dispatched yet).
 *	备注：无论怎样,这额外的更改应该在等待被通知之前被提交
 *	例：FloodlightProviderService#getSwitch(long)可能返回null在调用了switchAdded之后。
 *	(这可能是交换机添加之后又删除了，并且删除的消息没有发出)
 * These lifecycle notification methods are called by a single thread and they
 * will always be called by the same thread.
 * The calls are always in order.
 *	这些什么周期通知方法在一个单线程中被调用，并且他们总是被同一个线程调用
 */
public interface IOFSwitchListener {
    /**
     * Fired when switch becomes known to the controller cluster. I.e.,
     * the switch is connected at some controller in the cluster
     * 发送交换机连接上已知的控制器集群
     * @param switchId the datapath Id of the new switch
     */
    public void switchAdded(DatapathId switchId);

    /**
     * Fired when a switch disconnects from the cluster ,
     * 发送交换机从集群断开
     * @param switchId the datapath Id of the switch
     */
    public void switchRemoved(DatapathId switchId);

    /**
     * Fired when a switch becomes active *on the local controller*, I.e.,
     * the switch is connected to the local controller and is in MASTER mode
     * 当交换机在当前控制器上成为活动状态触发
     * @param switchId the datapath Id of the switch
     */
    public void switchActivated(DatapathId switchId);

    /**
     * Fired when a port on a known switch changes.
     *当一个已知的交换机端口状态改变时触发
     * A user of this notification needs to take care if the port and type
     * information is used directly and if the collection of ports has been
     * queried as well. This notification will only be dispatched after the
     * the port changes have been committed to the IOFSwitch instance. However,
     * if a user has previously called {@link IOFSwitch#getPorts()} or related
     * method a subsequent update might already be present in the information
     * returned by getPorts.
     * 使用者对于这个通知需要关心是否端口和类型信息立即被使用和是否端口集合已经被查询
     * 这个信息只会在端口改变已提交到IOFSwitch实例后发送。
     * 如果使用者之前调用了IOFSwitch.getPorts()方法,或者相关的方法,后续更新可能已经出现在getPorts的信息中。
     * @param switchId
     * @param port
     * @param type
     */
    public void switchPortChanged(DatapathId switchId,
                                  OFPortDesc port,
                                  PortChangeType type);

    /**
     * Fired when any non-port related information (e.g., attributes,
     * features) change after a switchAdded
     * 在添加交换机后当任何与端口无关的信息改变时触发.
     * TODO: currently unused
     * @param switchId
     */
    public void switchChanged(DatapathId switchId);
}
