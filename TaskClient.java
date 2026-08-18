import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client node that submits a task to the remote worker using Java RMI.
 */
public class TaskClient {

    public static void main(String[] args) {
        try {
            // Locate the RMI registry running on localhost at port 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Lookup the remote object
            TaskService stub = (TaskService) registry.lookup("TaskService");

            // Submit a remote distributed-computing task
            String taskType = "WORD_COUNT";
            String input = "distributed computing is interesting";

            System.out.println("======================================");
            System.out.println("Client submitting remote task...");
            System.out.println("Task Type: " + taskType);
            System.out.println("Input    : " + input);
            System.out.println("======================================");

            // Invoke the remote procedure
            String result = stub.executeTask(taskType, input);

            System.out.println("Response from Worker: " + result);

        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
