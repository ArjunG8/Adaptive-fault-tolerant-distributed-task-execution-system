import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TaskClient {

    public static void main(String[] args) {

        try {

            // --------------------------------------
            // Connect to Leader
            // --------------------------------------

            Registry registry = LocateRegistry.getRegistry(
                    "localhost",
                    1099);

            CoordinatorService coordinator = (CoordinatorService) registry.lookup(
                    "CoordinatorService");

            System.out.println();
            System.out.println(
                    "======================================");

            System.out.println(
                    "CONNECTED TO TECHNOVA SYSTEM");

            System.out.println(
                    "Current Leader : Arjun");

            System.out.println(
                    "======================================");

            // --------------------------------------
            // TASK 1
            // --------------------------------------

            submit(
                    coordinator,
                    "WORD_COUNT",
                    "distributed computing is interesting");

            // --------------------------------------
            // TASK 2
            // --------------------------------------

            submit(
                    coordinator,
                    "UPPERCASE",
                    "distributed computing project");

            // --------------------------------------
            // TASK 3
            // --------------------------------------

            submit(
                    coordinator,
                    "WORD_COUNT",
                    "java rmi distributed task execution");

            // --------------------------------------
            // TASK 4
            // --------------------------------------

            submit(
                    coordinator,
                    "UPPERCASE",
                    "fault tolerant distributed system");

            // --------------------------------------
            // TASK 5
            // --------------------------------------

            submit(
                    coordinator,
                    "WORD_COUNT",
                    "load balancing and leader election");

        } catch (Exception e) {

            System.err.println(
                    "Client exception: " + e);

            e.printStackTrace();
        }
    }

    private static void submit(
            CoordinatorService coordinator,
            String taskType,
            String input)
            throws Exception {

        System.out.println();
        System.out.println("--------------------------------------");

        System.out.println(
                "Submitting Task");

        System.out.println(
                "Type  : " + taskType);

        System.out.println(
                "Input : " + input);

        String result = coordinator.submitTask(
                taskType,
                input);

        System.out.println(
                "Result: " + result);

        System.out.println(
                "--------------------------------------");
    }
}