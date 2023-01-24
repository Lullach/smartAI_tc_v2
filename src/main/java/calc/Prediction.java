package calc;


import java.time.LocalDateTime;
import java.time.Duration;

/**
 * similar to Measurement, a prediction has both a predicted confluency and time.
 * For future versions it might be helpful to combine Measurement and Prediction into one abstract class.
 * @author Lukas
 */
public class Prediction {
	private final double conf;
	private final LocalDateTime time;
	
	public Prediction(double confluency, LocalDateTime time) {
		this.conf=confluency;
		this.time=time;
	}
	
	public Prediction(double confluency) {
		this.conf=confluency;
		this.time=LocalDateTime.now();
	}
	
	/** one prediction based on measurement at time endTime
    *
    * @param m1 is the first measurement
    * @param rate is the growth rate
    * @param endTime is the end time of measurement
    * @return the desired Prediction
    * @throws IllegalArgumentException if growth rate is null
    */
   public Prediction createPred(Measurement m1, double rate, LocalDateTime endTime) throws IllegalArgumentException{
           if(rate==0) {
                   throw new IllegalArgumentException("in create Pred(measurement): rate is zero \n");
           }
           LocalDateTime m1Time=m1.getTime();
           double m1Conf=m1.getConf();
           
           Duration duration=Duration.between(m1Time, endTime);
           System.out.println("in Prediction->createPred(Measurement): Checkpoint: m1Conf " + m1Conf);
           System.out.println("in Prediction->createPred(Measurement): Duration: " + duration.toSeconds() + " Rate: "+ rate);
           double tempConf=m1Conf * Math.exp(rate * duration.toSeconds());
           if(tempConf>100) {
        	   tempConf=100;
        	   System.out.println("in Prediction->createPred(Measurement)->tempConf>100, deswegen auf 100 gesetzt");
           }
       return new Prediction(tempConf,endTime);
           
   }
	public LocalDateTime getTime() {
        return time;
    }

    public double getConf() {
        return conf;
    }

}
