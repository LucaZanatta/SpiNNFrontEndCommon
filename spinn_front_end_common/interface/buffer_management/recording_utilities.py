import struct
import sys
import math
from pacman.model.resources import (
    ResourceContainer, IPtagResource, SDRAMResource)
from spinn_front_end_common.interface.buffer_management.storage_objects\
    import (
        ChannelBufferState)
from spinn_front_end_common.utilities.constants import (
    SARK_PER_MALLOC_SDRAM_USAGE, SDP_PORTS)

# The offset of the last sequence number field in bytes
_LAST_SEQUENCE_NUMBER_OFFSET = 4 * 6

# The offset of the memory addresses in bytes
_FIRST_REGION_ADDRESS_OFFSET = 4 * 7

# the number of data elements inside the recording region before
# recording regions sizes are stored.
_RECORDING_ELEMENTS_BEFORE_REGION_SIZES = 7

# The Buffer traffic type
TRAFFIC_IDENTIFIER = "BufferTraffic"

_ONE_WORD = struct.Struct("<I")
_TWO_SHORTS = struct.Struct("<HH")


def get_recording_header_size(n_recorded_regions):
    """ Get the size of the data to be written for the recording header

    :param n_recorded_regions: The number of regions to be recorded
    """

    # See recording.h/recording_initialise for data included in the header
    return (_RECORDING_ELEMENTS_BEFORE_REGION_SIZES +
            (2 * n_recorded_regions)) * 4


def get_recording_data_size(recorded_region_sizes):
    """ Get the size of the recorded data to be reserved

    :param recorded_region_sizes:\
        A list of sizes of each region to be recorded.\
        A size of 0 is acceptable.
    :rtype: int
    """
    return (
        # The total recording data size
        sum(recorded_region_sizes) +

        # The storage of the recording state
        (len(recorded_region_sizes) *
         ChannelBufferState.size_of_channel_state()) +

        # The SARK allocation of SDRAM overhead
        (len(recorded_region_sizes) * SARK_PER_MALLOC_SDRAM_USAGE))


def get_minimum_buffer_sdram(
        buffered_sdram, minimum_sdram_for_buffering=(1024 * 1024)):
    """ Get the minimum buffer SDRAM.

    :param buffered_sdram:\
        The maximum number of bytes to use per recorded region.\
        Disabled regions can specify 0.
    :type buffered_sdram: list(int)
    :param minimum_sdram_for_buffering:\
        The minimum SDRAM to reserve per recorded region for buffering
    :type minimum_sdram_for_buffering: int
    :rtype: list(int)
    """

    # The minimum SDRAM for each region is:
    # - If the buffered_sdram_per_timestep for the region is > 0 and
    #   n_machine_time_steps is defined then the minimum of the actual region
    #   size and the minimum_sdram_for_buffering
    # - If the SDRAM is 0 then 0
    # - If n_machine_time_steps is None then minimum_sdram_for_buffering
    return [
        min(sdram, minimum_sdram_for_buffering)
        for sdram in buffered_sdram
    ]


def get_recording_region_sizes(
        buffered_sdram, minimum_sdram_for_buffering=(1024 * 1024),
        maximum_sdram_for_buffering=None, use_auto_pause_and_resume=True):
    """ Get the size of each recording region to be passed in to\
        :py:func:`get_recording_resources`, based on the details of the\
        simulation.

    :param buffered_sdram:\
        The maximum number of bytes to use of recording, per recorded region.
        Disabled regions can specify 0.
    :type buffered_sdram: list(int)
    :param minimum_sdram_for_buffering:\
        The minimum SDRAM to reserve per recorded region for buffering
    :type minimum_sdram_for_buffering: int
    :param maximum_sdram_for_buffering:\
        The maximum size of each buffer, or None if no maximum
    :type maximum_sdram_for_buffering: None or list(int)
    :param use_auto_pause_and_resume:\
        True if automatic pause and resume is to be used for buffering
    :type use_auto_pause_and_resume: bool
    :rtype: list(int)
    """
    if use_auto_pause_and_resume:
        # If auto pause and resume is enabled, find the minimum sizes
        return get_minimum_buffer_sdram(
            buffered_sdram, minimum_sdram_for_buffering)
    else:

        # If auto pause and resume is disabled, use the actual region size
        return get_recorded_region_sizes(
            buffered_sdram, maximum_sdram_for_buffering)


def get_recording_resources(
        region_sizes, buffering_ip_address=None,
        buffering_port=None, notification_tag=None):
    """ Get the resources for recording

    :param region_sizes:\
        A list of the sizes of each region.  A size of 0 is acceptable to\
        indicate an empty region
    :type region_sizes: list(int)
    :param buffering_ip_address:\
        The IP address to receive buffering messages on, or None if buffering\
        is not in use
    :type buffering_ip_address: str
    :param buffering_port:\
        The port to receive buffering messages on, or None if a port is to be\
        assigned
    :type buffering_port: int
    :param notification_tag:\
        The tag to send buffering messages with, or None to use a default tag
    :type notification_tag: int
    :rtype: :py:class:`pacman.model.resources.ResourceContainer`
    """
    ip_tags = list()
    if buffering_ip_address is not None:
        ip_tags.append(IPtagResource(
            buffering_ip_address, buffering_port, True, notification_tag,
            TRAFFIC_IDENTIFIER))
    # return the resources including the SDRAM requirements
    return ResourceContainer(
        iptags=ip_tags,
        sdram=SDRAMResource(
            get_recording_header_size(len(region_sizes)) +
            get_recording_data_size(region_sizes)))


