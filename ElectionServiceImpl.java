import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ElectionServiceImpl
        extends UnicastRemoteObject
        implements ElectionService {

    private final String nodeId;
    private final String nodeName;

    private volatile String coordinatorId;

    public ElectionServiceImpl(
            String nodeId,
            String nodeName)
            throws RemoteException {

        super();

        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.coordinatorId = nodeId;
    }

    @Override
    public String getNodeId()
            throws RemoteException {

        return nodeId;
    }

    @Override
    public String getNodeName()
            throws RemoteException {

        return nodeName;
    }

    @Override
    public boolean isAlive()
            throws RemoteException {

        return true;
    }

    @Override
    public void electionMessage(
            String initiatorId)
            throws RemoteException {

        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println(
                "[" + nodeName + "] ELECTION MESSAGE RECEIVED");

        System.out.println(
                "Initiator : " + initiatorId);

        System.out.println(
                "My ID     : " + nodeId);

        System.out.println(
                "[" + nodeName + "] Response: OK");

        System.out.println("------------------------------------------");
    }

    @Override
    public synchronized void coordinatorMessage(
            String coordinatorId)
            throws RemoteException {

        this.coordinatorId = coordinatorId;

        System.out.println();
        System.out.println("==========================================");
        System.out.println(
                "[" + nodeName + "] NEW COORDINATOR");
        System.out.println(
                "Coordinator ID : " + coordinatorId);
        System.out.println("==========================================");
    }
}