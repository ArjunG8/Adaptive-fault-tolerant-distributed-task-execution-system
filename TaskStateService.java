import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskStateService extends Remote {

    String updateTask(
            String taskId,
            String taskName,
            String nodeId,
            String status)
            throws RemoteException;

    String replicateTask(TaskState state)
            throws RemoteException;

    TaskState getTask(String taskId)
            throws RemoteException;

    String getNodeRole()
            throws RemoteException;

    String getNodeId()
            throws RemoteException;

    boolean ping()
            throws RemoteException;

    String promoteToPrimary()
            throws RemoteException;

    int getTaskCount()
            throws RemoteException;
}