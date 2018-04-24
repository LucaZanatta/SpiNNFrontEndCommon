package spiNNManClasses.processes.connectionSelectors;

import spiNNManClasses.connections.UDPConnection;
import spiNNManClasses.messages.SCPRequest;

public abstract class AbstractMultiConnectionProcessConnectionSelector {
    
    public abstract UDPConnection getNextConnection(SCPRequest message);
    
}
