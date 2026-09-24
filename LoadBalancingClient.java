import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoadBalancingClient {

    static final String[] NAMES = {
            "Arjun", "Sarthak", "Amar", "Kaner", "Jogi"
    };

    static final String[] IDS = {
            "NODE-01", "NODE-02", "NODE-03", "NODE-04", "NODE-05"
    };

    static final int[] PORTS = {
            1099, 1100, 1101, 1102, 1103
    };

    // TECHNOVA workload
    static final String[] TASK_IDS = {
            "T-201", "T-202", "T-203", "T-204",
            "T-205", "T-206", "T-207", "T-208",
            "T-209", "T-210", "T-211", "T-212"
    };

    static final String[] TASK_NAMES = {
            "Build TECHNOVA Release",
            "Process User Registration",
            "Generate Project Report",
            "Run Database Backup",
            "Validate Deployment",
            "Process Task Update",
            "Generate Analytics Report",
            "Run Security Scan",
            "Deploy TECHNOVA Web Application",
            "Sync Task Records",
            "Build API Package",
            "Generate Deployment Report"
    };

    // Resource demand of each task
    static final int[] DEMANDS = {
            15, 25, 20, 30,
            10, 25, 20, 15,
            60, 20, 50, 35
    };

    static final List<Node> nodes = new ArrayList<>();
    static int roundRobinPointer = 0;

    static class Node {
        String name;
        String id;
        int port;
        LoadBalanceService service;

        Node(String name, String id, int port,
                LoadBalanceService service) {
            this.name = name;
            this.id = id;
            this.port = port;
            this.service = service;
        }
    }

    static void line() {
        System.out.println(
                "==================================================");
    }

    // Connect to all available RMI nodes
    static void connectNodes() {
        nodes.clear();

        for (int i = 0; i < NAMES.length; i++) {
            try {
                Registry registry = LocateRegistry.getRegistry(
                        "localhost", PORTS[i]);

                LoadBalanceService service = (LoadBalanceService) registry.lookup(
                        "LoadBalanceService");

                nodes.add(
                        new Node(
                                NAMES[i],
                                IDS[i],
                                PORTS[i],
                                service));

            } catch (Exception e) {
                System.out.println(
                        NAMES[i] + " (" + IDS[i] + ") -> OFFLINE");
            }
        }
    }

    // Reset node workload and energy
    static void resetNodes() {
        for (Node node : nodes) {
            try {
                node.service.resetNode();
            } catch (Exception ignored) {
            }
        }

        roundRobinPointer = 0;
    }

    static boolean eligible(
            LoadBalanceService service,
            int demand) {

        try {
            return service.isOnline()
                    && service.getActiveTasks() < service.getMaxTasks()
                    && service.getAvailableEnergy() >= demand;

        } catch (Exception e) {
            return false;
        }
    }

    // Display current node resources
    static void showNodeStatus() {
        line();
        System.out.println("TECHNOVA NODE STATUS");
        line();

        System.out.printf(
                "%-10s %-10s %-8s %-8s %-12s %-10s%n",
                "Node", "ID", "Tasks",
                "Max", "Energy", "Power");

        System.out.println(
                "--------------------------------------------------");

        for (Node node : nodes) {
            try {
                System.out.printf(
                        "%-10s %-10s %-8d %-8d %-12s %-10d%n",
                        node.name,
                        node.id,
                        node.service.getActiveTasks(),
                        node.service.getMaxTasks(),
                        node.service.getAvailableEnergy()
                                + "/"
                                + node.service.getTotalEnergy(),
                        node.service.getProcessingPower());

            } catch (Exception e) {
                System.out.printf(
                        "%-10s %-10s OFFLINE%n",
                        node.name, node.id);
            }
        }

        line();
    }

    // Round Robin algorithm
    static void roundRobin() {
        line();
        System.out.println("ROUND ROBIN");
        line();

        resetNodes();

        for (int i = 0; i < TASK_IDS.length; i++) {

            if (nodes.isEmpty()) {
                System.out.println("No active nodes.");
                return;
            }

            Node selected = nodes.get(roundRobinPointer);

            roundRobinPointer = (roundRobinPointer + 1)
                    % nodes.size();

            if (!eligible(
                    selected.service,
                    DEMANDS[i])) {

                try {
                    System.out.println(
                            TASK_IDS[i]
                                    + " | "
                                    + TASK_NAMES[i]
                                    + " | demand="
                                    + DEMANDS[i]
                                    + " -> "
                                    + selected.name
                                    + " | REJECTED"
                                    + " | energy="
                                    + selected.service.getAvailableEnergy()
                                    + "/"
                                    + selected.service.getTotalEnergy());
                } catch (Exception e) {
                    System.out.println(
                            TASK_IDS[i] + " | REJECTED");
                }

                continue;
            }

            try {
                String result = selected.service.allocateTask(
                        TASK_IDS[i],
                        TASK_NAMES[i],
                        DEMANDS[i]);

                System.out.println(
                        TASK_IDS[i]
                                + " | "
                                + TASK_NAMES[i]
                                + " | demand="
                                + DEMANDS[i]
                                + " -> "
                                + selected.name
                                + " | "
                                + result.split(" \\| ")[0]);

            } catch (Exception e) {
                System.out.println(
                        TASK_IDS[i] + " -> ERROR");
            }
        }

        System.out.println();
        showNodeStatus();
    }

    // Least Load algorithm
    static void leastLoad() {
        line();
        System.out.println("LEAST LOAD");
        line();

        resetNodes();

        for (int i = 0; i < TASK_IDS.length; i++) {

            Node selected = null;
            int lowestLoad = Integer.MAX_VALUE;

            for (Node node : nodes) {
                try {
                    if (!eligible(
                            node.service,
                            DEMANDS[i])) {
                        continue;
                    }

                    int load = node.service.getActiveTasks();

                    if (load < lowestLoad) {
                        lowestLoad = load;
                        selected = node;
                    }

                } catch (Exception ignored) {
                }
            }

            if (selected == null) {
                System.out.println(
                        TASK_IDS[i]
                                + " | demand="
                                + DEMANDS[i]
                                + " | REJECTED");
                continue;
            }

            try {
                String result = selected.service.allocateTask(
                        TASK_IDS[i],
                        TASK_NAMES[i],
                        DEMANDS[i]);

                System.out.println(
                        TASK_IDS[i]
                                + " | "
                                + TASK_NAMES[i]
                                + " | demand="
                                + DEMANDS[i]
                                + " -> "
                                + selected.name
                                + " | "
                                + result.split(" \\| ")[0]);

            } catch (Exception e) {
                System.out.println(
                        TASK_IDS[i] + " -> ERROR");
            }
        }

        System.out.println();
        showNodeStatus();
    }

    // Select node using AEAP weighted score
    static Node findAdaptiveNode(
            int demand,
            double[] scoreHolder) {

        Node best = null;
        double bestScore = -1.0;

        for (Node node : nodes) {
            try {
                if (!eligible(
                        node.service,
                        demand)) {
                    continue;
                }

                double energyHeadroom = (double) node.service.getAvailableEnergy()
                        / node.service.getTotalEnergy();

                double slotAvailability = 1.0
                        - ((double) node.service.getActiveTasks()
                                / node.service.getMaxTasks());

                double speedScore = (double) node.service.getProcessingPower()
                        / 100.0;

                double forecastAvailability = 1.0
                        - node.service.getPredictedLoad();

                // AEAP weights from the reference experiment
                double score = (0.35 * energyHeadroom)
                        + (0.25 * slotAvailability)
                        + (0.20 * speedScore)
                        + (0.20 * forecastAvailability);

                if (score > bestScore) {
                    bestScore = score;
                    best = node;
                }

            } catch (Exception ignored) {
            }
        }

        scoreHolder[0] = bestScore;
        return best;
    }

    // Adaptive Energy-Aware Provisioning
    static void adaptiveEnergy() {
        line();
        System.out.println(
                "ADAPTIVE ENERGY-AWARE PROVISIONING");
        line();

        resetNodes();

        for (int i = 0; i < TASK_IDS.length; i++) {

            double[] score = { -1.0 };

            Node selected = findAdaptiveNode(
                    DEMANDS[i],
                    score);

            if (selected == null) {
                System.out.println(
                        TASK_IDS[i]
                                + " | demand="
                                + DEMANDS[i]
                                + " | REJECTED");
                continue;
            }

            try {
                int energyBefore = selected.service.getAvailableEnergy();

                String result = selected.service.allocateTask(
                        TASK_IDS[i],
                        TASK_NAMES[i],
                        DEMANDS[i]);

                int energyAfter = selected.service.getAvailableEnergy();

                System.out.printf(
                        "%s | %s | demand=%d -> %s | score=%.3f | energy=%d->%d%n",
                        TASK_IDS[i],
                        TASK_NAMES[i],
                        DEMANDS[i],
                        selected.name,
                        score[0],
                        energyBefore,
                        energyAfter);

                if (result.contains("PROVISIONED")) {
                    System.out.println(
                            "    "
                                    + result.substring(
                                            result.indexOf(
                                                    "PROVISIONED")));
                }

            } catch (Exception e) {
                System.out.println(
                        TASK_IDS[i] + " -> ERROR");
            }
        }

        System.out.println();
        showNodeStatus();
    }

    // Run the same workload using all three algorithms
    static void compareAlgorithms() {
        line();
        System.out.println("ALGORITHM COMPARISON");
        line();

        System.out.println(
                "Same TECHNOVA workload is executed three times.");

        System.out.println();

        runCompactComparison("Round Robin");
        runCompactComparison("Least Load");
        runCompactComparison("Adaptive Energy");

        System.out.println();
        System.out.println("Comparison completed.");
    }

    static void runCompactComparison(
            String algorithm) {

        resetNodes();

        int success = 0;
        int rejected = 0;
        int totalAssignedDemand = 0;

        for (int i = 0; i < TASK_IDS.length; i++) {

            Node selected = null;

            if (algorithm.equals("Round Robin")) {

                selected = nodes.get(roundRobinPointer);

                roundRobinPointer = (roundRobinPointer + 1)
                        % nodes.size();

            } else if (algorithm.equals("Least Load")) {

                int lowest = Integer.MAX_VALUE;

                for (Node node : nodes) {
                    if (!eligible(
                            node.service,
                            DEMANDS[i])) {
                        continue;
                    }

                    try {
                        int load = node.service.getActiveTasks();

                        if (load < lowest) {
                            lowest = load;
                            selected = node;
                        }

                    } catch (Exception ignored) {
                    }
                }

            } else {

                double[] score = { -1.0 };

                selected = findAdaptiveNode(
                        DEMANDS[i],
                        score);
            }

            if (selected == null) {
                rejected++;
                continue;
            }

            try {
                if (!eligible(
                        selected.service,
                        DEMANDS[i])) {
                    rejected++;
                    continue;
                }

                String result = selected.service.allocateTask(
                        TASK_IDS[i],
                        TASK_NAMES[i],
                        DEMANDS[i]);

                if (result.startsWith("SUCCESS")) {
                    success++;
                    totalAssignedDemand += DEMANDS[i];
                } else {
                    rejected++;
                }

            } catch (Exception e) {
                rejected++;
            }
        }

        System.out.printf(
                "%s -> assigned=%d, rejected=%d, demand=%d%n",
                algorithm,
                success,
                rejected,
                totalAssignedDemand);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        connectNodes();

        if (nodes.isEmpty()) {
            System.out.println(
                    "No TECHNOVA nodes are available.");
            return;
        }

        while (true) {

            line();

            System.out.println(
                    "TECHNOVA DISTRIBUTED TASK SYSTEM");

            System.out.println(
                    "LOAD BALANCING AND ENERGY PROVISIONING");

            line();

            System.out.println();
            System.out.println("1. Show Node Status");
            System.out.println("2. Round Robin");
            System.out.println("3. Least Load");
            System.out.println(
                    "4. Adaptive Energy-Aware Provisioning");
            System.out.println("5. Compare Algorithms");
            System.out.println("6. Reset Nodes");
            System.out.println("7. Exit");

            System.out.print(
                    "Enter your choice: ");

            int choice;

            try {
                choice = scanner.nextInt();

            } catch (Exception e) {

                scanner.nextLine();

                System.out.println(
                        "Invalid input.");

                continue;
            }

            System.out.println();

            switch (choice) {

                case 1:
                    showNodeStatus();
                    break;

                case 2:
                    roundRobin();
                    break;

                case 3:
                    leastLoad();
                    break;

                case 4:
                    adaptiveEnergy();
                    break;

                case 5:
                    compareAlgorithms();
                    break;

                case 6:
                    resetNodes();
                    System.out.println(
                            "All available nodes reset.");
                    break;

                case 7:
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