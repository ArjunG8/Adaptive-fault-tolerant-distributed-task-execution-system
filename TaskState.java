import java.io.Serializable;

public class TaskState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String taskId;
    private final String taskName;
    private final String nodeId;
    private final String status;
    private final long version;
    private final long timestamp;

    public TaskState(
            String taskId,
            String taskName,
            String nodeId,
            String status,
            long version,
            long timestamp) {

        this.taskId = taskId;
        this.taskName = taskName;
        this.nodeId = nodeId;
        this.status = status;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getStatus() {
        return status;
    }

    public long getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return taskId
                + " | "
                + taskName
                + " | "
                + status
                + " | node="
                + nodeId
                + " | version="
                + version;
    }
}