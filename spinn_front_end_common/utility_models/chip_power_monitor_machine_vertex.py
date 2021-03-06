import math
import logging
from enum import Enum
import numpy
from spinn_utilities.log import FormatAdapter
from spinn_utilities.overrides import overrides
from data_specification.enums import DataType
from pacman.executor.injection_decorator import (
    inject_items, supports_injection)
from pacman.model.graphs.machine import MachineVertex
from pacman.model.resources import (
    ResourceContainer, SDRAMResource, CPUCyclesPerTickResource, DTCMResource)
from spinn_front_end_common.abstract_models import (
    AbstractGeneratesDataSpecification, AbstractHasAssociatedBinary)
from spinn_front_end_common.interface.buffer_management import (
    recording_utilities)
from spinn_front_end_common.interface.buffer_management.buffer_models import (
    AbstractReceiveBuffersToHost)
from spinn_front_end_common.utilities import globals_variables
from spinn_front_end_common.utilities.constants import (
    SARK_PER_MALLOC_SDRAM_USAGE, SYSTEM_BYTES_REQUIREMENT)
from spinn_front_end_common.utilities.utility_objs import ExecutableType
from spinn_front_end_common.utilities.helpful_functions import (
    locate_memory_region_for_placement, read_config_int)
from spinn_front_end_common.interface.simulation.simulation_utilities import (
    get_simulation_header_array)

logger = FormatAdapter(logging.getLogger(__name__))
BINARY_FILE_NAME = "chip_power_monitor.aplx"


