import os
from pacman.utilities.utility_calls import md5
from spinn_front_end_common.utilities.constants import SDP_PORTS


def get_simulation_header_array(
        binary_file_name, machine_time_step, time_scale_factor):
    """ Get data to be written to the simulation header

    :param binary_file_name: The name of the binary of the application
    :param machine_time_step: The time step of the simulation
    :param time_scale_factor: The time scaling of the simulation
    :return: An array of values to be written as the simulation header
    """
    # Get first 32-bits of the md5 hash of the application name
    application_name_hash = md5(os.path.splitext(binary_file_name)[0])[:8]

    # Write this to the system region (to be picked up by the simulation):
    data = list()
    data.append(int(application_name_hash, 16))
    data.append(machine_time_step * time_scale_factor)

    # add SDP port number for receiving synchronisations and new run times
    data.append(SDP_PORTS.RUNNING_COMMAND_SDP_PORT.value)

    return data
