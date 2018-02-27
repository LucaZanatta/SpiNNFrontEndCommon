from data_specification import data_spec_sender
from spinn_front_end_common.utility_models.data_speed_up_packet_gatherer_machine_vertex import \
    DataSpeedUpPacketGatherMachineVertex

from spinn_utilities.progress_bar import ProgressBar
from spinn_machine import CoreSubsets

# spinnman imports
from spinnman.model.enums import CPUState

# front end common imports
from spinn_front_end_common.utilities.constants import DSE_DATA_STRUCT_SIZE

import os
import logging
import struct

logger = logging.getLogger(__name__)
_FOUR_WORDS = struct.Struct("<4I")


class MachineExecuteOtherDataSpecification(object):
    """ Executes the machine based data specification
    """

    __slots__ = []

    def __call__(
            self, write_memory_map_report, dsg_targets, transceiver, app_id,
            uses_advanced_monitors=False, machine=None,
            extra_monitor_cores_to_ethernet_connection_map=None):
        """
        :param write_memory_map_report: bool for writing memory report
        :param dsg_targets: the mapping between placement and dsg file
        :param transceiver: SpiNNMan instance
        :param app_id: the app id
        :param machine: the SpiNNMachine instance
        :param uses_advanced_monitors: flag for using extra monitors
        :param extra_monitor_cores_to_ethernet_connection_map:\
        extra monitor to ethernet chip map
        """
        return self.spinnaker_based_data_specification_execution(
            write_memory_map_report, dsg_targets, transceiver, app_id,
            uses_advanced_monitors, machine,
            extra_monitor_cores_to_ethernet_connection_map)

    @staticmethod
    def spinnaker_based_data_specification_execution(
            write_memory_map_report, dsg_targets, transceiver, app_id,
            uses_advanced_monitors, machine,
            extra_monitor_cores_to_ethernet_connection_map):
        """
        :param write_memory_map_report: bool for writing memory report
        :param dsg_targets: the mapping between placement and dsg file
        :param transceiver: SpiNNMan instance
        :param app_id: the app id
        :param machine: the SpiNNMachine instance
        :param uses_advanced_monitors: flag for using extra monitors
        :param extra_monitor_cores_to_ethernet_connection_map:\
        extra monitor to ethernet chip map
        :return: True
        :rtype: bool
        """

        # create a progress bar for end users
        progress = ProgressBar(dsg_targets, "Loading data specifications")

        dse_app_id = transceiver.app_id_tracker.get_new_id()

        core_subset = CoreSubsets()
        for (x, y, p, label) in progress.over(dsg_targets):
            core_subset.add_processor(x, y, p)

            dse_data_struct_address = transceiver.malloc_sdram(
                x, y, DSE_DATA_STRUCT_SIZE, dse_app_id)

            data_spec_file_path = dsg_targets[x, y, p, label]
            data_spec_file_size = os.path.getsize(data_spec_file_path)

            base_address = transceiver.malloc_sdram(
                x, y, data_spec_file_size, dse_app_id)

            dse_data_struct_data = _FOUR_WORDS.pack(
                base_address, data_spec_file_size, app_id,
                write_memory_map_report)

            transceiver.write_memory(
                x, y, dse_data_struct_address, dse_data_struct_data,
                len(dse_data_struct_data))

            # determine which function to use for writing large memory
            write_memory_function = DataSpeedUpPacketGatherMachineVertex. \
                locate_correct_write_data_function_for_chip_location(
                machine=machine, x=x, y=y, transceiver=transceiver,
                uses_advanced_monitors=uses_advanced_monitors,
                extra_monitor_cores_to_ethernet_connection_map=
                extra_monitor_cores_to_ethernet_connection_map)

            write_memory_function(
                x, y, base_address, data_spec_file_path, is_filename=True)

            # data spec file is written at specific address (base_address)
            # this is encapsulated in a structure with four fields:
            # 1 - data specification base address
            # 2 - data specification file size
            # 3 - future application ID
            # 4 - store data for memory map report (True / False)
            # If the memory map report is going to be produced, the
            # address of the structure is returned in user1
            user_0_address = transceiver.\
                get_user_0_register_address_from_core(x, y, p)

            transceiver.write_memory(
                x, y, user_0_address, dse_data_struct_address, 4)

        # Execute the DSE on all the cores
        logger.info("Loading the Data Specification Executor")
        dse_exec = os.path.join(
            os.path.dirname(data_spec_sender),
            'data_specification_executor.aplx')
        transceiver.execute_flood(
            core_subset, dse_exec, app_id, is_filename=True)

        logger.info(
            "Waiting for On-chip Data Specification Executor to complete")
        transceiver.wait_for_cores_to_be_in_state(
            core_subset, app_id, [CPUState.FINISHED])

        transceiver.stop_application(dse_app_id)
        transceiver.app_id_tracker.free_id(dse_app_id)
        logger.info("On-chip Data Specification Executor completed")