package calc;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One measurement consists of the measurement time and the estimated confluency
 * For future versions of this program we also included a Growth rate specific to this measurement, though currently it isn't necessary
 * 
 * @author Lukas
 *
 */
public class Measurement implements Comparable<Measurement>{
	
	private final LocalDateTime time;
	private final double conf;

    public Measurement(double conf, LocalDateTime time) {
        this.time=time;
        this.conf=conf;
    }

    /**Calculates the growth rate by measuring the slope between DataPoints m1,m2 in % per second
	 * 
	 * @param m1 1st measurement
	 * @param m2 2nd measurement, i.e. has to be later
	 * @return the growth rate in % per second
	 * @throws IllegalArgumentException if m1 is before m2
	 */
	public static double calcGrowthRate(Measurement m1, Measurement m2) throws IllegalArgumentException{
		if (m1.getTime().isAfter(m2.getTime())) {
			throw new IllegalArgumentException("error calculating growth rate: m1 has to be before m2");
		}
    	double C1= m1.getConf();
    	double C2= m2.getConf();
    	LocalDateTime t1= m1.getTime();
    	LocalDateTime t2= m2.getTime();
    	Duration duration = Duration.between(t1, t2);
    	return (Math.log(C2-C1)/(duration.toSeconds()));
		//here we have confluency / Seconds
    }
    public LocalDateTime getTime() {
        return time;
    }
    
    public String getTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }
    public double getConf() {
        return conf;
    }

    /**
     *  note that we are only comparing by Time, not confluency and Time
     *  returns -1 of this.time is before o.getTime, 0 if they are the same (up to at least minutes), 1 if this.time is after o.getTime()
     */
	@Override
	public int compareTo(Measurement o) {
		return this.time.compareTo(o.getTime());
	}

	
}

