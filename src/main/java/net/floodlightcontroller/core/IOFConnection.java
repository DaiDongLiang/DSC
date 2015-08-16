package net.floodlightcontroller.core;

import java.net.SocketAddress;

import java.util.Date;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFAuxId;


/** Contract for an openflow connection to a switch.
 * openflow连接交换机的约定
 *  Provides message write and request/response handling capabilities.
 *  提供写入报文和请求/回复处理能力
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public interface IOFConnection extends IOFMessageWriter {

    /**
     * Retrieves the date the connection connected to this controller
     * 获得连接控制器的时间
     * @return the date
     */
    Date getConnectedSince();

    /**
     * Flush all flows queued for this switch in the current thread.
     * 在当前线程刷刷新交换机所有的流队列
     * NOTE: The contract is limited to the current thread
     * 备注:仅限于当前线程
     */
    void flush();

    /** 
     * 	得到DPID
     * @return the DatapathId of the switch associated with the connection 
     * */
    DatapathId getDatapathId();

    /** 
     * 得到AuxId
     * @return the OFAuxId of the this connection 
     * */
    OFAuxId getAuxId();

    /**
    * Get the IP address of the remote (switch) end of the connection
    * 得到远程IP
    * @return the inet address
    */
    SocketAddress getRemoteInetAddress();

    /**
     * Get the IP address of the local end of the connection
     *得到本地IP
     * @return the inet address
     */
    SocketAddress getLocalInetAddress();

    /**
     * Get's the OFFactory that this connection was constructed with.
     * This is the factory that was found in the features reply during
     * the channel handshake
     * 获得构建此次连接的OFFactory。
     * 这个工厂在建立handshake过程中的features reply报文里
     * @return The connection's OFFactory
     */
    OFFactory getOFFactory();

    /**
     *	是否仍然连接 
     *  @return whether this connection is currently (still) connected to the controller.
     */
    boolean isConnected();


}
