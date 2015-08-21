/**
 *    Copyright 2013, Big Switch Networks, Inc.
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

package net.dsc.hazelcast.listener;

import java.util.Map;

import net.dsc.cluster.HAListenerTypeMarker;
import net.dsc.cluster.HARole;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IListener;


/** Listener interface for the {@link HARole} of the local controller. Listeners
 *  are notified when the controller transitions to role {@link HARole#ACTIVE}.
 *  拥有集群身份的控制器的监听器接口，当控制器身份发生变化时监听器会得到通知。
 *  <p>
 *  <strong>NOTE:</strong> The floodlight platform currently does not support
 *  a transition to the STANDBY role.
 *  备注：当前floodlight平台没有提供到备用身份的过渡过程。
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public interface IHAListener extends IListener<HAListenerTypeMarker> {
    /**
     * This notification is fired if the controller's initial role was STANDBY
     * and the controller is now transitioning to ACTIVE.
     * Clients can query the current (and initial) role from
     * {@link IFloodlightProviderService#getRole()} (in startup).
     * 如果控制器的初始角色是STANDBY并且现在转变为ACTIVE时它将被触发
     * 客户端可以查询当前(和最初)身份从IFloodlightProviderService对象的getRole()方法(在启动时)
     */
    public void transitionToActive();

    /**
     * This notification is fired if the controller's initial role was ACTIVE
     * and the controller is now transitioning to STANDBY.
     * 如果控制器的初始角色是ACTIVE并且现在转变为STANDBY时它将被触发
     * <strong>NOTE:</strong> The floodlight platform currently terminates
     * after the transition to STANDBY. Clients should prepare for the shutdown
     * in transitionToStandby (e.g., ensure that current updates to operational
     * states are fully synced).
     * 备注：当前floodlight平台会在转变为STANDBY身份后停止，客户端应该准备关闭在抓换过程中。
     * 例如，确保当前更新状态完全同步
     */
    public void transitionToStandby();
    
    /**
     * Gets called when the IP addresses of the controller nodes in the controller cluster
     * change. All parameters map controller ID to the controller's IP.
     * 当控制器节点IP在集群中发生变化时获得通知。map参数key=控制器ID，value=控制器IP
     * @param curControllerNodeIPs当前控制器IP
     * @param addedControllerNodeIPs添加的控制器IP
     * @param removedControllerNodeIPs移除的控制器IP
     */
    public void controllerNodeIPsChanged(Map<String, String> curControllerNodeIPs,
    									Map<String, String> addedControllerNodeIPs,
    									Map<String, String> removedControllerNodeIPs);
}
