
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RingElectionClient {

    // Node information in ring order
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

    static int[] processIds = {
            1, 2, 3, 4, 5
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
                    "Usage: java RingElectionClient <NodeID>");

            System.out.println(
                    "Example: java RingElectionClient NODE-02");

            return;
        }

        String initiatorId = args[0];

        int initiatorIndex = findNode(initiatorId);

        if (initiatorIndex == -1) {

            System.out.println(
                    "ERROR: Node " + initiatorId +
                            " does not exist.");

            return;
        }

        if (!isNodeActive(initiatorIndex)) {

            System.out.println(
                    "ERROR: Initiator node is offline.");

            return;
        }

        System.out.println();
        System.out.println("==============================================");
        System.out.println("           RING ELECTION ALGORITHM");
        System.out.println("==============================================");

        System.out.println(
                "Election Initiator = " +
                        nodeNames[initiatorIndex] +
                        " (ID:" +
                        processIds[initiatorIndex] +
                        ")");

        startElection(initiatorIndex);

    }

    static void startElection(int initiatorIndex) {

        List<Integer> token = new ArrayList<>();

        token.add(processIds[initiatorIndex]);

        int currentIndex = initiatorIndex;

        System.out.println();
        System.out.println("--- Election Token Starts ---");

        System.out.println(
                nodeNames[currentIndex] +
                        " starts election");

        printToken(token);

        while (true) {

            int nextIndex = findNextActiveNode(currentIndex);

            if (nextIndex == -1) {

                System.out.println(
                        "No active nodes are available.");

                return;
            }

            System.out.println();

            System.out.println(
                    nodeNames[currentIndex] +
                            "(" + processIds[currentIndex] +
                            ") -> " +
                            nodeNames[nextIndex] +
                            "(" + processIds[nextIndex] +
                            ")");

            if (nextIndex == initiatorIndex) {

                System.out.println(
                        "Token returned to initiator " +
                                nodeNames[initiatorIndex] +
                                ".");

                break;
            }

            if (isNodeActive(nextIndex)) {

                token.add(processIds[nextIndex]);

                System.out.println(
                        nodeNames[nextIndex] +
                                " adds ID " +
                                processIds[nextIndex]);

                printToken(token);

                currentIndex = nextIndex;

            } else {

                System.out.println(
                        nodeNames[nextIndex] +
                                " is OFFLINE -> SKIPPED");

                currentIndex = nextIndex;
            }
        }

        announceCoordinator(token);

    }

    static int findNextActiveNode(int currentIndex) {

        for (int step = 1; step <= nodeIds.length; step++) {

            int nextIndex = (currentIndex + step) % nodeIds.length;

            if (nextIndex == currentIndex) {
                continue;
            }

            if (isNodeActive(nextIndex)) {
                return nextIndex;
            }

            System.out.println();

            System.out.println(
                    nodeNames[currentIndex] +
                            "(" + processIds[currentIndex] +
                            ") -> " +
                            nodeNames[nextIndex] +
                            "(" + processIds[nextIndex] +
                            ")");

            System.out.println(
                    nodeNames[nextIndex] +
                            " is OFFLINE -> SKIPPED");

            currentIndex = nextIndex;
        }

        return -1;
    }

    static boolean isNodeActive(int index) {

        try {

            Registry registry = LocateRegistry.getRegistry(
                    "localhost",
                    ports[index]);

            ElectionService electionService = (ElectionService) registry.lookup(
                    "ElectionService");

            return electionService.isAlive();

        } catch (Exception e) {

            return false;
        }
    }

    static void announceCoordinator(List<Integer> token) {

        int maximumProcessId = -1;

        int coordinatorIndex = -1;

        for (int processId : token) {

            if (processId > maximumProcessId) {

                maximumProcessId = processId;

                coordinatorIndex = findProcessIndex(processId);
            }
        }

        System.out.println();

        System.out.println(
                "Final Token = " + token);

        System.out.println(
                "Maximum Process ID = " +
                        maximumProcessId);

        if (coordinatorIndex == -1) {

            System.out.println(
                    "Coordinator could not be identified.");

            return;
        }

        System.out.println();

        System.out.println(
                nodeNames[coordinatorIndex] +
                        " becomes the NEW COORDINATOR.");

        notifyActiveNodes(
                nodeIds[coordinatorIndex]);

    }

    static void notifyActiveNodes(String coordinatorId) {

        System.out.println();

        System.out.println(
                "Sending coordinator notification...");

        for (int i = 0; i < nodeIds.length; i++) {

            if (!isNodeActive(i)) {
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
                                nodeNames[i]);
            }
        }

        System.out.println();

        System.out.println(
                "Ring election completed successfully.");

        System.out.println(
                "==============================================");

    }

    static int findNode(String nodeId) {

        for (int i = 0; i < nodeIds.length; i++) {

            if (nodeIds[i].equalsIgnoreCase(nodeId)) {
                return i;
            }
        }

        return -1;
    }

    static int findProcessIndex(int processId) {

        for (int i = 0; i < processIds.length; i++) {

            if (processIds[i] == processId) {
                return i;
            }
        }

        return -1;
    }

    static void printToken(List<Integer> token) {

        System.out.println(
                "Token = " + token);
    }
}
