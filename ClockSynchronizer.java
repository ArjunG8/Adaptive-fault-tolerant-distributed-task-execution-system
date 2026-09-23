import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ClockSynchronizer {

    private final List<NodeInfo> nodes;
    private int round = 0;

    public ClockSynchronizer(List<NodeInfo> nodes) {
        this.nodes = nodes;
    }

    public void synchronize() {

        round++;

        System.out.println("\n================================================");
        System.out.println("       BERKELEY CLOCK SYNCHRONIZATION");
        System.out.println("       Round " + round);
        System.out.println("================================================");

        List<NodeData> data = new ArrayList<>();

        // -------- BEFORE --------
        System.out.println("\nBEFORE SYNCHRONIZATION");
        System.out.println("-----------------------------------------------");

        for (NodeInfo n : nodes) {
            try {
                Registry r = LocateRegistry.getRegistry("localhost", n.port);
                ClockService c = (ClockService) r.lookup("ClockService");

                long start = System.currentTimeMillis();
                long time = c.getCurrentTime();
                long rtt = System.currentTimeMillis() - start;

                long offset = c.getClockOffset();

                data.add(new NodeData(n, time + rtt / 2, offset));

                System.out.printf(
                        "%-10s Offset: %+5d ms | RTT: %d ms%n",
                        n.name, offset, rtt);

            } catch (Exception e) {
                System.out.println(
                        n.name + " -> UNAVAILABLE");
            }
        }

        if (data.isEmpty()) {
            System.out.println("No nodes available.");
            return;
        }

        long beforeSkew = skew(data);

        System.out.println(
                "Maximum Clock Skew: "
                        + beforeSkew + " ms");

        // -------- BERKELEY CALCULATION --------
        long sum = 0;

        for (NodeData d : data)
            sum += d.time;

        long target = sum / data.size();

        System.out.println("\nBERKELEY CALCULATION");
        System.out.println("-----------------------------------------------");
        System.out.println("Common Target Time: " + target);

        for (NodeData d : data) {

            d.correction = target - d.time;

            System.out.printf(
                    "%-10s Correction: %+5d ms%n",
                    d.node.name,
                    d.correction);
        }

        // -------- APPLY CORRECTIONS --------
        for (NodeData d : data) {
            try {
                Registry r = LocateRegistry.getRegistry(
                        "localhost",
                        d.node.port);

                ClockService c = (ClockService) r.lookup("ClockService");

                c.adjustClock(d.correction);

            } catch (Exception e) {
                System.out.println(
                        "Could not adjust "
                                + d.node.name);
            }
        }

        // -------- AFTER --------
        System.out.println("\nAFTER SYNCHRONIZATION");
        System.out.println("-----------------------------------------------");

        List<NodeData> after = new ArrayList<>();

        for (NodeInfo n : nodes) {
            try {
                Registry r = LocateRegistry.getRegistry(
                        "localhost",
                        n.port);

                ClockService c = (ClockService) r.lookup("ClockService");

                long time = c.getCurrentTime();
                long offset = c.getClockOffset();

                after.add(
                        new NodeData(
                                n,
                                time,
                                offset));

                System.out.printf(
                        "%-10s Offset: %+5d ms | Time: %d%n",
                        n.name,
                        offset,
                        time);

            } catch (Exception e) {
                System.out.println(
                        n.name + " -> UNAVAILABLE");
            }
        }

        long afterSkew = skew(after);

        double improvement = beforeSkew == 0
                ? 0
                : ((double) (beforeSkew - afterSkew)
                        / beforeSkew) * 100;

        // -------- FINAL REPORT --------
        System.out.println("\n================================================");
        System.out.println("           SYNCHRONIZATION REPORT");
        System.out.println("================================================");

        System.out.println(
                "Round                  : " + round);

        System.out.println(
                "Before Maximum Skew    : "
                        + beforeSkew + " ms");

        System.out.println(
                "After Maximum Skew     : "
                        + afterSkew + " ms");

        System.out.printf(
                "Improvement             : %.2f%%%n",
                improvement);

        System.out.println(
                "================================================");
    }

    private long skew(List<NodeData> list) {

        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (NodeData d : list) {
            min = Math.min(min, d.offset);
            max = Math.max(max, d.offset);
        }

        return max - min;
    }

    public static class NodeInfo {

        String name;
        int port;

        public NodeInfo(
                String name,
                int port) {

            this.name = name;
            this.port = port;
        }
    }

    private static class NodeData {

        NodeInfo node;
        long time;
        long offset;
        long correction;

        NodeData(
                NodeInfo node,
                long time,
                long offset) {

            this.node = node;
            this.time = time;
            this.offset = offset;
        }
    }
}