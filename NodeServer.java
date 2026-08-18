import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class NodeServer {

    public static void main(String[] args) {

        try {

            if (args.length < 3) {

                System.out.println(
                        "Usage: java NodeServer "
                                + "<nodeId> <nodeName> <port>");

                return;
            }

            String nodeId = args[0];
            String nodeName = args[1];
            int port = Integer.parseInt(args[2]);

            // --------------------------------------
            // Create Worker Service
            // --------------------------------------

            TaskService worker = new TaskServiceImpl(
                    nodeId,
                    nodeName);

            // --------------------------------------
            // Create RMI Registry
            // --------------------------------------

            Registry registry = LocateRegistry.createRegistry(port);

            // --------------------------------------
            // Register Worker Service
            // --------------------------------------

            registry.rebind(
                    "TaskService",
                    worker);

            System.out.println();
            System.out.println(
                    "======================================");

            System.out.println(
                    "NODE STARTED");

            System.out.println(
                    "Node ID   : " + nodeId);

            System.out.println(
                    "Node Name : " + nodeName);

            System.out.println(
                    "Port      : " + port);

            System.out.println(
                    "Role      : Worker");

            System.out.println(
                    "======================================");

            // --------------------------------------
            // NODE-01 becomes initial coordinator
            // --------------------------------------

            if (nodeId.equals("NODE-01")) {

                List<CoordinatorServiceImpl.WorkerInfo> workers = new ArrayList<>();

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

                CoordinatorService coordinator = new CoordinatorServiceImpl(workers);

                registry.rebind(
                        "CoordinatorService",
                        coordinator);

                System.out.println();
                System.out.println(
                        "******** INITIAL LEADER ********");

                System.out.println(
                        "Leader : Arjun");

                System.out.println(
                        "Node   : NODE-01");

                System.out.println(
                        "********************************");
            }

        } catch (Exception e) {

            System.err.println(
                    "Node exception: " + e);

            e.printStackTrace();
        }
    }
}