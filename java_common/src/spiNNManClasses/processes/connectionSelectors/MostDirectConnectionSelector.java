package spiNNManClasses.processes.connectionSelectors;

import spiNNManClasses.connections.UDPConnection;
import java.util.ArrayList;

/**
 * A selector that goes for the most direct connection for the message.
 * 
 * @author alan
 */
public class MostDirectConnectionSelector
        extends AbstractMultiConnectionProcessConnectionSelector {
    protected ArrayList<UDPConnection> connections = new ArrayList<>();

    protected UDPConnection firstConnection = null;

    private Object machine;

    public MostDirectConnectionSelector(
                ArrayList<UDPConnection> connections){
        for (UDPConnection connection : connections){
            if( connection.chip_x == 0 and connection.chip_y == 0) {
                firstConnection = connection;
            }
            connections.set(
                              (connection.chip_x, connection.chip_y), connection);
        }
        if (firstConnection == null) {
            firstConnection = next(iter(connections));
        }
    }
    public void setMachine(Object machine) { // FIXME machine type
        this.machine = machine;
    }
    public UDPConnection getNextConnection(Object message) { // FIXME message type
        if (machine == null || len(connections) == 1){
            return firstConnection;
        }

        chip = machine.get_chip_at(
            message.sdp_header.destination_chip_x,
            message.sdp_header.destination_chip_y);
        key = (chip.nearest_ethernet_x, chip.nearest_ethernet_y);

        if (connections.get(key) == null) {
            return firstConnection;
        }
        return connections.get(key);
    }
}
