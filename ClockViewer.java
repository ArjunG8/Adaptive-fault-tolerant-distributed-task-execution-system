import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClockViewer {

    public static void main(String[] args) {

        String[][] nodes = {

                { "Arjun", "1099" },
                { "Sarthak", "1100" },
                { "Amar", "1101" },
                { "Kaner", "1102" },
                { "Jogi", "1103" }
        };

        System.out.println();
        System.out.println(
                "==========================================");

        System.out.println(
                "     TECHNOVA CLOCK STATUS");

        System.out.println(
                "==========================================");

        for (String[] node : nodes) {

            String name = node[0];

            int port = Integer.parseInt(node[1]);

            try {

                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        port);

                ClockService clock = (ClockService) registry.lookup(
                        "ClockService");

                long time = clock.getCurrentTime();

                long offset = clock.getClockOffset();

                System.out.println();

                System.out.println(
                        name);

                System.out.println(
                        "  Logical Time : "
                                + time);

                System.out.println(
                        "  Offset       : "
                                + offset
                                + " ms");

            } catch (Exception e) {

                System.out.println();

                System.out.println(
                        name
                                + " -> UNAVAILABLE");
            }
        }

        System.out.println();
        System.out.println(
                "==========================================");
    }
}