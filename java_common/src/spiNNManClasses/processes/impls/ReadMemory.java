package spiNNManClasses.processes.impls;

import static spiNNManClasses.Constants.ADDRESS_LENGTH_DTYPE;
import static spiNNManClasses.Constants.SDPFlag.REPLY_EXPECTED;
import static spiNNManClasses.model.enums.SCPCommands.READ_MEMORY;

import java.nio.ByteBuffer;

import spiNNManClasses.connections.SCPResponse;
import spiNNManClasses.exceptions.SpinnmanUnexpectedResponseCodeException;
import spiNNManClasses.messages.SCPRequest;
import spiNNManClasses.messages.SCPResult;
import spiNNManClasses.messages.SDPHeader;

class ReadMemory extends SCPRequest {
    public ReadMemory(int x, int y, int address, int length) {
        this(x, y, 0, 0, address, length);
    }

    public ReadMemory(int x, int y, int cpu, int port, int address,
            int length) {
        super(new SDPHeader(x, y, cpu, port, REPLY_EXPECTED.value, 0, 0, 0, 0,
                0), new Header(READ_MEMORY), address, length,
                ADDRESS_LENGTH_DTYPE
                        .get(((address % 4) << 8) | (length % 4)).value,
                null);
    }

    @Override
    public SCPResponse getScpResponse() {
        // TODO Auto-generated method stub
        return new Response();
    }

    class Response extends SCPResponse {
        ByteBuffer data = null;
        int length = 0;
        int offset = 0;

        @Override
        protected void readDataBytes(ByteBuffer data, int offset) {
            if (this.scpHeader.result != SCPResult.OK) {
                throw new SpinnmanUnexpectedResponseCodeException(
                    "Read", "CMD_READ", this.scpHeader.result);
            }
            this.data = data;
            this.offset = offset;
            this.length = data.limit() - offset;
        }
    }
}
