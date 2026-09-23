import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DataServiceImpl extends UnicastRemoteObject
        implements DataService {

    private String taskId = "T-101";
    private String taskName = "Deploy TECHNOVA Web Application";
    private String assignedTo = "Amar (NODE-03)";
    private String status = "Pending";

    public DataServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized String getTask()
            throws RemoteException {

        return "Task ID: " + taskId
                + " | Task: " + taskName
                + " | Assigned To: " + assignedTo
                + " | Status: " + status;
    }

    @Override
    public synchronized void updateTaskStatus(
            String taskId,
            String status)
            throws RemoteException {

        if (this.taskId.equals(taskId)) {

            this.status = status;

            System.out.println(
                    "Local update: "
                            + taskId
                            + " -> "
                            + status);
        }
    }

    @Override
    public synchronized void replicateTask(
            String taskId,
            String taskName,
            String assignedTo,
            String status)
            throws RemoteException {

        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.status = status;

        System.out.println(
                "Replica updated: "
                        + taskId
                        + " -> "
                        + status);
    }
}