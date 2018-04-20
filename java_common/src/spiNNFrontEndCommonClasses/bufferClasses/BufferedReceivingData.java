package spiNNFrontEndCommonClasses.bufferClasses;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author alan
 */
public class BufferedReceivingData {
    private HashMap<Location, Boolean> _is_flushed = 
            new HashMap<Location, Boolean>();
    private HashMap<Location, Integer> _sequence_no = 
            new HashMap<Location, Integer>();
    private HashMap<Location, SpiNNakerRequestReadData> _last_packet_received =
            new HashMap<Location, SpiNNakerRequestReadData>();
    private HashMap<Location, HostDataRead> _last_packet_sent = 
            new HashMap<Location, HostDataRead>();
    private HashMap<Location, Integer> _end_buffering_sequence_no = 
            new HashMap<Location, Integer>();
    private HashMap<Location, ChannelBufferState> _end_buffering_state = 
            new HashMap<Location, ChannelBufferState>();
            
    public BufferedReceivingData(){
       
    }

    public boolean is_data_from_region_flushed(self, x, y, p, region){
        /* Check if the data region has been flushed

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data
        :type region: int
        :return: True if the region has been flushed. False otherwise
        :rtype: bool
        */
        Location key = new Location(x, y, p, region);
        
        return this._is_flushed.get(key);

    def flushing_data_from_region(self, x, y, p, region, data):
        """ Store flushed data from a region of a core on a chip, and mark it\
            as being flushed

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data to be stored
        :type region: int
        :param data: data to be stored
        :type data: bytearray
        """
        # pylint: disable=too-many-arguments
        self.store_data_in_region_buffer(x, y, p, region, data)
        self._is_flushed[x, y, p, region] = True

    def store_last_received_packet_from_core(self, x, y, p, packet):
        """ Store the most recent packet received from SpiNNaker for a given\
            core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param packet: SpinnakerRequestReadData packet received
        :type packet:\
            :py:class:`spinnman.messages.eieio.command_messages.spinnaker_request_read_data.SpinnakerRequestReadData`
        """
        self._last_packet_received[x, y, p] = packet

    def last_received_packet_from_core(self, x, y, p):
        """ Get the last packet received for a given core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: SpinnakerRequestReadData packet received
        :rtype:\
            :py:class:`spinnman.messages.eieio.command_messages.spinnaker_request_read_data.SpinnakerRequestReadData`
        """
        return self._last_packet_received[x, y, p]

    def store_last_sent_packet_to_core(self, x, y, p, packet):
        """ Store the last packet sent to the given core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param packet: last HostDataRead packet sent
        :type packet:\
            :py:class:`spinnman.messages.eieio.command_messages.host_data_read.HostDataRead`
        """
        self._last_packet_sent[x, y, p] = packet

    def last_sent_packet_to_core(self, x, y, p):
        """ Retrieve the last packet sent to a core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: last HostDataRead packet sent
        :rtype:\
            :py:class:`spinnman.messages.eieio.command_messages.host_data_read.HostDataRead`
        """
        return self._last_packet_sent[x, y, p]

    def last_sequence_no_for_core(self, x, y, p):
        """ Get the last sequence number for a core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: last sequence number used
        :rtype: int
        """
        return self._sequence_no[x, y, p]

    def update_sequence_no_for_core(self, x, y, p, sequence_no):
        """ Set the last sequence number used

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param sequence_no: last sequence number used
        :type sequence_no: int
        :rtype: None
        """
        self._sequence_no[x, y, p] = sequence_no

    def get_region_data(self, x, y, p, region):
        """ Get the data stored for a given region of a given core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data
        :type region: int
        :return: an array contained all the data received during the\
            simulation, and a flag indicating if any data was missing
        :rtype: (bytearray, bool)
        """
        missing = None
        if (x, y, p, region) not in self._end_buffering_state:
            missing = (x, y, p, region)
        data = self._data[x, y, p, region].read_all()
        return data, missing

    def get_region_data_pointer(self, x, y, p, region):
        """ Get the data received during the simulation for a region of a core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data
        :type region: int
        :return: all the data received during the simulation, and a flag\
            indicating if any data was lost
        :rtype: tuple of \
            (:py:class:`spinn_front_end_common.interface.buffer_management.buffer_models.AbstractBufferedDataStorage`,
             bool)
        """
        missing = False
        if (x, y, p, region) not in self._end_buffering_state:
            missing = True
        else:
            missing = self._end_buffering_state[x, y, p, region].missing_info
        data_pointer = self._data[x, y, p, region]
        return data_pointer, missing

    def store_end_buffering_state(self, x, y, p, region, state):
        """ Store the end state of buffering

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param state: The end state
        """
        # pylint: disable=too-many-arguments
        self._end_buffering_state[x, y, p, region] = state

    def is_end_buffering_state_recovered(self, x, y, p, region):
        """ Determine if the end state has been stored

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: True if the state has been stored
        """
        return (x, y, p, region) in self._end_buffering_state

    def get_end_buffering_state(self, x, y, p, region):
        """ Get the end state of the buffering

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: The end state
        """
        return self._end_buffering_state[x, y, p, region]

    def store_end_buffering_sequence_number(self, x, y, p, sequence):
        """ Store the last sequence number sent by the core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param sequence: The last sequence number
        :type sequence: int
        """
        self._end_buffering_sequence_no[x, y, p] = sequence

    def is_end_buffering_sequence_number_stored(self, x, y, p):
        """ Determine if the last sequence number has been retrieved

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: True if the number has been retrieved
        :rtype: bool
        """
        return (x, y, p) in self._end_buffering_sequence_no

    def get_end_buffering_sequence_number(self, x, y, p):
        """ Get the last sequence number sent by the core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :return: The last sequence number
        :rtype: int
        """
        return self._end_buffering_sequence_no[x, y, p]

    def resume(self):
        """ Resets states so that it can behave in a resumed mode
        """
        self._end_buffering_state = dict()
        self._is_flushed = defaultdict(lambda: False)
        self._sequence_no = defaultdict(lambda: 0xFF)
        self._last_packet_received = defaultdict(lambda: None)
        self._last_packet_sent = defaultdict(lambda: None)

    def clear(self, x, y, p, region_id):
        """ Clears the data from a given data region (only clears things\
            associated with a given data recording region).

        :param x: placement x coord
        :param y: placement y coord
        :param p: placement p coord
        :param region_id: the recording region id to clear data from
        :rtype: None
        """
        del self._end_buffering_state[x, y, p, region_id]
        del self._data[x, y, p, region_id]
        del self._is_flushed[x, y, p, region_id]

