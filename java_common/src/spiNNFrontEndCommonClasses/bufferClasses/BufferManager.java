package spiNNFrontEndCommonClasses.bufferClasses;

import spiNNManClasses.Transceiver;
import commonClasses.CoreLocation;

public class BufferManager {
    
    public BufferManager(){
        _received_data
    }
    
    private int locate_memory_region_for_placement(
            CoreLocation placement, int region, Transceiver transceiver){
    /* Get the address of a region for a placement

    :param region: the region to locate the base address of
    :type region: int
    :param placement: the placement object to get the region address of
    :type placement: pacman.model.placements.Placement
    :param transceiver: the python interface to the spinnaker machine
    :type transceiver: spiNNMan.transciever.Transciever
    */
    
    regions_base_address = transceiver.get_cpu_information_from_core(
        placement.x, placement.y, placement.p).user[0]

    # Get the position of the region in the pointer table
    region_offset_in_pointer_table = \
        utility_calls.get_region_base_address_offset(
            regions_base_address, region)
    region_address = buffer(transceiver.read_memory(
        placement.x, placement.y, region_offset_in_pointer_table, 4))
    return _ONE_WORD.unpack_from(region_address)[0]
    
    
    public byte[] get_data_for_vertex_locked(placement, recording_region_id){
        recording_data_address = \
            placement.vertex.get_recording_region_base_address(
                self._transceiver, placement)

        # Ensure the last sequence number sent has been retrieved
        if not self._received_data.is_end_buffering_sequence_number_stored(
                placement.x, placement.y, placement.p):
            self._received_data.store_end_buffering_sequence_number(
                placement.x, placement.y, placement.p,
                get_last_sequence_number(
                    placement, self._transceiver, recording_data_address))

        # Read the data if not already received
        if not self._received_data.is_data_from_region_flushed(
                placement.x, placement.y, placement.p,
                recording_region_id):

            # Read the end state of the recording for this region
            if not self._received_data.is_end_buffering_state_recovered(
                    placement.x, placement.y, placement.p,
                    recording_region_id):
                end_state = self._generate_end_buffering_state_from_machine(
                    placement, get_region_pointer(
                        placement, self._transceiver, recording_data_address,
                        recording_region_id))
                self._received_data.store_end_buffering_state(
                    placement.x, placement.y, placement.p, recording_region_id,
                    end_state)
            else:
                end_state = self._received_data.get_end_buffering_state(
                    placement.x, placement.y, placement.p, recording_region_id)

            # current read needs to be adjusted in case the last portion of the
            # memory has already been read, but the HostDataRead packet has not
            # been processed by the chip before simulation finished.
            # This situation is identified by the sequence number of the last
            # packet sent to this core and the core internal state of the
            # output buffering finite state machine
            seq_no_last_ack_packet = \
                self._received_data.last_sequence_no_for_core(
                    placement.x, placement.y, placement.p)

            # get the last sequence number
            last_sequence_number = \
                self._received_data.get_end_buffering_sequence_number(
                    placement.x, placement.y, placement.p)

            if last_sequence_number == seq_no_last_ack_packet:
                self._process_last_ack(placement, recording_region_id,
                                       end_state)

            # now state is updated, read back values for read pointer and
            # last operation performed
            last_operation = end_state.last_buffer_operation
            start_ptr = end_state.start_address
            end_ptr = end_state.end_address
            write_ptr = end_state.current_write
            read_ptr = end_state.current_read

            # now read_ptr is updated, check memory to read
            if read_ptr < write_ptr:
                length = write_ptr - read_ptr
                logger.debug(
                    "< Reading {} bytes from {}, {}, {}: {} for region {}",
                    length, placement.x, placement.y, placement.p,
                    hex(read_ptr), recording_region_id)
                data = self._request_data(
                    transceiver=self._transceiver, placement_x=placement.x,
                    address=read_ptr, length=length, placement_y=placement.y)
                self._received_data.flushing_data_from_region(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)

            elif read_ptr > write_ptr:
                length = end_ptr - read_ptr
                if length < 0:
                    raise exceptions.ConfigurationException(
                        "The amount of data to read is negative!")
                logger.debug(
                    "> Reading {} bytes from {}, {}, {}: {} for region {}",
                    length, placement.x, placement.y, placement.p,
                    hex(read_ptr), recording_region_id)
                data = self._request_data(
                    transceiver=self._transceiver, placement_x=placement.x,
                    address=read_ptr, length=length, placement_y=placement.y)
                self._received_data.store_data_in_region_buffer(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)
                read_ptr = start_ptr
                length = write_ptr - read_ptr
                logger.debug(
                    "Reading {} bytes from {}, {}, {}: {} for region {}",
                    length, placement.x, placement.y, placement.p,
                    hex(read_ptr), recording_region_id)
                data = self._request_data(
                    transceiver=self._transceiver, placement_x=placement.x,
                    address=read_ptr, length=length, placement_y=placement.y)
                self._received_data.flushing_data_from_region(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)

            elif (read_ptr == write_ptr and
                    last_operation == BUFFERING_OPERATIONS.BUFFER_WRITE.value):
                length = end_ptr - read_ptr
                logger.debug(
                    "= Reading {} bytes from {}, {}, {}: {} for region {}",
                    length, placement.x, placement.y, placement.p,
                    hex(read_ptr), recording_region_id)
                data = self._request_data(
                    transceiver=self._transceiver, placement_x=placement.x,
                    address=read_ptr, length=length, placement_y=placement.y)
                self._received_data.store_data_in_region_buffer(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)
                read_ptr = start_ptr
                length = write_ptr - read_ptr
                logger.debug(
                    "Reading {} bytes from {}, {}, {}: {} for region {}",
                    length, placement.x, placement.y, placement.p,
                    hex(read_ptr), recording_region_id)
                data = self._request_data(
                    transceiver=self._transceiver, placement_x=placement.x,
                    address=read_ptr, length=length, placement_y=placement.y)
                self._received_data.flushing_data_from_region(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)

            elif (read_ptr == write_ptr and
                    last_operation == BUFFERING_OPERATIONS.BUFFER_READ.value):
                data = bytearray()
                self._received_data.flushing_data_from_region(
                    placement.x, placement.y, placement.p, recording_region_id,
                    data)

        # data flush has been completed - return appropriate data
        # the two returns can be exchanged - one returns data and the other
        # returns a pointer to the structure holding the data
        data = self._received_data.get_region_data_pointer(
            placement.x, placement.y, placement.p, recording_region_id)
        return data
    
}
