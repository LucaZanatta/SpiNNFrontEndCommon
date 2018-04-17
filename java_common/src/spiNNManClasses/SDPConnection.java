package spiNNManClasses;

import java.net.DatagramPacket;
import java.net.SocketException;

import commonClasses.UDPConnection;

public class SDPConnection extends UDPConnection {
    public SDPConnection(
            int remote_port, String remote_host, int time_out)
            throws SocketException {
        this(0, null, remote_port, remote_host, time_out);
    }

    public SDPConnection(
            String local_host, int remote_port, String remote_host,
            int time_out) throws SocketException {
        this(0, local_host, remote_port, remote_host, time_out);
    }

    public SDPConnection(
            int local_port, int remote_port, String remote_host,
            int time_out) throws SocketException {
        this(local_port, null, remote_port, remote_host, time_out);
    }

    // The real constructor
    public SDPConnection(
            int local_port, String local_host, int remote_port,
            String remote_host, int time_out) throws SocketException {
        super(local_port, local_host, remote_port, remote_host, time_out);
    }    

    public void send(SDPMessage message) {
        sendData(message.convert_to_byte_array(), message.length_in_bytes());
    }

    public DatagramPacket receive() {
        return receiveData(300);
    }
}
