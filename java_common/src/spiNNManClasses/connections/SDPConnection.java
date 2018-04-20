package spiNNManClasses.connections;

import java.net.DatagramPacket;
import java.net.SocketException;

import spiNNManClasses.messages.SDPMessage;

public class SDPConnection extends UDPConnection {
    
    protected int chipX;
    protected int chipY;
    
    public SDPConnection(
            int remotePort, String remoteHost, int timeOut, int chipX, 
            int chipY)
            throws SocketException {
        this(0, null, remotePort, remoteHost, timeOut, chipX, chipY);
    }

    public SDPConnection(
            String local_host, int remote_port, String remote_host,
            int time_out, int chipX, int chipY) throws SocketException {
        this(0, local_host, remote_port, remote_host, time_out, chipX, chipY);
    }

    public SDPConnection(
            int local_port, int remote_port, String remote_host,
            int time_out, int chipX, int chipY) throws SocketException {
        this(local_port, null, remote_port, remote_host, time_out, chipX, 
             chipY);
    }

    // The real constructor
    public SDPConnection(
            int localPort, String localHost, int remotePort,
            String remoteHost, int timeOut, int chipX, int chipY) 
            throws SocketException {
        /**
         * 
        @param chipX: The optional x-coordinate of the chip at the remote\
            end of the connection. If not specified, it will not be possible\
            to send SDP messages that require a response with this connection.
        @param chipY: The optional y-coordinate of the chip at the remote\
            end of the connection. If not specified, it will not be possible\
            to send SDP messages that require a response with this connection.
        @param localHost: The optional IP address or host name of the local\
            interface to listen on
        @param localPort: The optional local port to listen on
        @param remoteHost: The optional remote host name or IP address to\
            send messages to. If not specified, sending will not be possible\
            using this connection
        @param remotePort: The optional remote port number to send messages\
            to. If not specified, sending will not be possible using this\
            connection
        """
         */
        super(localPort, localHost, remotePort, remoteHost, timeOut);
        this.chipX = chipX;
        this.chipY = chipY;
    }    

    public void send(SDPMessage message) {
        Utils.updateSdpHeaderForUdpSend(
            message.getHeader(), this.chipX, this.chipY);
        sendData(message.convertToByteArray(), message.lengthInBytes());
    }

    public DatagramPacket receive() {
        return receiveData(300);
    }
}
