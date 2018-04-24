package spiNNManClasses.processes.impls;

import static spiNNManClasses.Constants.CPU_INFO_BYTES;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import spiNNMachineClasses.CoreSubset;
import spiNNMachineClasses.CoreSubsets;
import spiNNManClasses.model.CPUInfo;
import spiNNManClasses.processes.abstractClasses.AbstractMultiConnectionProcess;
import spiNNManClasses.processes.connectionSelectors.MostDirectConnectionSelector;

/**
 *
 * @author alan
 */
public class GetCPUInfoProcess extends AbstractMultiConnectionProcess {
    private List<CPUInfo> cpu_info;

    public GetCPUInfoProcess(MostDirectConnectionSelector connectionSelector) {
        super(connectionSelector);
        cpu_info = new ArrayList<>();
    }

    private void handleResponse(int x, int y, int p, ByteBuffer responseData,
            int responseOffset) {
        try {
            cpu_info.add(new CPUInfo(x, y, p, responseData, responseOffset));
        } catch (UnsupportedEncodingException e) {
            // Shouldn't happen
        }
    }

    public List<CPUInfo> get_cpu_info(CoreSubsets core_subsets) {
        for (CoreSubset core_subset : core_subsets) {
            final int x = core_subset.getX();
            final int y = core_subset.getY();

            for (final int p : core_subset.getProcessorIDs()) {
                sendRequest(new ReadMemory(x, y, get_vcpu_address(p),
                        CPU_INFO_BYTES), (buffer, offset) -> {
                            handleResponse(x, y, p, buffer, offset);
                        });
            }
        }
        finish();
        checkForError();
        return cpu_info;
    }
}
