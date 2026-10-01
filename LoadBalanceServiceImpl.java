import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoadBalanceServiceImpl
        extends UnicastRemoteObject
        implements LoadBalanceService {

    private final String nodeId;
    private final String nodeName;

    private final int maxTasks;
    private final int totalEnergy;
    private final int processingPower;
    private final double predictedLoad;

    private int activeTasks;
    private int availableEnergy;

    private boolean online;

    public LoadBalanceServiceImpl(
            String nodeId,
            String nodeName,
            int maxTasks,
            int totalEnergy,
            int processingPower,
            double predictedLoad)
            throws RemoteException {

        super();

        this.nodeId = nodeId;
        this.nodeName = nodeName;

        this.maxTasks = maxTasks;
        this.totalEnergy = totalEnergy;
        this.processingPower = processingPower;
        this.predictedLoad = predictedLoad;

        this.activeTasks = 0;
        this.availableEnergy = totalEnergy;

        this.online = true;
    }

    @Override
    public synchronized String getNodeId()
            throws RemoteException {

        return nodeId;
    }

    @Override
    public synchronized String getNodeName()
            throws RemoteException {

        return nodeName;
    }

    @Override
    public synchronized int getActiveTasks()
            throws RemoteException {

        return activeTasks;
    }

    @Override
    public synchronized int getMaxTasks()
            throws RemoteException {

        return maxTasks;
    }

    @Override
    public synchronized int getTotalEnergy()
            throws RemoteException {

        return totalEnergy;
    }

    @Override
    public synchronized int getAvailableEnergy()
            throws RemoteException {

        return availableEnergy;
    }

    @Override
    public synchronized int getProcessingPower()
            throws RemoteException {

        return processingPower;
    }

    @Override
    public synchronized double getPredictedLoad()
            throws RemoteException {

        return predictedLoad;
    }

    @Override
    public synchronized boolean isOnline()
            throws RemoteException {

        return online;
    }

    @Override
    public synchronized String allocateTask(
            String taskId,
            String taskName,
            int demand)
            throws RemoteException {

        if (!online) {
            return "OFFLINE";
        }

        if (activeTasks >= maxTasks) {
            return "NO_TASK_SLOT";
        }

        if (availableEnergy < demand) {
            return "INSUFFICIENT_ENERGY";
        }

        int oldEnergy = availableEnergy;

        activeTasks++;

        availableEnergy -= demand;

        String provisioning = "NONE";

        /*
         * Adaptive energy provisioning.
         *
         * When remaining energy falls to 30% or below,
         * additional energy is provisioned.
         */
        if (((double) availableEnergy / totalEnergy) <= 0.30) {

            int provision = (int) Math.round(totalEnergy * 0.20);

            int beforeProvision = availableEnergy;

            availableEnergy = Math.min(
                    totalEnergy,
                    availableEnergy + provision);

            int actualProvision = availableEnergy - beforeProvision;

            provisioning = "PROVISIONED +"
                    + actualProvision
                    + " ENERGY ("
                    + beforeProvision
                    + " -> "
                    + availableEnergy
                    + ")";
        }

        return "SUCCESS | "
                + taskId
                + " | "
                + taskName
                + " | demand="
                + demand
                + " | energy="
                + oldEnergy
                + "->"
                + availableEnergy
                + " | "
                + provisioning;
    }

    @Override
    public synchronized void resetNode()
            throws RemoteException {

        activeTasks = 0;

        availableEnergy = totalEnergy;

        online = true;
    }
}