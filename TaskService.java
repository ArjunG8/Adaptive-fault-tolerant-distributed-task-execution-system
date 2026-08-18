import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the distributed task execution service.
 * This follows the same RMI structure used in the lab's Adder example.
 */
public interface TaskService extends Remote {

    // Remote method declaration
    public String executeTask(String taskType, String input)
            throws RemoteException;
}
