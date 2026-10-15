import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskStateServiceImpl
        extends UnicastRemoteObject
        implements TaskStateService {

    private final String nodeId;

    private volatile String role;

    private final int peerPort;

    private final Map<String, TaskState> taskStates =
            new ConcurrentHashMap<>();

    public TaskStateServiceImpl(
            String nodeId,
            String role,
            int peerPort)
            throws RemoteException {

        super();

        this.nodeId = nodeId;
        this.role = role;
        this.peerPort = peerPort;
    }

    @Override
    public synchronized String updateTask(
            String taskId,
            String taskName,
            String taskNodeId,
            String status)
            throws RemoteException {

        if (!role.equals("PRIMARY")) {

            return "UPDATE REJECTED: "
                    + "Active coordinator unavailable.";
        }

        TaskState previous =
                taskStates.get(taskId);

        long version = 1;

        if (previous != null) {
            version = previous.getVersion() + 1;
        }

        TaskState state =
                new TaskState(
                        taskId,
                        taskName,
                        taskNodeId,
                        status,
                        version,
                        System.currentTimeMillis()
                );

        System.out.println();
        System.out.println("[TASK UPDATE]");
        System.out.println("Task    : " + taskId);
        System.out.println("Status  : " + status);
        System.out.println("Version : " + version);

        try {

            Registry registry =
                    LocateRegistry.getRegistry(
                            "localhost",
                            peerPort
                    );

            TaskStateService peer =
                    (TaskStateService)
                            registry.lookup(
                                    "TaskStateService"
                            );

            System.out.println(
                    "[REPLICATION] Synchronizing update..."
            );

            String response =
                    peer.replicateTask(state);

            if (!response.startsWith("ACK")) {

                System.out.println(
                        "[REPLICATION] Synchronization failed."
                );

                return "UPDATE FAILED: "
                        + "Replication was not confirmed.";
            }

            taskStates.put(
                    taskId,
                    state
            );

            System.out.println(
                    "[REPLICATION] Synchronization confirmed."
            );

            System.out.println(
                    "[UPDATE] State committed successfully."
            );

            return "UPDATE COMMITTED: "
                    + taskId
                    + " version="
                    + version;

        } catch (Exception e) {

            System.out.println(
                    "[REPLICATION] Peer unavailable."
            );

            return "UPDATE FAILED: "
                    + "Replication peer is unavailable.";
        }
    }

    @Override
    public synchronized String replicateTask(
            TaskState state)
            throws RemoteException {

        TaskState existing =
                taskStates.get(
                        state.getTaskId()
                );

        if (existing != null
                && state.getVersion()
                <= existing.getVersion()) {

            return "ACK: existing version is newer.";
        }

        taskStates.put(
                state.getTaskId(),
                state
        );

        System.out.println();
        System.out.println(
                "[REPLICATION] Update received."
        );

        System.out.println(
                "Task    : "
                        + state.getTaskId()
        );

        System.out.println(
                "Status  : "
                        + state.getStatus()
        );

        System.out.println(
                "Version : "
                        + state.getVersion()
        );

        return "ACK: version "
                + state.getVersion()
                + " synchronized.";
    }

    @Override
    public TaskState getTask(
            String taskId)
            throws RemoteException {

        return taskStates.get(taskId);
    }

    @Override
    public String getNodeRole()
            throws RemoteException {

        return role;
    }

    @Override
    public String getNodeId()
            throws RemoteException {

        return nodeId;
    }

    @Override
    public boolean ping()
            throws RemoteException {

        return true;
    }

    @Override
    public synchronized String promoteToPrimary()
            throws RemoteException {

        if (role.equals("PRIMARY")) {

            return "Node is already active.";
        }

        role = "PRIMARY";

        System.out.println();
        System.out.println(
                "[FAILOVER] Service promoted successfully."
        );

        System.out.println(
                "Active node: " + nodeId
        );

        System.out.println(
                "Replicated task states available: "
                        + taskStates.size()
        );

        return "FAILOVER COMPLETED: "
                + nodeId
                + " is now active.";
    }

    @Override
    public int getTaskCount()
            throws RemoteException {

        return taskStates.size();
    }
}