package net.floodlightcontroller.core;

/**
 * abstracts the 'back side' of a Future that is being listened on, i.e., an
 * object that receives a result or an error of the computation once it is ready.
 * A deliverable can accept multiple computation results, indicated by the
 * return value of deliver.
 *抽象一个Future的反面。
 *对象接收一个结果或者一个计算错误一旦它准备好
 *一个deliverable能够接收多种计算结果，
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 * @param <T>
 *            type of the result of the computation
 */
public interface Deliverable<T> {
    public static enum Status {
        DONE,
        CONTINUE
    }

    /**
     * deliver the result after a successful computation has completed
     *	传递一个结果在成功计算完成后
     * @param msg
     *            result
     * @return whether the delivery is complete with this result.
     **/
    public void deliver(T msg);

    /** deliver an error result for the computation
     * 	传递一个错误信息
     * @param cause throwable that describes the error
     */
    void deliverError(Throwable cause);

    /** whether or not the deliverable has been completed before.
     */
    boolean isDone();

    boolean cancel(boolean mayInterruptIfRunning);
}
