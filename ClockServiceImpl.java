import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClockServiceImpl
        extends UnicastRemoteObject
        implements ClockService {

    private final String nodeName;

    /*
     * Difference between the logical node clock
     * and the actual PC system clock.
     *
     * We NEVER modify the Windows system clock.
     */
    private long clockOffset;

    public ClockServiceImpl(
            String nodeName,
            long initialOffset)
            throws RemoteException {

        super();

        this.nodeName = nodeName;
        this.clockOffset = initialOffset;

        System.out.println(
                "[" + nodeName + "] Clock initialized");

        System.out.println(
                "[" + nodeName + "] Initial clock offset = "
                        + initialOffset
                        + " ms");
    }

    @Override
    public synchronized long getCurrentTime()
            throws RemoteException {

        /*
         * Logical Clock =
         *
         * System Clock + Node Offset
         */
        return System.currentTimeMillis()
                + clockOffset;
    }

    @Override
    public synchronized long getClockOffset()
            throws RemoteException {

        return clockOffset;
    }

    @Override
    public synchronized void adjustClock(
            long correction)
            throws RemoteException {

        long oldOffset = clockOffset;

        clockOffset += correction;

        System.out.println();
        System.out.println(
                "[" + nodeName + "] CLOCK ADJUSTMENT");

        System.out.println(
                "Previous offset : "
                        + oldOffset + " ms");

        System.out.println(
                "Correction      : "
                        + correction + " ms");

        System.out.println(
                "New offset      : "
                        + clockOffset + " ms");
    }

    @Override
    public String getNodeName()
            throws RemoteException {

        return nodeName;
    }
}