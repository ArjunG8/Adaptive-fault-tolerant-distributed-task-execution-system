import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClockService extends Remote {

    /*
     * Returns the node's logical current time.
     */
    long getCurrentTime() throws RemoteException;

    /*
     * Returns the node's current clock offset.
     */
    long getClockOffset() throws RemoteException;

    /*
     * Applies a correction calculated by
     * the Berkeley synchronization algorithm.
     */
    void adjustClock(long correction)
            throws RemoteException;

    /*
     * Returns the name of the node.
     */
    String getNodeName()
            throws RemoteException;
}