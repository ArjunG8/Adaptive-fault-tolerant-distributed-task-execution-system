import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskClient {

        public static void main(String[] args) {

                try {

                        Registry registry = LocateRegistry.getRegistry(
                                        "localhost",
                                        1099);

                        CoordinatorService coordinator = (CoordinatorService) registry.lookup(
                                        "CoordinatorService");

                        System.out.println();
                        System.out.println(
                                        "==========================================");

                        System.out.println(
                                        "   TECHNOVA DISTRIBUTED TASK SYSTEM");

                        System.out.println(
                                        "   Leader: Arjun");

                        System.out.println(
                                        "   5 Nodes × 3 Worker Threads");

                        System.out.println(
                                        "   Concurrent Task Burst: 20 Tasks");

                        System.out.println(
                                        "==========================================");

                        /*
                         * Client-side thread pool.
                         *
                         * This creates multiple requests
                         * simultaneously.
                         */
                        ExecutorService clientPool = Executors.newFixedThreadPool(10);

                        String[] inputs = {

                                        "distributed computing is interesting",

                                        "java rmi distributed system",

                                        "fault tolerant task execution",

                                        "load balancing between workers",

                                        "leader election in distributed systems",

                                        "multithreading improves concurrency",

                                        "replication protects task state",

                                        "clock synchronization orders events",

                                        "map reduce parallel processing",

                                        "distributed task scheduling",

                                        "parallel processing improves throughput",

                                        "remote method invocation communication",

                                        "distributed nodes share workload",

                                        "worker threads execute tasks",

                                        "fault recovery in distributed systems",

                                        "dynamic task allocation",

                                        "concurrent programming with java",

                                        "distributed computing laboratory",

                                        "task scheduling and load balancing",

                                        "five node distributed architecture"
                        };

                        long startTime = System.currentTimeMillis();

                        /*
                         * Submit 20 tasks.
                         */
                        for (int i = 0; i < inputs.length; i++) {

                                final int taskNumber = i + 1;

                                final String taskId = String.format(
                                                "TASK-%03d",
                                                taskNumber);

                                final String input = inputs[i];

                                /*
                                 * Alternate between two task types.
                                 */
                                final String taskType = (i % 2 == 0)
                                                ? "WORD_COUNT"
                                                : "UPPERCASE";

                                clientPool.submit(() -> {

                                        try {

                                                System.out.println(
                                                                "[CLIENT] Sending "
                                                                                + taskId);

                                                String result = coordinator.submitTask(
                                                                taskId,
                                                                taskType,
                                                                input);

                                                System.out.println(
                                                                "[CLIENT] "
                                                                                + taskId
                                                                                + " RESULT: "
                                                                                + result);

                                        } catch (Exception e) {

                                                System.out.println(
                                                                "[CLIENT] "
                                                                                + taskId
                                                                                + " FAILED");

                                                System.out.println(
                                                                e.getMessage());
                                        }
                                });
                        }

                        /*
                         * Stop accepting new client tasks.
                         */
                        clientPool.shutdown();

                        /*
                         * Wait for all 20 client requests
                         * to finish.
                         */
                        boolean completed = clientPool.awaitTermination(
                                        60,
                                        TimeUnit.SECONDS);

                        long endTime = System.currentTimeMillis();

                        System.out.println();
                        System.out.println(
                                        "==========================================");

                        if (completed) {

                                System.out.println(
                                                "ALL TASKS COMPLETED");

                        } else {

                                System.out.println(
                                                "TIMEOUT: Some tasks did not finish");
                        }

                        System.out.println(
                                        "Total execution time = "
                                                        + (endTime - startTime)
                                                        + " ms");

                        System.out.println(
                                        "Tasks submitted = "
                                                        + inputs.length);

                        System.out.println(
                                        "Nodes available = 5");

                        System.out.println(
                                        "Threads per node = 3");

                        System.out.println(
                                        "==========================================");

                } catch (Exception e) {

                        System.err.println(
                                        "Client exception: "
                                                        + e);

                        e.printStackTrace();
                }
        }
}