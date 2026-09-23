import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class DataConsistencyClient {

    static String[] names = {
            "Arjun",
            "Sarthak",
            "Amar",
            "Kaner",
            "Jogi"
    };

    static String[] ids = {
            "NODE-01",
            "NODE-02",
            "NODE-03",
            "NODE-04",
            "NODE-05"
    };

    static int[] ports = {
            1099,
            1100,
            1101,
            1102,
            1103
    };

    static final String TASK_ID = "T-101";
    static final String TASK_NAME = "Deploy TECHNOVA Web Application";
    static final String ASSIGNED_TO = "Amar (NODE-03)";

    static DataService connect(int index) throws Exception {

        Registry registry = LocateRegistry.getRegistry(
                "localhost",
                ports[index]);

        return (DataService) registry.lookup("DataService");
    }

    static void line() {

        System.out.println(
                "==================================================");
    }

    // ==================================================
    // SHOW REPLICATED DATA
    // ==================================================

    static void showAllReplicas() {

        line();

        System.out.println("CURRENT REPLICA DATA");

        line();

        for (int i = 0; i < names.length; i++) {

            try {

                DataService node = connect(i);

                System.out.println(
                        names[i] + " (" + ids[i] + ")");

                System.out.println(
                        "   " + node.getTask());

            } catch (Exception e) {

                System.out.println(
                        names[i]
                                + " (" + ids[i] + ") -> OFFLINE");
            }
        }

        line();
    }

    // ==================================================
    // REPLICATION
    // ==================================================

    static void replicationDemo() {

        line();

        System.out.println("REPLICATION DEMONSTRATION");

        line();

        System.out.println(
                "Primary: Arjun (NODE-01)");

        System.out.println(
                "Task: " + TASK_ID
                        + " - " + TASK_NAME);

        System.out.println();

        try {

            DataService primary = connect(0);

            String before = primary.getTask();

            System.out.println(
                    "Before replication:");

            System.out.println(
                    "Arjun -> " + before);

            System.out.println();

            primary.updateTaskStatus(
                    TASK_ID,
                    "Completed");

            System.out.println(
                    "Arjun -> Status changed to Completed");

            System.out.println();

            System.out.println(
                    "Replicating to other nodes...");

            for (int i = 1; i < names.length; i++) {

                try {

                    DataService replica = connect(i);

                    replica.replicateTask(
                            TASK_ID,
                            TASK_NAME,
                            ASSIGNED_TO,
                            "Completed");

                    System.out.println(
                            names[i]
                                    + " -> Completed");

                } catch (Exception e) {

                    System.out.println(
                            names[i]
                                    + " -> Replication Failed");
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Arjun -> OFFLINE");
        }

        System.out.println();

        System.out.println(
                "Replication completed.");

        System.out.println();

        showAllReplicas();
    }

    // ==================================================
    // STRONG CONSISTENCY
    // ==================================================

    static void strongConsistency() {

        line();

        System.out.println("STRONG CONSISTENCY");

        line();

        System.out.println(
                "Primary: Arjun (NODE-01)");

        System.out.println(
                "Task: " + TASK_ID
                        + " - " + TASK_NAME);

        System.out.println();

        try {

            DataService primary = connect(0);

            String oldData = primary.getTask();

            System.out.println(
                    "Current value:");

            System.out.println(
                    "Arjun -> " + oldData);

            System.out.println();

            // Update primary

            primary.updateTaskStatus(
                    TASK_ID,
                    "Completed");

            System.out.println(
                    "Updating primary...");

            System.out.println(
                    "Arjun -> Completed");

            System.out.println();

            // Update all replicas

            System.out.println(
                    "Updating replicas...");

            boolean allUpdated = true;

            for (int i = 1; i < names.length; i++) {

                try {

                    DataService replica = connect(i);

                    replica.replicateTask(
                            TASK_ID,
                            TASK_NAME,
                            ASSIGNED_TO,
                            "Completed");

                    System.out.println(
                            names[i]
                                    + " -> Completed");

                } catch (Exception e) {

                    allUpdated = false;

                    System.out.println(
                            names[i]
                                    + " -> Update Failed");
                }
            }

            System.out.println();

            // Verify actual values

            System.out.println(
                    "Checking replica values...");

            System.out.println();

            for (int i = 0; i < names.length; i++) {

                try {

                    DataService node = connect(i);

                    String data = node.getTask();

                    System.out.println(
                            names[i]
                                    + " -> "
                                    + getStatus(data));

                    if (!data.contains(
                            "Status: Completed")) {

                        allUpdated = false;
                    }

                } catch (Exception e) {

                    allUpdated = false;

                    System.out.println(
                            names[i]
                                    + " -> OFFLINE");
                }
            }

            System.out.println();

            if (allUpdated) {

                System.out.println(
                        "WRITE SUCCESSFUL");

            } else {

                System.out.println(
                        "WRITE NOT CONFIRMED");
            }

        } catch (Exception e) {

            System.out.println(
                    "Arjun -> OFFLINE");
        }

        System.out.println();

        System.out.println(
                "FINAL REPLICA STATE");

        showAllReplicas();
    }

    // ==================================================
    // EVENTUAL CONSISTENCY
    // ==================================================

    static void eventualConsistency() {

        line();

        System.out.println("EVENTUAL CONSISTENCY");

        line();

        System.out.println(
                "Primary: Arjun (NODE-01)");

        System.out.println(
                "Task: " + TASK_ID
                        + " - " + TASK_NAME);

        System.out.println();

        try {

            DataService primary = connect(0);

            String oldData = primary.getTask();

            String oldStatus = getStatus(oldData);

            String newStatus;

            if (oldStatus.equals("Completed")) {

                newStatus = "Verified";

            } else {

                newStatus = "Completed";
            }

            System.out.println(
                    "Current value:");

            System.out.println(
                    "Arjun -> " + oldStatus);

            System.out.println();

            // Update only primary

            primary.updateTaskStatus(
                    TASK_ID,
                    newStatus);

            System.out.println(
                    "Updating primary...");

            System.out.println(
                    "Arjun -> "
                            + oldStatus
                            + " -> "
                            + newStatus);

            System.out.println();

            // Immediately read all nodes

            System.out.println(
                    "Current replica values:");

            System.out.println();

            for (int i = 0; i < names.length; i++) {

                try {

                    DataService node = connect(i);

                    String data = node.getTask();

                    System.out.println(
                            names[i]
                                    + " -> "
                                    + getStatus(data));

                } catch (Exception e) {

                    System.out.println(
                            names[i]
                                    + " -> OFFLINE");
                }
            }

            System.out.println();

            // Delayed replication

            System.out.println(
                    "Updating replicas...");

            System.out.println();

            for (int i = 1; i < names.length; i++) {

                try {

                    Thread.sleep(1500);

                    DataService replica = connect(i);

                    replica.replicateTask(
                            TASK_ID,
                            TASK_NAME,
                            ASSIGNED_TO,
                            newStatus);

                    System.out.println(
                            names[i]
                                    + " -> "
                                    + newStatus);

                } catch (Exception e) {

                    System.out.println(
                            names[i]
                                    + " -> Update Failed");
                }
            }

            System.out.println();

            // Final verification

            System.out.println(
                    "Final replica values:");

            System.out.println();

            boolean allSame = true;

            for (int i = 0; i < names.length; i++) {

                try {

                    DataService node = connect(i);

                    String data = node.getTask();

                    String status = getStatus(data);

                    System.out.println(
                            names[i]
                                    + " -> "
                                    + status);

                    if (!status.equals(newStatus)) {

                        allSame = false;
                    }

                } catch (Exception e) {

                    allSame = false;

                    System.out.println(
                            names[i]
                                    + " -> OFFLINE");
                }
            }

            System.out.println();

            if (allSame) {

                System.out.println(
                        "All replicas are synchronized.");

            } else {

                System.out.println(
                        "Replica synchronization incomplete.");
            }

        } catch (Exception e) {

            System.out.println(
                    "Arjun -> OFFLINE");
        }
    }

    // ==================================================
    // EXTRACT STATUS FROM ACTUAL RMI RESPONSE
    // ==================================================

    static String getStatus(String taskData) {

        String key = "Status: ";

        int position = taskData.indexOf(key);

        if (position == -1) {

            return "Unknown";
        }

        return taskData.substring(
                position + key.length()).trim();
    }

    // ==================================================
    // MAIN MENU
    // ==================================================

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            line();

            System.out.println(
                    "TECHNOVA DISTRIBUTED TASK SYSTEM");

            System.out.println(
                    "DATA CONSISTENCY AND REPLICATION");

            line();

            System.out.println();

            System.out.println(
                    "USE CASE:");

            System.out.println(
                    TASK_ID
                            + " - "
                            + TASK_NAME);

            System.out.println(
                    "Assigned To: "
                            + ASSIGNED_TO);

            System.out.println();

            System.out.println(
                    "1. Show Replicated Data");

            System.out.println(
                    "2. Replication Demonstration");

            System.out.println(
                    "3. Strong Consistency");

            System.out.println(
                    "4. Eventual Consistency");

            System.out.println(
                    "5. Exit");

            System.out.print(
                    "Enter your choice: ");

            int choice = scanner.nextInt();

            System.out.println();

            switch (choice) {

                case 1:

                    showAllReplicas();
                    break;

                case 2:

                    replicationDemo();
                    break;

                case 3:

                    strongConsistency();
                    break;

                case 4:

                    eventualConsistency();
                    break;

                case 5:

                    System.out.println(
                            "Experiment ended.");

                    scanner.close();
                    return;

                default:

                    System.out.println(
                            "Invalid choice.");
            }
        }
    }
}