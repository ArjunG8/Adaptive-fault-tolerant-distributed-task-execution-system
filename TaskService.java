import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskService extends Remote {

    String executeTask(
            String taskId,
            String taskType,
            String input)
            throws RemoteException;
}