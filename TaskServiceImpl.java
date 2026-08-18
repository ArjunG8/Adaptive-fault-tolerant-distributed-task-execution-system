import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Worker-side implementation of the remote task service.
 */
public class TaskServiceImpl extends UnicastRemoteObject implements TaskService {

    // Constructor required to handle RemoteException
    public TaskServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String executeTask(String taskType, String input)
            throws RemoteException {

        System.out.println("Worker received task:");
        System.out.println("Task Type : " + taskType);
        System.out.println("Input     : " + input);

        if ("WORD_COUNT".equalsIgnoreCase(taskType)) {
            int count = input == null || input.trim().isEmpty()
                    ? 0
                    : input.trim().split("\\s+").length;

            String result = "Word Count = " + count;
            System.out.println("Worker computed: " + result);
            return result;
        }

        if ("UPPERCASE".equalsIgnoreCase(taskType)) {
            String result = input == null ? "" : input.toUpperCase();
            System.out.println("Worker computed: " + result);
            return result;
        }

        return "Unknown task type: " + taskType;
    }
}