@supports_injection
class ChipPowerMonitorMachineVertex(
        MachineVertex, AbstractHasAssociatedBinary,
        AbstractGeneratesDataSpecification, AbstractReceiveBuffersToHost):
    """ Machine vertex for C code representing functionality to record\
        idle times in a machine graph.
    """
    __slots__ = ["_n_samples_per_recording", "_sampling_frequency"]

    # data regions
    CHIP_POWER_MONITOR_REGIONS = Enum(
        value="CHIP_POWER_MONITOR_REGIONS",
        names=[('SYSTEM', 0),
               ('CONFIG', 1),
               ('RECORDING', 2)])

    # default magic numbers
    DEFAULT_MALLOCS_USED = 3
    CONFIG_SIZE_IN_BYTES = 8
    RECORDING_SIZE_PER_ENTRY = 18 * 4
    SAMPLE_RECORDING_REGION = 0
    MAX_CORES_PER_CHIP = 18
    MAX_BUFFER_SIZE = 1048576

    def __init__(
            self, label, constraints, n_samples_per_recording,
            sampling_frequency):
        """
        :param label: vertex label
        :param constraints: constraints on this vertex
        :param n_samples_per_recording: how may samples between recording entry
        :type n_samples_per_recording: int
        :param sampling_frequency: how often to sample
        :type sampling_frequency: microseconds
        """
        super(ChipPowerMonitorMachineVertex, self).__init__(
            label=label, constraints=constraints)
        self._n_samples_per_recording = n_samples_per_recording
        self._sampling_frequency = sampling_frequency

    @property
    def sampling_frequency(self):
        return self._sampling_frequency

    @property
    def n_samples_per_recording(self):
        return self._n_samples_per_recording

    @property
    @inject_items({"machine_time_step": "MachineTimeStep",
                   "n_machine_time_steps": "TotalMachineTimeSteps",
                   "time_scale_factor": "TimeScaleFactor"})
    @overrides(MachineVertex.resources_required,
               additional_arguments={
                   'machine_time_step', 'n_machine_time_steps',
                   'time_scale_factor'})
    def resources_required(
            self, n_machine_time_steps, machine_time_step, time_scale_factor):
        # pylint: disable=arguments-differ
        return self.get_resources(
            n_machine_time_steps, machine_time_step, time_scale_factor,
            self._n_samples_per_recording, self._sampling_frequency)

    @staticmethod
    def get_resources(
            n_machine_time_steps, time_step, time_scale_factor,
            n_samples_per_recording, sampling_frequency):
        """ Get the resources used by this vertex

        :return: Resource container
        """
        # pylint: disable=too-many-locals

        # get config
        config = globals_variables.get_simulator().config

        # get recording params
        minimum_buffer_sdram = config.getint(
            "Buffers", "minimum_buffer_sdram")
        using_auto_pause_and_resume = config.getboolean(
            "Buffers", "use_auto_pause_and_resume")
        receive_buffer_host = config.get("Buffers", "receive_buffer_host")
        receive_buffer_port = read_config_int(
            config, "Buffers", "receive_buffer_port")

        # figure recording size for max run
        if not using_auto_pause_and_resume and n_machine_time_steps is None:
            raise Exception(
                "You cannot use the chip power montiors without auto pause "
                "and resume and not allocating a n_machine_time_steps")

        # figure max buffer size
        max_buffer_size = 0
        if config.getboolean("Buffers", "enable_buffered_recording"):
            max_buffer_size = config.getint(
                "Buffers", "chip_power_monitor_buffer")

        maximum_sdram_for_buffering = [max_buffer_size]

        n_recording_entries = (math.ceil(
            (sampling_frequency / (time_step * time_scale_factor))) /
            n_samples_per_recording)

        recording_size = (
            ChipPowerMonitorMachineVertex.RECORDING_SIZE_PER_ENTRY *
            n_recording_entries)

        container = ResourceContainer(
            sdram=SDRAMResource(
                ChipPowerMonitorMachineVertex.sdram_calculation()),
            cpu_cycles=CPUCyclesPerTickResource(100),
            dtcm=DTCMResource(100))
        recording_sizes = recording_utilities.get_recording_region_sizes(
            [int(recording_size) * n_machine_time_steps], minimum_buffer_sdram,
            maximum_sdram_for_buffering, using_auto_pause_and_resume)
        container.extend(recording_utilities.get_recording_resources(
            recording_sizes, receive_buffer_host, receive_buffer_port))
        return container

    @staticmethod
    def sdram_calculation():
        """ Calculates the SDRAM requirements of the vertex

        :return: int
        """
        return SYSTEM_BYTES_REQUIREMENT + \
            ChipPowerMonitorMachineVertex.CONFIG_SIZE_IN_BYTES + \
            ChipPowerMonitorMachineVertex.DEFAULT_MALLOCS_USED * \
            SARK_PER_MALLOC_SDRAM_USAGE

    @overrides(AbstractHasAssociatedBinary.get_binary_file_name)
    def get_binary_file_name(self):
        return BINARY_FILE_NAME

    @staticmethod
    def binary_file_name():
        """ Return the string binary file name

        :return: basestring
        """
        return BINARY_FILE_NAME

    @inject_items({"time_scale_factor": "TimeScaleFactor",
                   "machine_time_step": "MachineTimeStep",
                   "n_machine_time_steps": "TotalMachineTimeSteps",
                   "ip_tags": "MemoryIpTags"})
    @overrides(AbstractGeneratesDataSpecification.generate_data_specification,
               additional_arguments={
                   "machine_time_step", "time_scale_factor",
                   "n_machine_time_steps", "ip_tags"})
    def generate_data_specification(
            self, spec, placement,  # @UnusedVariable
            machine_time_step, time_scale_factor, n_machine_time_steps,
            ip_tags):
        # pylint: disable=too-many-arguments, arguments-differ
        self._generate_data_specification(
            spec, machine_time_step, time_scale_factor, n_machine_time_steps,
            ip_tags)

    def _generate_data_specification(
            self, spec, machine_time_step, time_scale_factor,
            n_machine_time_steps, ip_tags):
        """ Supports the application vertex calling this directly

        :param spec: data spec
        :param machine_time_step: machine time step
        :param time_scale_factor: time scale factor
        :param n_machine_time_steps: n_machine time steps
        :param ip_tags: IP tags
        :rtype: None
        """
        # pylint: disable=too-many-arguments
        spec.comment("\n*** Spec for ChipPowerMonitor Instance ***\n\n")

        # Construct the data images needed for the Neuron:
        self._reserve_memory_regions(spec)
        self._write_setup_info(
            spec, machine_time_step, time_scale_factor, n_machine_time_steps,
            ip_tags)
        self._write_configuration_region(spec)

        # End-of-Spec:
        spec.end_specification()

    def _write_configuration_region(self, spec):
        """ Write the data needed by the C code to configure itself

        :param spec: spec object
        :rtype: None
        """
        spec.switch_write_focus(
            region=self.CHIP_POWER_MONITOR_REGIONS.CONFIG.value)
        spec.write_value(self._n_samples_per_recording,
                         data_type=DataType.UINT32)
        spec.write_value(self._sampling_frequency, data_type=DataType.UINT32)

    def _write_setup_info(
            self, spec, machine_time_step, time_scale_factor,
            n_machine_time_steps, ip_tags):
        """ Writes the system data as required.

        :param spec: the DSG spec writer
        :param machine_time_step: the machine time step
        :param time_scale_factor: the time scale factor
        :rtype: None
        """
        # pylint: disable=too-many-arguments
        spec.switch_write_focus(
            region=self.CHIP_POWER_MONITOR_REGIONS.SYSTEM.value)
        spec.write_array(get_simulation_header_array(
            self.get_binary_file_name(), machine_time_step, time_scale_factor))

        spec.switch_write_focus(
            region=self.CHIP_POWER_MONITOR_REGIONS.RECORDING.value)
        recorded_region_sizes = recording_utilities.get_recorded_region_sizes(
            [self._deduce_sdram_requirements_per_timer_tick(
                machine_time_step, time_scale_factor) * n_machine_time_steps],
            [self.MAX_BUFFER_SIZE])
        spec.write_array(recording_utilities.get_recording_header_array(
            recorded_region_sizes,
            globals_variables.get_simulator().config.getint(
                "Buffers", "time_between_requests"),
            None, ip_tags))

    def _reserve_memory_regions(self, spec):
        """ Reserve the DSG memory regions as required

        :param spec: the DSG specification to reserve in
        :rtype: None
        """
        spec.comment("\nReserving memory space for data regions:\n\n")

        # Reserve memory:
        spec.reserve_memory_region(
            region=self.CHIP_POWER_MONITOR_REGIONS.SYSTEM.value,
            size=SYSTEM_BYTES_REQUIREMENT,
            label='system')
        spec.reserve_memory_region(
            region=self.CHIP_POWER_MONITOR_REGIONS.CONFIG.value,
            size=self.CONFIG_SIZE_IN_BYTES, label='config')
        spec.reserve_memory_region(
            region=self.CHIP_POWER_MONITOR_REGIONS.RECORDING.value,
            size=recording_utilities.get_recording_header_size(1),
            label="Recording")

    @overrides(AbstractHasAssociatedBinary.get_binary_start_type)
    def get_binary_start_type(self):
        return self.binary_start_type()

    @staticmethod
    def binary_start_type():
        """ The type of binary that implements this vertex

        :return: starttype enum
        """
        return ExecutableType.USES_SIMULATION_INTERFACE

    @overrides(AbstractReceiveBuffersToHost.get_recording_region_base_address)
    def get_recording_region_base_address(self, txrx, placement):
        return locate_memory_region_for_placement(
            placement, self.CHIP_POWER_MONITOR_REGIONS.RECORDING.value, txrx)

    @overrides(AbstractReceiveBuffersToHost.get_recorded_region_ids)
    def get_recorded_region_ids(self):
        return [0]

    @inject_items({"time_scale_factor": "TimeScaleFactor"})
    @overrides(AbstractReceiveBuffersToHost.get_n_timesteps_in_buffer_space,
               additional_arguments={"time_scale_factor"})
    def get_n_timesteps_in_buffer_space(
            self, buffer_space, machine_time_step, time_scale_factor):
        # pylint: disable=arguments-differ
        return recording_utilities.get_n_timesteps_in_buffer_space(
            buffer_space,
            [self._deduce_sdram_requirements_per_timer_tick(
                machine_time_step, time_scale_factor)])

    @inject_items({"machine_time_step": "MachineTimeStep",
                   "n_machine_time_steps": "TotalMachineTimeSteps",
                   "time_scale_factor": "TimeScaleFactor"})
    @overrides(AbstractReceiveBuffersToHost.get_minimum_buffer_sdram_usage,
               additional_arguments={
                   'machine_time_step', 'n_machine_time_steps',
                   'time_scale_factor'})
    def get_minimum_buffer_sdram_usage(
            self, n_machine_time_steps, machine_time_step, time_scale_factor):
        # pylint: disable=arguments-differ
        return recording_utilities.get_minimum_buffer_sdram(
            [self._deduce_sdram_requirements_per_timer_tick(
                machine_time_step, time_scale_factor) * n_machine_time_steps],
            globals_variables.get_simulator().config.getint(
                "Buffers", "minimum_buffer_sdram"))[0]

    def _deduce_sdram_requirements_per_timer_tick(
            self, machine_time_step, time_scale_factor):
        """ Deduce SDRAM usage per timer tick

        :param machine_time_step: the machine time step
        :param time_scale_factor: the time scale factor
        :return: the SDRAM usage
        """
        timer_tick_in_micro_seconds = machine_time_step * time_scale_factor
        recording_time = \
            self._sampling_frequency * self._n_samples_per_recording
        n_entries = math.floor(timer_tick_in_micro_seconds / recording_time)
        return math.ceil(n_entries * self.RECORDING_SIZE_PER_ENTRY)

    def get_recorded_data(self, placement, buffer_manager):
        """ Get data from SDRAM given placement and buffer manager

        :param placement: the location on machine to get data from
        :param buffer_manager: the buffer manager that might have data
        :return: results
        :rtype: numpy array with 1 dimension
        """
        # for buffering output info is taken form the buffer manager
        samples, data_missing = buffer_manager.get_data_for_vertex(
            placement, self.SAMPLE_RECORDING_REGION)
        if data_missing:
            logger.warning(
                "Chip Power monitor has lost data on chip({}, {})",
                placement.x, placement.y)

        # get raw data as a byte array
        record_raw = samples.read_all()

        results = (
            numpy.frombuffer(record_raw, dtype="uint32").reshape(-1, 18) /
            self.n_samples_per_recording)
        return results
