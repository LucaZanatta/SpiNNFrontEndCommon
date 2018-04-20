package spiNNMachineClasses;

import commonClasses.ChipLocation;
import commonClasses.CoreLocation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author alan
 */
public class CoreSubsets implements Iterable{
    private LinkedHashMap<ChipLocation, CoreSubset> coreSubsets = 
        new LinkedHashMap<>();
    
    public CoreSubsets(CoreSubsets to_add){
        Iterator itr = to_add.iterator();
        while(itr.hasNext()){
            CoreSubset element = (CoreSubset) itr.next();
            this.add_core_subset(element);
        }
    }
    
    public CoreSubsets(){}
    
    public final void add_core_subset(CoreSubset coreSubset){
        /** Add a core subset to the set

        @param core_subset: The core subset to add
        @return: Nothing is returned
        */
        ChipLocation loc = new ChipLocation(
            coreSubset.getX(), coreSubset.getY());
        if (!this.coreSubsets.containsKey(loc)){
            this.coreSubsets.put(loc, coreSubset);
        }
        else{
            for (int processorID : coreSubset.getProcessorIDs()){
                this.coreSubsets.get(loc).addProcessor(processorID);
            }
        }
    }
    
    public void add_core_subsets(ArrayList<CoreSubset> coreSubsets){
        /** merges a core subsets into this one

        @param core_subsets: the core subsets to add
        @return void
        */
        for (CoreSubset coreSubset : coreSubsets){
            this.add_core_subset(coreSubset);
        }
    }

    public void add_processor(int x, int y, int processor_id){
        /** Add a processor on a given chip to the set

        @param x: The x-coordinate of the chip
        @param y: The y-coordinate of the chip
        @param processor_id: A processor id
        @return: Nothing is returned
        */
        ChipLocation xy = new ChipLocation(x, y);
        if (!this.coreSubsets.containsKey(xy)){
            this.add_core_subset(new CoreSubset(x, y, new ArrayList<>()));
        this.coreSubsets.get(xy).addProcessor(processor_id);
        }
    }

    public boolean isChip(int x, int y){
        /** Determine if the chip with coordinates (x, y) is in the subset

        @param x: The x-coordinate of a chip
        @param y: The y-coordinate of a chip
        @return: True if the chip with coordinates (x, y) is in the subset
        */
        ChipLocation xy = new ChipLocation(x, y);
        return this.coreSubsets.containsKey(xy);
    }

    public boolean isCore(int x, int y, int processorID){
        /** Determine if there is a chip with coordinates (x, y) in the\
            subset, which has a core with the given id in the subset

        @param x: The x-coordinate of a chip
        @param y: The y-coordinate of a chip
        @param processorID: The id of a core
        @return: Whether there is a chip with coordinates (x, y) in the\
            subset, which has a core with the given id in the subset
        */
        ChipLocation xy = new ChipLocation(x, y);
        if (this.coreSubsets.containsKey(xy)){
           return this.coreSubsets.get(xy).contains(processorID);
        }
        return false;
    }
        
    public Iterator getCoreSubsets(){
        /** The one-per-chip subsets

        @return: Iterable of core subsets
        */
        return this.coreSubsets.values().iterator();
    }

    public CoreSubset get_core_subset_for_chip(int x, int y){
        /** Get the core subset for a chip

        @param x: The x-coordinate of a chip
        @param y: The y-coordinate of a chip
        @return: The core subset of a chip, which will be empty if not added
        */
        ChipLocation xy = new ChipLocation(x, y);
        if(this.coreSubsets.containsKey(xy)){
            return this.coreSubsets.get(xy);
        }
        else{
            return new CoreSubset(x, y);
        }
    }
    
    @Override
    public Iterator iterator() {
        return this.coreSubsets.values().iterator();
    }

    public int size(){
        /** The total number of processors that are in these core subsets
        @return n cores
        */
        int sum = 0;
        for (CoreSubset subset : this.iterator()) {
            sum += subset.size();
        }
        return sum;

    public bool contains(CoreLocation xy){
        /** True if the given coordinates are in the set

        @param xy:\
            Either a 2-tuple of x, y coordinates or a 3-tuple or x, y,\
            processor_id coordinates
        @return True if the given coordinates are in the set false otherwise
        */
        if(xy.get_p() == null){
            return this.isChip(xy.x, xy.y);
        return this.isCore(xy.x, xy.y, xy.p);
        
    public bool contains(ChipLocation xy){
        /** True if the given coordinates are in the set

        @param xy:\
            Either a 2-tuple of x, y coordinates or a 3-tuple or x, y,\
            processor_id coordinates
        @return True if the given coordinates are in the set false otherwise
        */
    }

    def __getitem__(self, x_y_tuple):
        """ The core subset for the given x, y tuple
        """
        return self._core_subsets[x_y_tuple]

    def __repr__(self):
        """ Human-readable version of the object

        :return: string representation of the CoreSubsets
        """
        output = ""
        for xy in self._core_subsets:
            output += str(xy)
        return output

    
}
