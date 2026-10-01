import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("TECHNOVA DISTRIBUTED TASK SYSTEM");
            System.out.println("Node Name : " + nodeName);
            System.out.println("Node ID   : " + nodeId);
            System.out.println("RMI Port  : " + port);

            Registry registry =
                    LocateRegistry.createRegistry(port);

            TaskService taskService =
                    new TaskServiceImpl(nodeName, nodeId);

            registry.rebind(
                    "TaskService",
                    taskService);

            System.out.println(
                    "[RMI] TaskService registered.");

            if (nodeId.equals("NODE-01")) {

                List<CoordinatorServiceImpl.WorkerInfo> workers =
                        new ArrayList<>();

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-01",
                                "Arjun",
                                1099));

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-02",
                                "Sarthak",
                                1100));

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-03",
                                "Amar",
                                1101));

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-04",
                                "Kaner",
                                1102));

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-05",
                                "Jogi",
                                1103));

                CoordinatorService coordinator =
                        new CoordinatorServiceImpl(workers);

                registry.rebind(
                        "CoordinatorService",
                        coordinator);

                System.out.println(
                        "[RMI] CoordinatorService registered.");
            }

            ClockService clockService =
                    new ClockServiceImpl(
                            nodeName,
                            0L);

            registry.rebind(
                    "ClockService",
                    clockService);

            ElectionService electionService =
                    new ElectionServiceImpl(
                            nodeId,
                            nodeName);

            registry.rebind(
                    "ElectionService",
                    electionService);

            DataService dataService =
                    new DataServiceImpl();

            registry.rebind(
                    "DataService",
                    dataService);

            LoadBalanceService loadBalanceService =
                    createLoadBalanceService(
                            nodeId,
                            nodeName);

            registry.rebind(
                    "LoadBalanceService",
                    loadBalanceService);

            System.out.println();
            System.out.println(
                    "Node is ready.");
            System.out.println(
                    "Registered services:");
            System.out.println(
                    " - TaskService");
            System.out.println(
                    " - ClockService");
            System.out.println(
                    " - ElectionService");
            System.out.println(
                    " - DataService");
            System.out.println(
                    " - LoadBalanceService");

            if (nodeId.equals("NODE-01")) {
                System.out.println(
                        " - CoordinatorService");
            }

            System.out.println();

        } catch (Exception e) {

            System.out.println(
                    "Server exception: "
                            + e);

            e.printStackTrace();
        }
    }

    static LoadBalanceService createLoadBalanceService(
            String nodeId,
            String nodeName)
            throws Exception {

        switch (nodeId) {

            case "NODE-01":
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        5,
                        100,
                        100,
                        0.25);

            case "NODE-02":
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        4,
                        100,
                        75,
                        0.60);

            case "NODE-03":
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        6,
                        120,
                        90,
                        0.35);

            case "NODE-04":
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        3,
                        80,
                        60,
                        0.45);

            case "NODE-05":
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        5,
                        110,
                        80,
                        0.70);

            default:
                return new LoadBalanceServiceImpl(
                        nodeId,
                        nodeName,
                        4,
                        100,
                        75,
                        0.50);
        }
    }
}