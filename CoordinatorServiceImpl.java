import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class CoordinatorServiceImpl
        extends java.rmi.server.UnicastRemoteObject
        implements CoordinatorService {

    private final List<WorkerInfo> workers;

    private int nextWorkerIndex = 0;

    public CoordinatorServiceImpl(List<WorkerInfo> workers)
            throws RemoteException {

        super();

        this.workers = workers;
    }

    @Override
    public synchronized String submitTask(
            String taskType,
            String input)
            throws RemoteException {

        if (workers.isEmpty()) {
            return "No workers available.";
        }

        System.out.println();
        System.out.println("======================================");
        System.out.println("COORDINATOR RECEIVED TASK");
        System.out.println("Task Type : " + taskType);
        System.out.println("Input     : " + input);
        System.out.println("======================================");

        int attempts = 0;

        while (attempts < workers.size()) {

            WorkerInfo worker = workers.get(nextWorkerIndex);

            nextWorkerIndex = (nextWorkerIndex + 1) % workers.size();

            attempts++;

            try {

                System.out.println(
                        "Assigning task to: "
                                + worker.name
                                + " (" + worker.nodeId + ")");

                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        worker.port);

                TaskService taskService = (TaskService) registry.lookup("TaskService");

                String result = taskService.executeTask(
                        taskType,
                        input);

                System.out.println(
                        "Task completed by: "
                                + worker.name);

                return result;

            } catch (Exception e) {

                System.out.println(
                        "Worker unavailable: "
                                + worker.name);

                System.out.println(
                        "Trying next worker...");
            }
        }

        return "All workers are currently unavailable.";
    }

    // Stores information about one worker
    public static class WorkerInfo {

        String nodeId;
        String name;
        int port;

        public WorkerInfo(
                String nodeId,
                String name,
                int port) {

            this.nodeId = nodeId;
            this.name = name;
            this.port = port;
        }
    }
}