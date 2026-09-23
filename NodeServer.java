import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NodeServer {

        public static void main(String[] args) {

                if (args.length < 3) {
                        System.out.println(
                                        "Usage: java NodeServer <NodeName> <NodeId> <Port>");
                        return;
                }

                String nodeName = args[0];
                String nodeId = args[1];
                int port = Integer.parseInt(args[2]);

                try {

                        System.out.println();
                        System.out.println("==============================================");
                        System.out.println("      TECHNOVA DISTRIBUTED TASK SYSTEM");
                        System.out.println("==============================================");

                        System.out.println("Starting Node...");
                        System.out.println("Node Name : " + nodeName);
                        System.out.println("Node ID   : " + nodeId);
                        System.out.println("Port      : " + port);

                        // ------------------------------------------
                        // START RMI REGISTRY
                        // ------------------------------------------

                        Registry registry =
                                        LocateRegistry.createRegistry(port);

                        System.out.println(
                                        "[RMI] Registry started on port " + port);

                        // ------------------------------------------
                        // TASK SERVICE
                        // ------------------------------------------

                        TaskService taskService =
                                        new TaskServiceImpl(
                                                        nodeName,
                                                        nodeId);

                        registry.rebind(
                                        "TaskService",
                                        taskService);

                        System.out.println(
                                        "[RMI] TaskService registered.");

                        // ------------------------------------------
                        // CLOCK SERVICE
                        // ------------------------------------------

                        ClockService clockService =
                                        new ClockServiceImpl(
                                                        nodeName,
                                                        0L);

                        registry.rebind(
                                        "ClockService",
                                        clockService);

                        System.out.println(
                                        "[RMI] ClockService registered.");

                        // ------------------------------------------
                        // ELECTION SERVICE
                        // ------------------------------------------

                        ElectionService electionService =
                                        new ElectionServiceImpl(
                                                        nodeId,
                                                        nodeName);

                        registry.rebind(
                                        "ElectionService",
                                        electionService);

                        System.out.println(
                                        "[RMI] ElectionService registered.");

                        // ------------------------------------------
                        // DATA SERVICE
                        // ------------------------------------------

                        DataService dataService =
                                        new DataServiceImpl();

                        registry.rebind(
                                        "DataService",
                                        dataService);

                        System.out.println(
                                        "[RMI] DataService registered.");

                        // ------------------------------------------
                        // NODE INFORMATION
                        // ------------------------------------------

                        System.out.println();
                        System.out.println("----------------------------------------------");
                        System.out.println("Node is READY");
                        System.out.println("----------------------------------------------");

                        System.out.println(
                                        "Node Name : " + nodeName);

                        System.out.println(
                                        "Node ID   : " + nodeId);

                        System.out.println(
                                        "RMI Port  : " + port);

                        System.out.println();
                        System.out.println("Registered Services:");
                        System.out.println("  [OK] TaskService");
                        System.out.println("  [OK] ClockService");
                        System.out.println("  [OK] ElectionService");
                        System.out.println("  [OK] DataService");

                        System.out.println();
                        System.out.println(
                                        "Node " + nodeName +
                                                        " is waiting for requests...");

                        System.out.println(
                                        "==============================================");

                } catch (Exception e) {

                        System.err.println();
                        System.err.println(
                                        "[ERROR] Failed to start node "
                                                        + nodeName);

                        e.printStackTrace();
                }
        }
}