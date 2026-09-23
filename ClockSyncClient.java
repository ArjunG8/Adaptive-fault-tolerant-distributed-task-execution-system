import java.util.ArrayList;
import java.util.List;

public class ClockSyncClient {

    public static void main(String[] args) {

        List<ClockSynchronizer.NodeInfo> nodes = new ArrayList<>();

        nodes.add(
                new ClockSynchronizer.NodeInfo(
                        "Arjun",
                        1099));

        nodes.add(
                new ClockSynchronizer.NodeInfo(
                        "Sarthak",
                        1100));

        nodes.add(
                new ClockSynchronizer.NodeInfo(
                        "Amar",
                        1101));

        nodes.add(
                new ClockSynchronizer.NodeInfo(
                        "Kaner",
                        1102));

        nodes.add(
                new ClockSynchronizer.NodeInfo(
                        "Jogi",
                        1103));

        ClockSynchronizer synchronizer = new ClockSynchronizer(nodes);

        synchronizer.synchronize();
    }
}