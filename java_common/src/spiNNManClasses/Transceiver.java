package spiNNManClasses;

import spiNNManClasses.model.CPUInfo;
import java.util.ArrayList;
import spiNNMachineClasses.CoreSubsets;
import spiNNManClasses.connections.ScampConnection;

public class Transceiver {
    
    private final ArrayList<ScampConnection> scampConnectionSelector = 
        new ArrayList<>();
    
    public Transceiver(){
        
    }
    
    public CPUInfo getCpuInformationFromCore(int x, int y, int p){
        /** Get information about a specific processor on the board

        @param x: The x-coordinate of the chip containing the processor
        @param y: The y-coordinate of the chip containing the processor
        @param p: The id of the processor to get the information about
        @return: The cpu information for the selected core
        @raise spinnman.exceptions.SpinnmanIOException: \
            If there is an error communicating with the board
        @raise spinnman.exceptions.SpinnmanInvalidPacketException: \
            If a packet is received that is not in the valid format
        @raise spinnman.exceptions.SpinnmanInvalidParameterException:
            * If x, y, p is not a valid processor
            * If a packet is received that has invalid parameters
        @raise spinnman.exceptions.SpinnmanUnexpectedResponseCodeException: \
            If a response indicates an error during the exchange
        */

        CoreSubsets coreSubsets = new CoreSubsets();
        coreSubsets.addProcessor(x, y, p);
        return this.getCpuInformation(coreSubsets).iterator().next();
    }
    
    public ArrayList<CPUInfo> getCpuInformation(CoreSubsets coreSubsets){
        /** Get information about the processors on the board

        @param core_subsets: A set of chips and cores from which to get the\
            information. If not specified, the information from all of the\
            cores on all of the chips on the board are obtained.
        @return: An iterable of the cpu information for the selected cores, or\
            all cores if core_subsets is not specified
        @rtype: iterable of \
            :py:class:`spinnman.model.CPUInfo`
        @raise spinnman.exceptions.SpinnmanIOException: \
            If there is an error communicating with the board
        @raise spinnman.exceptions.SpinnmanInvalidPacketException: \
            If a packet is received that is not in the valid format
        @raise spinnman.exceptions.SpinnmanInvalidParameterException:
            * If chip_and_cores contains invalid items
            * If a packet is received that has invalid parameters
            * If coreSubsets is null. TODO make work for SpiNNMachine if needed
        @raise spinnman.exceptions.SpinnmanUnexpectedResponseCodeException: \
            If a response indicates an error during the exchange
        */

        // Get all the cores if the subsets are not given
        if (coreSubsets == null){
            throw new UnsupportedOperationException();
            //TODO should do the following, but needs SPiNNMachine built in 
            // java to support it.
            /*if self._machine is None:
                self._update_machine()
            if self._machine is None:
                self._update_machine()
            core_subsets = CoreSubsets()
            for chip in self._machine.chips:
                for processor in chip.processors:
                    core_subsets.add_processor(
                        chip.x, chip.y, processor.processor_id)*/
        }
        
        process = GetCPUInfoProcess(this._scamp_connection_selector)
        cpu_info = process.get_cpu_info(core_subsets)
        return cpu_info
    
}
