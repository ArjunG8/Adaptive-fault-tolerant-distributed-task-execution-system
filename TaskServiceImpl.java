import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskServiceImpl extends UnicastRemoteObject
        implements TaskService {

    private final String nodeId;
    private final String nodeName;

    /*
     * Each node has a fixed pool of 3 worker threads.
     *
     * Example:
     *
     * Arjun
     * ├── Thread 1
     * ├── Thread 2
     * └── Thread 3
     */
    private final ExecutorService workerPool = Executors.newFixedThreadPool(3);

    public TaskServiceImpl(
            String nodeId,
            String nodeName)
            throws RemoteException {

        super();

        this.nodeId = nodeId;
        this.nodeName = nodeName;

        System.out.println(
                "[" + nodeName + "] "
                        + "Worker pool initialized with 3 threads.");
    }

    @Override
    public String executeTask(
            String taskId,
            String taskType,
            String input)
            throws RemoteException {

        try {

            /*
             * Submit task to this node's thread pool.
             */
            Future<String> future = workerPool.submit(
                    () -> processTask(
                            taskId,
                            taskType,
                            input));

            /*
             * Return the result after the worker
             * thread completes the task.
             */
            return future.get();

        } catch (Exception e) {

            throw new RemoteException(
                    "Task execution failed on "
                            + nodeName,
                    e);
        }
    }

    private String processTask(
            String taskId,
            String taskType,
            String input) {

        String threadName = Thread.currentThread().getName();

        long startTime = System.currentTimeMillis();

        System.out.println();
        System.out.println(
                "==========================================");

        System.out.println(
                "[" + nodeName + "] TASK STARTED");

        System.out.println(
                "Task ID   : " + taskId);

        System.out.println(
                "Node      : " + nodeName);

        System.out.println(
                "Thread    : " + threadName);

        System.out.println(
                "Task Type : " + taskType);

        System.out.println(
                "Input     : " + input);

        String result;

        try {

            if ("WORD_COUNT".equalsIgnoreCase(taskType)) {

                int count = 0;

                if (input != null &&
                        !input.trim().isEmpty()) {

                    count = input.trim()
                            .split("\\s+").length;
                }

                /*
                 * Artificial delay is intentional.
                 * It makes concurrent execution
                 * clearly visible during demonstration.
                 */
                Thread.sleep(2000);

                result = "Word Count = " + count;

            } else if ("UPPERCASE".equalsIgnoreCase(taskType)) {

                Thread.sleep(2000);

                result = input == null
                        ? ""
                        : input.toUpperCase();

            } else if ("HEAVY_COMPUTE".equalsIgnoreCase(taskType)) {

                /*
                 * CPU-intensive demonstration task.
                 */
                long sum = 0;

                for (long i = 0; i < 50_000_000L; i++) {

                    sum += i % 97;
                }

                result = "Computation Result = "
                        + sum;

            } else {

                result = "Unknown task type: "
                        + taskType;
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            result = "Task interrupted.";
        }

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        System.out.println(
                "[" + nodeName + "] TASK COMPLETED");

        System.out.println(
                "Task ID   : " + taskId);

        System.out.println(
                "Thread    : " + threadName);

        System.out.println(
                "Result    : " + result);

        System.out.println(
                "Duration  : " + duration + " ms");

        System.out.println(
                "==========================================");

        return nodeName
                + " [" + threadName + "] -> "
                + result
                + " | " + duration + " ms";
    }
}