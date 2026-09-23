import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ElectionService extends Remote {

    String getNodeId() throws RemoteException;

    String getNodeName() throws RemoteException;

    boolean isAlive() throws RemoteException;

    void electionMessage(String initiatorId)
            throws RemoteException;

    void coordinatorMessage(String coordinatorId)
            throws RemoteException;
}