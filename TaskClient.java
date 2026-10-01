import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskClient {

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.getRegistry(
                            "localhost",
                            1099);

            CoordinatorService coordinator =
                    (CoordinatorService) registry.lookup(
                            "CoordinatorService");

            System.out.println();
            System.out.println(
                    "TECHNOVA DISTRIBUTED TASK SYSTEM");
            System.out.println(
                    "Leader: Arjun");
            System.out.println(
                    "5 Nodes x 3 Worker Threads");
            System.out.println(
                    "Concurrent Task Burst: 20 Tasks");
            System.out.println();

            String[] taskTypes = {
                    "WORD_COUNT",
                    "UPPERCASE"
            };

            String[] inputs = {
                    "Distributed systems enable resource sharing",
                    "Java RMI supports remote method invocation",
                    "TECHNOVA manages distributed tasks",
                    "Multithreading improves concurrent execution",
                    "Load balancing distributes workload",
                    "Fault tolerance improves reliability",
                    "Distributed nodes communicate remotely",
                    "Concurrent clients submit multiple tasks",
                    "RMI enables distributed object communication",
                    "Task scheduling improves system performance",
                    "Distributed computing uses multiple nodes",
                    "Remote services process client requests",
                    "Worker nodes execute assigned tasks",
                    "Round robin distributes tasks cyclically",
                    "Concurrent execution reduces waiting time",
                    "TECHNOVA uses Java distributed computing",
                    "Multiple workers process tasks",
                    "Task coordination manages workers",
                    "Fault handling improves availability",
                    "Distributed task processing is scalable"
            };

            ExecutorService executor =
                    Executors.newFixedThreadPool(10);

            long startTime =
                    System.currentTimeMillis();

            Future<?>[] futures =
                    new Future<?>[20];

            for (int i = 0; i < 20; i++) {

                final int index = i;

                futures[i] = executor.submit(() -> {

                    String taskId =
                            String.format(
                                    "TASK-%03d",
                                    index + 1);

                    String taskType =
                            taskTypes[index % 2];

                    String input =
                            inputs[index];

                    try {

                        String result =
                                coordinator.submitTask(
                                        taskId,
                                        taskType,
                                        input);

                        System.out.println(
                                taskId
                                        + " -> "
                                        + result);

                    } catch (Exception e) {

                        System.out.println(
                                taskId
                                        + " -> ERROR: "
                                        + e.getMessage());
                    }
                });
            }

            for (Future<?> future : futures) {
                future.get();
            }

            executor.shutdown();

            long endTime =
                    System.currentTimeMillis();

            System.out.println();
            System.out.println(
                    "All tasks completed.");
            System.out.println(
                    "Total execution time: "
                            + (endTime - startTime)
                            + " ms");

        } catch (Exception e) {

            System.out.println(
                    "Client exception: "
                            + e);

            e.printStackTrace();
        }
    }
}