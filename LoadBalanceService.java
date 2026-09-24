import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalanceService extends Remote {

    String getNodeId() throws RemoteException;

    String getNodeName() throws RemoteException;

    int getActiveTasks() throws RemoteException;

    int getMaxTasks() throws RemoteException;

    int getTotalEnergy() throws RemoteException;

    int getAvailableEnergy() throws RemoteException;

    int getProcessingPower() throws RemoteException;

    double getPredictedLoad() throws RemoteException;

    boolean isOnline() throws RemoteException;

    String allocateTask(
            String taskId,
            String taskName,
            int demand)
            throws RemoteException;

    void resetNode() throws RemoteException;
}