import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinatorService extends Remote {

    String submitTask(
            String taskId,
            String taskType,
            String input)
            throws RemoteException;
}