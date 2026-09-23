import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BullyElectionClient {

    // Node information
    static String[] nodeNames = {
            "Arjun",
            "Sarthak",
            "Amar",
            "Kaner",
            "Jogi"
    };

    static String[] nodeIds = {
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

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println(
                    "Usage: java BullyElectionClient <NodeID>");
            System.out.println(
                    "Example: java BullyElectionClient NODE-03");
            return;
        }

        String initiatorId = args[0];

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          BULLY ELECTION ALGORITHM");
        System.out.println("==============================================");

        System.out.println(
                "Election initiated by: " + initiatorId);

        int initiatorIndex = findNode(initiatorId);

        if (initiatorIndex == -1) {

            System.out.println(
                    "ERROR: Node " + initiatorId +
                            " does not exist.");

            return;
        }

        startElection(initiatorIndex);

        System.out.println();
        System.out.println("==============================================");
        System.out.println("              ELECTION COMPLETE");
        System.out.println("==============================================");
    }

    static void startElection(int initiatorIndex) {

        String initiatorId = nodeIds[initiatorIndex];

        String initiatorName = nodeNames[initiatorIndex];

        System.out.println();
        System.out.println(
                initiatorName +
                        " (" + initiatorId + ") starts election.");

        boolean higherNodeAlive = false;

        /*
         * Bully Algorithm:
         *
         * Contact every node having
         * a higher ID than the initiator.
         */

        for (int i = initiatorIndex + 1; i < nodeIds.length; i++) {

            System.out.println();
            System.out.println(
                    initiatorName +
                            " sends ELECTION to " +
                            nodeNames[i] +
                            " (" + nodeIds[i] + ")");

            try {

                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        ports[i]);

                ElectionService electionService = (ElectionService) registry.lookup(
                        "ElectionService");

                // Check whether node is alive
                boolean alive = electionService.isAlive();

                if (alive) {

                    higherNodeAlive = true;

                    System.out.println(
                            nodeNames[i] +
                                    " (" + nodeIds[i] +
                                    ") responds: OK");

                    /*
                     * Higher node now starts
                     * its own election.
                     */

                    startElection(i);

                    return;
                }

            } catch (Exception e) {

                System.out.println(
                        nodeNames[i] +
                                " (" + nodeIds[i] +
                                ") is NOT responding.");
            }
        }

        /*
         * If no higher node is alive,
         * this node becomes coordinator.
         */

        if (!higherNodeAlive) {

            announceCoordinator(
                    initiatorIndex);
        }
    }

    static void announceCoordinator(
            int coordinatorIndex) {

        String coordinatorId = nodeIds[coordinatorIndex];

        String coordinatorName = nodeNames[coordinatorIndex];

        System.out.println();
        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "No higher active node found.");

        System.out.println();

        System.out.println(
                "NEW COORDINATOR:");

        System.out.println(
                coordinatorName +
                        " (" + coordinatorId + ")");

        System.out.println(
                "----------------------------------------------");

        /*
         * Inform all active nodes.
         */

        for (int i = 0; i < nodeIds.length; i++) {

            if (i == coordinatorIndex) {
                continue;
            }

            try {

                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        ports[i]);

                ElectionService electionService = (ElectionService) registry.lookup(
                        "ElectionService");

                electionService.coordinatorMessage(
                        coordinatorId);

                System.out.println(
                        "Coordinator message sent to " +
                                nodeNames[i]);

            } catch (Exception e) {

                System.out.println(
                        "Could not notify " +
                                nodeNames[i] +
                                " - node unavailable.");
            }
        }

        System.out.println();
        System.out.println(
                "Coordinator election finished.");
    }

    static int findNode(String nodeId) {

        for (int i = 0; i < nodeIds.length; i++) {

            if (nodeIds[i].equalsIgnoreCase(nodeId)) {
                return i;
            }
        }

        return -1;
    }
}