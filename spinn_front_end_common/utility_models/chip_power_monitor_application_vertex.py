from pacman.executor.injection_decorator import inject_items
from pacman.model.graphs.application import ApplicationVertex

from spinn_front_end_common.abstract_models.\
    abstract_generates_data_specification import \
    AbstractGeneratesDataSpecification
from spinn_front_end_common.abstract_models.\
    abstract_has_associated_binary import \
    AbstractHasAssociatedBinary
from spinn_front_end_common.utility_models.\
    chip_power_monitor_machine_vertex import ChipPowerMonitorMachineVertex

from spinn_utilities.overrides import overrides


class ChipPowerMonitorApplicationVertex(
        ApplicationVertex, AbstractHasAssociatedBinary,
        AbstractGeneratesDataSpecification):

    def __init__(
            self, label, constraints, n_samples_per_recording,
            sampling_frequency):
        """

        :param label:
        :param constraints:
        :param n_samples_per_recording:
        :param sampling_frequency:
        """
        ApplicationVertex.__init__(self, label, constraints, 1)
        self._n_samples_per_recording = n_samples_per_recording
        self._sampling_frequency = sampling_frequency

    @property
    @overrides(ApplicationVertex.n_atoms)
    def n_atoms(self):
        return 1

    @overrides(ApplicationVertex.create_machine_vertex)
    def create_machine_vertex(self, vertex_slice, resources_required,
                              label=None, constraints=None):
        return ChipPowerMonitorMachineVertex(
            constraints, label, self._n_samples_per_recording,
            self._sampling_frequency)

    @overrides(AbstractHasAssociatedBinary.get_binary_file_name)
    def get_binary_file_name(self):
        return ChipPowerMonitorMachineVertex.binary_file_name()

    @overrides(
        AbstractGeneratesDataSpecification.generate_data_specification,
        additional_arguments={"machine_time_step", "time_scale_factor"})
    def generate_data_specification(
            self, spec, placement, machine_time_step, time_scale_factor):
        # generate spec for the machine vertex
        placement.vertex.generate_data_specification(
            spec, placement, machine_time_step, time_scale_factor)

    @overrides(AbstractHasAssociatedBinary.get_binary_start_type)
    def get_binary_start_type(self):
        return ChipPowerMonitorMachineVertex.binary_start_type()

    @overrides(ApplicationVertex.get_resources_used_by_atoms)
    @inject_items({
        "n_machine_time_steps": "TotalMachineTimeSteps",
        "machine_time_step": "MachineTimeStep",
        "time_scale_factor": "TimeScaleFactor"
    })
    def get_resources_used_by_atoms(
            self, vertex_slice, n_machine_time_steps, time_scale_factor,
            machine_time_step):
        return ChipPowerMonitorMachineVertex.get_resources(
            n_machine_time_steps, machine_time_step, time_scale_factor,
            self._n_samples_per_recording, self._sampling_frequency)
