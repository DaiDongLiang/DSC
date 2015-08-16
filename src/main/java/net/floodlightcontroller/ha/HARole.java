package net.floodlightcontroller.ha;

import net.floodlightcontroller.core.IOFSwitchListener;

import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HARole describes the role that a given controller node currently plays in the
 * management of the SDN network. Controller nodes can be either
 * HARole描述当前控制器节点在管理的SDN网络中的角色
 * <ul>
 * <li>ACTIVE - currently controlling the network 当前正在控制网络
 * <li>STANDBY - standing by in case of a fail-over from the ACTIVE node备用当ACTIVE节点出现故障
 * </ul>
 * At any given time there SHOULD be at most one ACTIVE node in the network
 * 在任何时候至少应该有一个控制节点
 * (this invariant cannot be strictly guranteed for certain split-brain
 * situtations). There can be multiple STANDBY controllers. There are other
 * HA-related roles in the system. Try to not confuse them.
 * (这个静态变量不能严格的保证某个分裂位置)
 * 可以有多个STANDBY角色控制器
 * 在系统中可以有其他的集群关系角色
 * 尽量不要混淆他么
 * <ul>
 * <li>On the cluster management/bigsync layer {@link ISyncService} determines a
 * DOMAIN LEADER / DOMAIN FOLLOWER (which are exposed via
 * 在集群管理层确定一个区域领导/区域跟随者
 * <li>On the OF layer, switch connections can be in either MASTER, SLAVE or
 * EQUAL {@link Role} (exposed by {@link IOFSwitchListener}).
 * 在OF层，交换机也能有MASTER，SLAVE，EQUAL身份
 * </ul>
 * Most applications and modules trying to decide something on the ACTIVE node
 * should base that decision on the HARole.
 *大多数应用和模块试图确定一些事在ACTIVE节点应该基于集群角色
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public enum HARole{
    /** This controller node is currently actively managing the SDN network. At any given
     *  time, there should be at most one ACTIVE node. When the ACTIVE node fails, a STANDBY
     *  node is determined to become ACTIVE.
     */
    ACTIVE(OFControllerRole.ROLE_MASTER),

    /** This controller node is currently standing by and not managing the networking. There
     *  may be more than one STANDBY nodes in the network.
     */
    STANDBY(OFControllerRole.ROLE_SLAVE);

    private static final Logger logger = LoggerFactory.getLogger(HARole.class);
    private final OFControllerRole ofRole;

    HARole(OFControllerRole ofRole) {
        this.ofRole = ofRole;
    }

    /** a backwards-compatible {@link #valueOf} that accepts the old terms "MASTER" and "SLAVE"
     *  and normalizes them to ACTIVE and STANDBY.
     *  一个先后兼容的valueOf,将MASTER和SLAVE规范化为ACTIVE和STANDBY
     *
     * @param roleString
     * @return an HARole
     * @throws IllegalArgumentException - if no such role can be found.
     */
    public static HARole valueOfBackwardsCompatible(String roleString) throws IllegalArgumentException {
        roleString = roleString.trim().toUpperCase();
        if("MASTER".equals(roleString)) {
            logger.warn("got legacy role name MASTER - normalized to ACTIVE", roleString);
            if(logger.isDebugEnabled()) {
               logger.debug("Legacy role call stack", new IllegalArgumentException());
            }
            roleString = "ACTIVE";
        } else if ("SLAVE".equals(roleString)) {
            logger.warn("got legacy role name SLAVE - normalized to STANDBY", roleString);
            if(logger.isDebugEnabled()) {
               logger.debug("Legacy role call stack", new IllegalArgumentException());
            }
            roleString = "STANDBY";
        }
        return valueOf(roleString);
    }

    public OFControllerRole getOFRole() {
        return ofRole;
    }

    public static HARole ofOFRole(OFControllerRole role) {
        switch(role) {
            case ROLE_MASTER:
            case ROLE_EQUAL:
                return ACTIVE;
            case ROLE_SLAVE:
                return STANDBY;
            default:
                throw new IllegalArgumentException("Unmappable controller role: " + role);
        }
    }
}
