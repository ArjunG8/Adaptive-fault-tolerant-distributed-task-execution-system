import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI worker node.
 *
 * For today's demo this is the worker node.
 * Later, the same pattern will be extended to multiple worker nodes
 * and a coordinator/leader.
 */
public class TaskServer {

    public static void main(String[] args) {
        try {
            // Create the remote service instance
            TaskService stub = new TaskServiceImpl();

            // Create and start the RMI Registry on port 1099 locally
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote service to the name "TaskService"
            registry.rebind("TaskService", stub);

            System.out.println("======================================");
            System.out.println("RMI Worker Node is running...");
            System.out.println("Service: TaskService");
            System.out.println("Port   : 1099");
            System.out.println("Waiting for remote tasks...");
            System.out.println("======================================");

        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
