package spiNNManClasses.connections;

import static spiNNManClasses.Constants.SCP_SCAMP_PORT;
import static spiNNManClasses.connections.Utils.updateSdpHeaderForUdpSend;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import spiNNManClasses.exceptions.SpinnmanIOException;
import spiNNManClasses.messages.SCPRequest;
import spiNNManClasses.messages.SCPResult;;

/**
 * A UDP connection to SCAMP on the board
 * 
 * @author alan
 * @author dkf
 */
public class ScampConnection extends SDPConnection
        implements SCPSender, SCPReceiver {
    /**
     * 
     * @param chipX
     *            The x-coordinate of the chip on the board with this
     *            remote_host
     * @param chipY
     *            The y-coordinate of the chip on the board with this
     *            remote_host
     * @param localHost
     *            The optional IP address or host name of the local interface to
     *            listen on
     * @param localPort
     *            The optional local port to listen on
     * @param remoteHost
     *            The optional remote host name or IP address to send messages
     *            to. If not specified, sending will not be possible using this
     *            connection
     * @param remotePort
     *            The optional remote port number to send messages to. If not
     *            specified, sending will not be possible using this connection
     * @throws SocketException
     */
    public ScampConnection(int chipX, int chipY, String localHost,
            Integer localPort, String remoteHost, Integer remotePort)
            throws SocketException {
        super(localPort == null ? 0 : localPort, localHost,
                remotePort == null ? SCP_SCAMP_PORT : remotePort, remoteHost,
                1000, chipX, chipY);
    }

    public ScampConnection() throws SocketException {
        this(255, 255, null, null, null, SCP_SCAMP_PORT);
    }

    @Override
    public final int chipX() {
        return chipX;
    }

    @Override
    public final int chipY() {
        return chipY;
    }

    public void updateChipCoordinates(int x, int y) {
        chipX = x;
        chipY = y;
    }

    @Override
    public ByteBuffer getSCPData(SCPRequest scpRequest) {
        ByteBuffer buffer = ByteBuffer.allocate(320);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        buffer.limit(0);
        addToBuffer(buffer, scpRequest);
        return buffer;
    }

    public void addToBuffer(ByteBuffer buffer, SCPRequest scpRequest) {
        updateSdpHeaderForUdpSend(scpRequest.getSdpHeader(), chipX, chipY);
        buffer.limit(buffer.limit() + 2);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        scpRequest.appendTo(buffer);
    }

    @Override
    public SCPResultTuple receiveSCPResonse(Integer timeout) {
        DatagramPacket packet = receive(); // TODO use timeout
        ByteBuffer data = ByteBuffer.wrap(packet.getData(), packet.getOffset(),
                packet.getLength());
        data.position(10);
        int result = data.getShort();
        int sequence = data.getShort();
        return new SCPResultTuple(SCPResult.get(result), sequence, data, 2);
    }

    private static final String _REPR_TEMPLATE = "SCAMPConnection("
            + "chip_x=%d, chip_y=%d, local_host=%s,"
            + " local_port=%d, remote_host=%s, remote_port=%d)";

    @Override
    public String toString() {
        InetSocketAddress local = getLocalSocketAddress();
        InetSocketAddress remote = getRemoteSocketAddress();
        return String.format(_REPR_TEMPLATE, chipX, chipY,
                local.getAddress().getHostAddress(), local.getPort(),
                remote.getAddress().getHostAddress(), remote.getPort());
    }

    @Override
    public void sendSCPRequest(SCPRequest scp_request)
            throws SpinnmanIOException {
        ByteBuffer message = this.getSCPData(scp_request);
        sendData(message.array(), message.limit());
    }

    @Override
    public boolean isReadyToReceive(int timeout) {
        // TODO Auto-generated method stub
        return false;
    }
}
