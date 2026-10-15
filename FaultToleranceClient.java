import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FaultToleranceClient {

    private static final String HOST = "localhost";

    private static final int PRIMARY_PORT = 1099;
    private static final int BACKUP_PORT = 1100;

    private static final String SERVICE_NAME =
            "TaskStateService";

    private static int activePort =
            PRIMARY_PORT;

    private static final Scanner scanner =
            new Scanner(System.in);

    private static TaskStateService getService(
            int port)
            throws Exception {

        Registry registry =
                LocateRegistry.getRegistry(
                        HOST,
                        port
                );

        return (TaskStateService)
                registry.lookup(
                        SERVICE_NAME
                );
    }

    private static TaskStateService getActiveService() {

        try {

            TaskStateService service =
                    getService(activePort);

            service.ping();

            return service;

        } catch (Exception e) {

            try {

                int alternatePort =
                        activePort == PRIMARY_PORT
                                ? BACKUP_PORT
                                : PRIMARY_PORT;

                TaskStateService service =
                        getService(alternatePort);

                service.ping();

                activePort = alternatePort;

                return service;

            } catch (Exception ignored) {

                return null;
            }
        }
    }

    private static void updateTask() {

        TaskStateService service =
                getActiveService();

        if (service == null) {

            System.out.println(
                    "Active coordinator unavailable."
            );

            return;
        }

        try {

            System.out.print("Task ID: ");
            String taskId =
                    scanner.nextLine();

            System.out.print("Task Name: ");
            String taskName =
                    scanner.nextLine();

            System.out.print("Node ID: ");
            String nodeId =
                    scanner.nextLine();

            System.out.print("Status: ");
            String status =
                    scanner.nextLine();

            String result =
                    service.updateTask(
                            taskId,
                            taskName,
                            nodeId,
                            status
                    );

            System.out.println();
            System.out.println(result);

        } catch (Exception e) {

            System.out.println(
                    "Unable to process task update."
            );
        }
    }

    private static void showTask() {

        TaskStateService service =
                getActiveService();

        if (service == null) {

            System.out.println(
                    "Active coordinator unavailable."
            );

            return;
        }

        try {

            System.out.print("Task ID: ");

            String taskId =
                    scanner.nextLine();

            TaskState state =
                    service.getTask(taskId);

            System.out.println();

            if (state == null) {

                System.out.println(
                        "Task state not found."
                );

                return;
            }

            System.out.println(
                    "TASK STATE"
            );

            System.out.println(
                    "------------------------------"
            );

            System.out.println(
                    "Task ID   : "
                            + state.getTaskId()
            );

            System.out.println(
                    "Task Name : "
                            + state.getTaskName()
            );

            System.out.println(
                    "Node      : "
                            + state.getNodeId()
            );

            System.out.println(
                    "Status    : "
                            + state.getStatus()
            );

            System.out.println(
                    "Version   : "
                            + state.getVersion()
            );

        } catch (Exception e) {

            System.out.println(
                    "Unable to retrieve task state."
            );
        }
    }

    private static void showSystemStatus() {

        boolean primaryAvailable = false;
        boolean backupAvailable = false;

        try {

            TaskStateService primary =
                    getService(PRIMARY_PORT);

            primary.ping();

            primaryAvailable = true;

        } catch (Exception ignored) {
        }

        try {

            TaskStateService backup =
                    getService(BACKUP_PORT);

            backup.ping();

            backupAvailable = true;

        } catch (Exception ignored) {
        }

        System.out.println();
        System.out.println(
                "FAULT-TOLERANCE STATUS"
        );

        System.out.println(
                "------------------------------"
        );

        if (primaryAvailable) {

            try {

                TaskStateService primary =
                        getService(PRIMARY_PORT);

                System.out.println(
                        "Active Coordinator : "
                                + primary.getNodeId()
                );

                System.out.println(
                        "Service Availability : ONLINE"
                );

                System.out.println(
                        "Replication Partner : NODE-02"
                );

                System.out.println(
                        "Replicated Tasks   : "
                                + primary.getTaskCount()
                );

                activePort =
                        PRIMARY_PORT;

            } catch (Exception e) {

                System.out.println(
                        "Unable to retrieve status."
                );
            }

        } else if (backupAvailable) {

            try {

                TaskStateService backup =
                        getService(BACKUP_PORT);

                System.out.println(
                        "Active Coordinator : "
                                + backup.getNodeId()
                );

                System.out.println(
                        "Service Availability : RESTORED"
                );

                System.out.println(
                        "Replication Partner : NODE-01"
                );

                System.out.println(
                        "Replicated Tasks   : "
                                + backup.getTaskCount()
                );

                activePort =
                        BACKUP_PORT;

            } catch (Exception e) {

                System.out.println(
                        "Unable to retrieve status."
                );
            }

        } else {

            System.out.println(
                    "Active Coordinator : UNAVAILABLE"
            );

            System.out.println(
                    "Service Availability : OFFLINE"
            );
        }
    }

    private static void checkHealth() {

        System.out.println();
        System.out.println(
                "SERVICE HEALTH"
        );

        System.out.println(
                "------------------------------"
        );

        try {

            TaskStateService service =
                    getService(PRIMARY_PORT);

            service.ping();

            System.out.println(
                    "NODE-01 : ONLINE"
            );

        } catch (Exception e) {

            System.out.println(
                    "NODE-01 : UNAVAILABLE"
            );
        }

        try {

            TaskStateService service =
                    getService(BACKUP_PORT);

            service.ping();

            System.out.println(
                    "NODE-02 : ONLINE"
            );

        } catch (Exception e) {

            System.out.println(
                    "NODE-02 : UNAVAILABLE"
            );
        }
    }

    private static void failover() {

        System.out.println();
        System.out.println(
                "FAILOVER"
        );

        System.out.println(
                "------------------------------"
        );

        try {

            TaskStateService primary =
                    getService(PRIMARY_PORT);

            primary.ping();

            System.out.println(
                    "Current service is available."
            );

            System.out.println(
                    "Failover is not required."
            );

            return;

        } catch (Exception e) {

            System.out.println(
                    "Node failure detected."
            );
        }

        try {

            TaskStateService backup =
                    getService(BACKUP_PORT);

            backup.ping();

            System.out.println(
                    "Promoting replication partner..."
            );

            String result =
                    backup.promoteToPrimary();

            activePort =
                    BACKUP_PORT;

            System.out.println(result);

            System.out.println(
                    "Service availability restored."
            );

        } catch (Exception e) {

            System.out.println(
                    "Failover could not be completed."
            );
        }
    }

    private static void printMenu() {

        System.out.println();
        System.out.println(
                "TECHNOVA FAULT-TOLERANCE CLIENT"
        );

        System.out.println(
                "1. Update Task"
        );

        System.out.println(
                "2. View Task State"
        );

        System.out.println(
                "3. System Status"
        );

        System.out.println(
                "4. Health Check"
        );

        System.out.println(
                "5. Execute Failover"
        );

        System.out.println(
                "6. Exit"
        );

        System.out.print(
                "Select operation: "
        );
    }

    public static void main(String[] args) {

        while (true) {

            printMenu();

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":
                    updateTask();
                    break;

                case "2":
                    showTask();
                    break;

                case "3":
                    showSystemStatus();
                    break;

                case "4":
                    checkHealth();
                    break;

                case "5":
                    failover();
                    break;

                case "6":

                    System.out.println(
                            "Client terminated."
                    );

                    return;

                default:

                    System.out.println(
                            "Invalid operation."
                    );
            }
        }
    }
}