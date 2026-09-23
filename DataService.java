import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataService extends Remote {

    String getTask() throws RemoteException;

    void updateTaskStatus(String taskId, String status)
            throws RemoteException;

    void replicateTask(
            String taskId,
            String taskName,
            String assignedTo,
            String status)
            throws RemoteException;
}