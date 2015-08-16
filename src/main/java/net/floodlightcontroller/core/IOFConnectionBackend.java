package net.floodlightcontroller.core;

import net.floodlightcontroller.core.internal.IOFConnectionListener;

public interface IOFConnectionBackend extends IOFConnection {
    /**
     * Disconnect the channel
     * 断开连接
     */
    void disconnect();

    /**
     * Cancel all pending request
     * 撤销所有等待请求
     */
    void cancelAllPendingRequests();

    /** 
     * 是否允许写入
     * @return whether the output stream associated with this connection
     *  is currently writeable (for throttling)
     */
    boolean isWritable();

    /** 
     * 设置监听器
     * set the message/closing listener for this connection 
     **/
    void setListener(IOFConnectionListener listener);
}
