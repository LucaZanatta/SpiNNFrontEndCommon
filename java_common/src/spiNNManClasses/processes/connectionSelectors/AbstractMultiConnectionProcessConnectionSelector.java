package spiNNManClasses.processes.connectionSelectors;

import spiNNManClasses.messages.UDPMessage;

public abstract class AbstractMultiConnectionProcessConnectionSelector {
    
    public abstract int getNextConnection(UDPMessage message);
    
}
