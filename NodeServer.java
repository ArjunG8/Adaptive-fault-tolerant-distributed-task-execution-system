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

            Registry registry;

            try {

                registry =
                        LocateRegistry.createRegistry(port);

                System.out.println(
                        "RMI registry created on port "
                                + port);

            } catch (Exception e) {

                registry =
                        LocateRegistry.getRegistry(port);
            }

            /*
             * Task Service
             */
            TaskService taskService =
                    new TaskServiceImpl(
                            nodeId,
                            nodeName
                    );

            registry.rebind(
                    "TaskService",
                    taskService
            );

            /*
             * Clock Service
             */
            ClockService clockService =
                    new ClockServiceImpl(
                            nodeId,
                            System.currentTimeMillis()
                    );

            registry.rebind(
                    "ClockService",
                    clockService
            );

            /*
             * Election Service
             */
            ElectionService electionService =
                    new ElectionServiceImpl(
                            nodeId,
                            nodeName
                    );

            registry.rebind(
                    "ElectionService",
                    electionService
            );

            /*
             * Data Service
             */
            DataService dataService =
                    new DataServiceImpl();

            registry.rebind(
                    "DataService",
                    dataService
            );

            /*
             * Load Balance Service
             */
            int maxTasks;
            int totalEnergy;
            int processingPower;
            double predictedLoad;

            if (nodeId.equals("NODE-01")) {

                maxTasks = 5;
                totalEnergy = 100;
                processingPower = 100;
                predictedLoad = 0.25;

            } else if (nodeId.equals("NODE-02")) {

                maxTasks = 4;
                totalEnergy = 100;
                processingPower = 75;
                predictedLoad = 0.60;

            } else if (nodeId.equals("NODE-03")) {

                maxTasks = 6;
                totalEnergy = 120;
                processingPower = 90;
                predictedLoad = 0.35;

            } else if (nodeId.equals("NODE-04")) {

                maxTasks = 3;
                totalEnergy = 80;
                processingPower = 60;
                predictedLoad = 0.45;

            } else {

                maxTasks = 5;
                totalEnergy = 110;
                processingPower = 80;
                predictedLoad = 0.70;
            }

            LoadBalanceService loadBalanceService =
                    new LoadBalanceServiceImpl(
                            nodeId,
                            nodeName,
                            maxTasks,
                            totalEnergy,
                            processingPower,
                            predictedLoad
                    );

            registry.rebind(
                    "LoadBalanceService",
                    loadBalanceService
            );

            /*
             * Coordinator Service
             */
            if (nodeId.equals("NODE-01")) {

                java.util.List<CoordinatorServiceImpl.WorkerInfo> workers =
                        new java.util.ArrayList<>();

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-01",
                                "Arjun",
                                1099
                        )
                );

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-02",
                                "Sarthak",
                                1100
                        )
                );

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-03",
                                "Amar",
                                1101
                        )
                );

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-04",
                                "Kaner",
                                1102
                        )
                );

                workers.add(
                        new CoordinatorServiceImpl.WorkerInfo(
                                "NODE-05",
                                "Jogi",
                                1103
                        )
                );

                CoordinatorService coordinatorService =
                        new CoordinatorServiceImpl(
                                workers
                        );

                registry.rebind(
                        "CoordinatorService",
                        coordinatorService
                );
            }

            /*
             * Experiment 8
             *
             * Configuration is determined internally.
             * The startup command remains unchanged.
             */
            String serviceMode;
            int peerPort;

            if (nodeId.equals("NODE-01")) {

                serviceMode = "PRIMARY";
                peerPort = 1100;

            } else if (nodeId.equals("NODE-02")) {

                serviceMode = "BACKUP";
                peerPort = 1099;

            } else {

                serviceMode = "NORMAL";
                peerPort = 1099;
            }

            TaskStateService taskStateService =
                    new TaskStateServiceImpl(
                            nodeId,
                            serviceMode,
                            peerPort
                    );

            registry.rebind(
                    "TaskStateService",
                    taskStateService
            );

            /*
             * Startup information
             */
            System.out.println();

            System.out.println(
                    "Node ready: "
                            + nodeName
                            + " ("
                            + nodeId
                            + ")"
            );

            System.out.println(
                    "Distributed services registered successfully."
            );

            System.out.println(
                    "Node is ready to accept requests."
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to start node: "
                            + nodeName
            );

            e.printStackTrace();
        }
    }
}