import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CoordinatorServiceImpl
        extends java.rmi.server.UnicastRemoteObject
        implements CoordinatorService {

    private final List<WorkerInfo> workers;

    /*
     * Thread-safe counter.
     *
     * Multiple client threads can call the
     * coordinator at the same time.
     */
    private final AtomicInteger nextWorker = new AtomicInteger(0);

    public CoordinatorServiceImpl(
            List<WorkerInfo> workers)
            throws RemoteException {

        super();

        this.workers = workers;
    }

    @Override
    public String submitTask(
            String taskId,
            String taskType,
            String input)
            throws RemoteException {

        if (workers.isEmpty()) {

            return "No workers available.";
        }

        System.out.println();
        System.out.println(
                "------------------------------------------");

        System.out.println(
                "[COORDINATOR] TASK RECEIVED");

        System.out.println(
                "Task ID   : " + taskId);

        System.out.println(
                "Task Type : " + taskType);

        /*
         * Select next worker using thread-safe
         * round-robin scheduling.
         */
        int startIndex = Math.floorMod(
                nextWorker.getAndIncrement(),
                workers.size());

        int attempts = 0;

        while (attempts < workers.size()) {

            int index = (startIndex + attempts)
                    % workers.size();

            WorkerInfo worker = workers.get(index);

            attempts++;

            try {

                System.out.println(
                        "[COORDINATOR] "
                                + taskId
                                + " -> "
                                + worker.name);

                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        worker.port);

                TaskService taskService = (TaskService) registry.lookup(
                        "TaskService");

                return taskService.executeTask(
                        taskId,
                        taskType,
                        input);

            } catch (Exception e) {

                System.out.println(
                        "[COORDINATOR] Worker unavailable: "
                                + worker.name);

                System.out.println(
                        "[COORDINATOR] Trying next worker...");
            }
        }

        return "All workers are currently unavailable.";
    }

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