package calc;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** operational interface, containing and managing all measurements
 * 
 * @author Lukas
 * @threshold is the parameter used to differentiate log phase from other phases. If the growth rate is higher than this number, 
 * the phase is updated to log phase (and vice versa). Also functions as tolerance for evaluation.
 * In future version we distinguish evaluation from phase change and determine both these thresholds by examining the first few measurements.
 */
public class GrowthContainer implements Iterable<Measurement> {
	
	private static GrowthContainer unique = null;
	private GrowthPhase phase = GrowthPhase.NOTLOG;
	private final ArrayList<Measurement> mlist;
	private static double rate=0;			// the first measure will have that r=0 assigned, unit is usually % per hour
	
	//need to calculate these somehow
	private static final double threshold= 0.0000001; //when is log phase?
	
	//you can use beans to get notified everytime the contents in the container change
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	private GrowthContainer() {
		mlist = new ArrayList<>();
	}
	
	/**
	 * 
	 * @return since only one container can exist, it either returns an empty container or the current one
	 */
	public static GrowthContainer instance() {
		if(unique==null) {
			unique = new GrowthContainer();
		}
		return unique;
	}
	
	
	/**adds a new measurement to the container and updates phase&rate if necessary
	*  notifies all listeners after a measurement has been added
	*  if update is true then the Phase and growth rate are also updated.
	*  @throws IllegalArgumentException, if measure already exists in the container
	**/ 
    public void addMeasure(Measurement measure, boolean update) throws IllegalArgumentException{
    	if(mlist.contains(measure)) {
    		throw new IllegalArgumentException("measure already exists or time is sooner than start time");
    	}
    	
    	mlist.add(measure);
    	Collections.sort(mlist);
    	int n=mlist.size();
    	if(update && n==2) {
    		updatePhaseAndRate(threshold);	
    	}
    	changes.firePropertyChange("mlist add",null, measure);
    }
    /**
     * removes a measurement and updates growth rate and phase
     */
    public void removeMeasure(Measurement measure) {
    	boolean res=mlist.remove(measure);
    	if(res) {
			this.updatePhaseAndRate(threshold);
			changes.firePropertyChange("mlist rmv", measure, null);
		}
    }
    
    /**
     * 
     * @param index of measurement
     * @return the measurement at the specified index
     */
    public Measurement getMeasure(int index){
    	return mlist.get(index);
    }


    /**
     * estimates the time it takes to reach 90% confluency based on the last measurement and current growth rate
     * @return final time as LocalDateTime
     * @throws IllegalStateException in case log phase is not reached
     * @throws NumberFormatException in case final time can not be calculated
     * the double temp in this method is the difference between the last measure and the final time
     * it is converted into seconds, note that if that difference is e.g. 1 day, then we have ~80000 for temp.
     * that should'nt cause any bufferoverflows but just to keep that in mind, this number can get quite big
     * @throws NumberFormatException if there was an issue calculating the final time
     * @throws IllegalStateException growth phase is NOTLOG
     **/
    public LocalDateTime calcFinalTime() throws IllegalStateException, NumberFormatException{
  		Measurement measure=mlist.get(mlist.size()-2);
  		if(phase == GrowthPhase.NOTLOG) {
  			throw new IllegalStateException("cells have not reached log phase");
  		}
  		
  		double temp = Math.log((90)/measure.getConf())/rate;		//90 weil die confluency ist double mit z.B. conf = 58,2
  														
  		if (Double.isNaN(temp)||temp==0){
  			throw new NumberFormatException("error calculating the final time");
  		}
  		
  		return measure.getTime().plusSeconds((long) temp);
  	}
  	
  	
  	

  	
    /**
     * use this method to get a PropertyChangeEvent if the contents in the container get manipulated
     * @param l the specific listener
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
    	changes.addPropertyChangeListener(l);
    }
    
    /**
     * used exclusively by MonitoringController to forward user inputs to the graphical interface
     */
    public void startPredictions() {
        changes.firePropertyChange("start Predictions", false, true);
    }

    /**
     * resets the growthcontainer, i.e. the next method call of container.instance() is the same as when you called it the first time
     */
    public void reset() {
        	rate=0;
        	mlist.clear();
        	phase = GrowthPhase.NOTLOG;
    }
    
    
    
    /**updates Growthphase and Growthrate
    * If new growth rate is higher than some threshold -> log phase
    * Compares the most recent measurements in this container
    **/
    public void updatePhaseAndRate(double threshold) {
    	int n=mlist.size();
    	if(n<=1) {
    		rate=0;
    		phase=GrowthPhase.NOTLOG;
    		return;
    	}
    	double oldrate=this.getRate();
    	double temp = Measurement.calcGrowthRate(mlist.get(n-2),mlist.get(n-1)); //calculates growth rate of the most recent measurements
    	rate=temp;
    	changes.firePropertyChange("updated Rate", oldrate, temp);
    	if(rate > threshold) {
    		phase= GrowthPhase.LOG;
    		changes.firePropertyChange("updated Phase to Log", GrowthPhase.NOTLOG, GrowthPhase.LOG);
    	} else {
    		phase=GrowthPhase.NOTLOG;
    	}
    	
    }
   
    public int getMListSize(){
		return mlist.size();
	}
	
	public Iterator<Measurement> iterator() {
		return this.mlist.iterator();
	}

	public double getRate() {
		return rate;
	}
}
