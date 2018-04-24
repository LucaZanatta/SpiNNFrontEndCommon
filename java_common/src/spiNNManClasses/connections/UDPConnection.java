package spiNNManClasses.connections;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import spiNNManClasses.exceptions.SpinnmanIOException;

public class UDPConnection implements Connection {
    private final DatagramSocket sock;
    private boolean connected;

    // Simple passthrough constructors that allow sensible defaults
    public UDPConnection(
            int remote_port, String remote_host, int time_out)
            throws SocketException {
        this(0, null, remote_port, remote_host, time_out);
    }

    public UDPConnection(
            String local_host, int remote_port, String remote_host,
            int time_out) throws SocketException {
        this(0, local_host, remote_port, remote_host, time_out);
    }

    public UDPConnection(
            int local_port, int remote_port, String remote_host,
            int time_out) throws SocketException {
        this(local_port, null, remote_port, remote_host, time_out);
    }

    // The real constructor
    public UDPConnection(
            int local_port, String local_host, int remote_port,
            String remote_host, int time_out) throws SocketException {
        connected = false;
        if (local_host == null || local_host.isEmpty()) {
            sock = new DatagramSocket();
        } else {
            sock = new DatagramSocket(
                new InetSocketAddress(local_host, local_port));
        }
        sock.connect(new InetSocketAddress(remote_host, remote_port));
        connected = true;
        sock.setSoTimeout(time_out);
        sock.setReceiveBufferSize(1024 * 1024);
    }

    public boolean isClosed() {
        return sock.isClosed();
    }

    public ByteBuffer receiveBuffer(int maxLength) {
        DatagramPacket packet = receiveData(maxLength);
        if (packet == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(
            packet.getData(), packet.getOffset(), packet.getLength());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        return buffer;
    }

    public DatagramPacket receiveData(int maxLength) {
        return receiveData(new byte[maxLength], maxLength);
    }

    public DatagramPacket receiveData(byte[] data, int length) {
        DatagramPacket packet = new DatagramPacket(data, length);
        try {
            sock.receive(packet);
        } catch (SocketTimeoutException e) {
            packet.setLength(0);
        } catch (IOException e) {
            //System.out.println("failed to recieve packet");
            return null;
        }
        return packet;
    }

    public void sendData(byte[] data, int length) {
        DatagramPacket packet = new DatagramPacket(data, length);
        try {
            sock.send(packet);
        } catch (IOException e) {
            System.out.printf(
                "failed to send packet due to %s\n", e.toString());
        }
    }

    @Override
    public void close() {
        sock.close();
        connected = false;
    }

    @Override
    public boolean isConnected() throws SpinnmanIOException {
        return connected;
    }

    public InetSocketAddress getLocalSocketAddress() {
        return (InetSocketAddress) sock.getLocalSocketAddress();
    }

    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) sock.getRemoteSocketAddress();
    }
}
