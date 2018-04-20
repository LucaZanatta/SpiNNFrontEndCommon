package spiNNMachineClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;


/**
 *
 * @author alan
 */
public class CoreSubset implements Comparable<CoreSubset>, Iterable {
    private final int x;
    private final int y;
    private final LinkedHashSet<Integer> processorIDs = new LinkedHashSet<>();
    
    public CoreSubset(int x, int y, ArrayList<Integer> processor_ids){
        this.x = x;
        this.y = y;
        for (int processorID : processor_ids) {
            this.addProcessor(processorID);
        }
    }
    
    public CoreSubset(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public final void addProcessor(int processorID){
        getProcessorIDs().add(processorID);
    }
    
    public boolean contains(int processorID){
        return this.processorIDs.contains(processorID);
    }
    
    @Override
    public Iterator iterator() {
        return this.processorIDs.iterator();
    }

    @Override
    public int compareTo(CoreSubset other) {
        if (this.equals(other)){
            return 0;
        }
        else{
            return 1;
        }
    }
    
    public boolean equals(CoreSubset other){
        return this.x == other.getX() && this.y == other.getY() && 
                this.processorIDs == other.getProcessorIDs();
    }
    
    @Override
    public int hashCode(){
        return this.x + this.y + this.processorIDs.hashCode();
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the processorIDs
     */
    public LinkedHashSet<Integer> getProcessorIDs() {
        return processorIDs;
    }
    
    public int size(){
        return this.processorIDs.size();
    }
    
    public String toString(){
        return "" + this.x + ":" + this.y + ":" + this.processorIDs.toString();
    }
    
}