def get_recorded_region_sizes(
        buffered_sdram, maximum_sdram_for_buffering=None):
    """ Get the size of each recording region to be passed in to\
        get_recording_header_array

    :param buffered_sdram:\
        The maximum SDRAM used in bytes per region
    :type buffered_sdram_per_timestep: list(int)
    :param maximum_sdram_for_buffering:\
        The maximum size of each buffer, or None if no maximum
    :type maximum_sdram_for_buffering: None or list(int)
    :rtype: list(int)
    """

    # The size of each buffer is the actual size needed for the number of
    # timesteps, or the maximum for buffering if a maximum is specified
    return [
        sdram
        if (maximum_sdram_for_buffering is None or
            maximum_sdram_for_buffering[i] == 0 or
            sdram < maximum_sdram_for_buffering[i])
        else maximum_sdram_for_buffering[i]
        for i, sdram in enumerate(buffered_sdram)
    ]


def get_recording_header_array(
        recorded_region_sizes,
        time_between_triggers=0, buffer_size_before_request=None, ip_tags=None,
        buffering_tag=None):
    """ Get data to be written for the recording header

    :param recorded_region_sizes:\
        A list of sizes of each region to be recorded.\
        A size of 0 is acceptable.
    :param time_between_triggers:\
        The minimum time between requesting reads of any region
    :param buffer_size_before_request:\
        The amount of buffer to fill before a read request is sent
    :param ip_tags: A list of IP tags to extract the buffer tag from
    :param buffering_tag: The tag to use for buffering requests
    :return: An array of values to be written as the header
    :rtype: list(int)
    """

    # Find the tag if required
    buffering_output_tag = 0
    buffering_output_dest_x = 0
    buffering_output_dest_y = 0
    if buffering_tag is not None:
        buffering_output_tag = buffering_tag
    elif ip_tags:
        buffering_output_tag = None
        for tag in ip_tags:
            if tag.traffic_identifier == TRAFFIC_IDENTIFIER:
                buffering_output_tag = tag.tag
                buffering_output_dest_x = tag.destination_x
                buffering_output_dest_y = tag.destination_y
                break
        else:
            raise Exception("Buffering tag not found")

    # See recording.h/recording_initialise for data included in the header
    data = list()

    # The parameters
    data.append(len(recorded_region_sizes))
    data.append(buffering_output_tag)
    data.append(_ONE_WORD.unpack(_TWO_SHORTS.pack(
        buffering_output_dest_y, buffering_output_dest_x))[0])
    data.append(SDP_PORTS.OUTPUT_BUFFERING_SDP_PORT.value)
    if buffer_size_before_request is not None:
        data.append(buffer_size_before_request)
    else:

        # If no buffer size before request, assume not buffering, so ensure
        # that the buffering will not be activated
        data.append(max(recorded_region_sizes) + 256)
    data.append(time_between_triggers)

    # The last sequence number (to be filled in by C code)
    data.append(0)

    # The pointers for each region (to be filled in by C code)
    data.extend([0 for _ in recorded_region_sizes])

    # The size of the regions
    data.extend(recorded_region_sizes)

    return data


def get_last_sequence_number(placement, transceiver, recording_data_address):
    """ Read the last sequence number from the data

    :param placement: The placement from which to read the sequence number
    :param transceiver: The transceiver to use to read the sequence number
    :param recording_data_address:\
        The address of the recording data from which to read the number
    :rtype: int
    """
    data = transceiver.read_memory(
        placement.x, placement.y,
        recording_data_address + _LAST_SEQUENCE_NUMBER_OFFSET, 4)
    return _ONE_WORD.unpack_from(data)[0]


def get_region_pointer(placement, transceiver, recording_data_address, region):
    """ Get a pointer to a recording region

    :param placement: The placement from which to read the pointer
    :param transceiver: The transceiver to use to read the pointer
    :param recording_data_address:\
        The address of the recording data from which to read the pointer
    :param region: The index of the region to get the pointer of
    :rtype: int
    """
    data = transceiver.read_memory(
        placement.x, placement.y,
        recording_data_address + _FIRST_REGION_ADDRESS_OFFSET + (region * 4),
        4)
    return _ONE_WORD.unpack_from(data)[0]


def get_n_timesteps_in_buffer_space(buffer_space, buffered_sdram_per_timestep):
    """ Get the number of time steps of data that can be stored in a given\
        buffers space

    :param buffer_space: The space that will hold the data
    :type buffer_space: int
    :param buffered_sdram_per_timestep:\
        The maximum SDRAM used by each region per timestep
    :type buffered_sdram_per_timestep: list(int)
    :rtype: int
    """
    total_per_timestep = sum(buffered_sdram_per_timestep)
    if total_per_timestep == 0:
        return sys.maxsize
    return int(math.floor(buffer_space / total_per_timestep))


def get_recorded_region_ids(buffered_sdram_per_timestep):
    """ Get the IDs of regions where recording is enabled

    :param buffered_sdram_per_timestep:\
        The maximum SDRAM used by each region per timestep, where 0 indicates\
        a disabled region
    :type buffered_sdram_per_timestep: list(int)
    :rtype: list(int)
    """
    return [
        region_id
        for region_id in range(len(buffered_sdram_per_timestep))
        if buffered_sdram_per_timestep[region_id] > 0]
