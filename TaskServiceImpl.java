import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TaskServiceImpl extends UnicastRemoteObject
        implements TaskService {

    private final String nodeId;
    private final String nodeName;

    public TaskServiceImpl(String nodeId, String nodeName)
            throws RemoteException {

        super();

        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    @Override
    public String executeTask(String taskType, String input)
            throws RemoteException {

        System.out.println();
        System.out.println("--------------------------------------");
        System.out.println("TASK RECEIVED");
        System.out.println("Node ID   : " + nodeId);
        System.out.println("Node Name : " + nodeName);
        System.out.println("Task Type : " + taskType);
        System.out.println("Input     : " + input);

        String result;

        if ("WORD_COUNT".equalsIgnoreCase(taskType)) {

            int count = 0;

            if (input != null && !input.trim().isEmpty()) {
                count = input.trim().split("\\s+").length;
            }

            result = "Word Count = " + count;

        } else if ("UPPERCASE".equalsIgnoreCase(taskType)) {

            result = input == null ? "" : input.toUpperCase();

        } else {

            result = "Unknown task type: " + taskType;
        }

        System.out.println("Result    : " + result);
        System.out.println("--------------------------------------");

        return nodeName + " (" + nodeId + ") -> " + result;
    }
}