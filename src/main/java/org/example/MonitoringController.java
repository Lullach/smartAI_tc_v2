package org.example;

import calc.*;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.util.Comparator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import calc.GrowthContainer;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MonitoringController implements Initializable, PropertyChangeListener {

	private XYChart.Series measurements;
	private FrameController frameController;
	private TemperatureController temperatureController;
	private LineChart<String,Number> chartMonitoring;
	private XYChart.Series predictions;
	private boolean allowMeasurements = true;
	private static final DecimalFormat df = new DecimalFormat("0.00");

	@FXML
	private GridPane gridpane;

	@FXML
	private Text devText;

	@FXML
	private Text finishedText;

	@FXML
	private Text tempText;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	measurements = new XYChart.Series();
    	measurements.setName("Measurements");
        GrowthContainer con = GrowthContainer.instance();
        con.addPropertyChangeListener(this);
		//Create chart
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		yAxis.setLabel("Confluency in %");
		//creating the chart
		chartMonitoring = new LineChart<>(xAxis,yAxis);
		chartMonitoring.setAnimated(false);
		chartMonitoring.setTitle("Confluency over Time");
		gridpane.add(chartMonitoring, 1, 1);
    }

    
	public void setController(FrameController fc, TemperatureController tc) {
		frameController = fc;
		temperatureController = tc;
	}

    // LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) "1",23
    
    /**
     * If notified by the GrowthContainer, this will do one of three things:
     * 1) add a measurement to the graph that was added to the container
     * 2) same as 1) but with a removed measurement
     * 3) start generating Predictions and displaying them on the chart, forwarded from container. Will also evaluate 
     *    the comparison of the latest measurement with the appropriate prediction
     * 4) in future versions, multiple predictions could be generated everytime a measurement is added
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
    	GrowthContainer con = GrowthContainer.instance();
    	if(e.getPropertyName().equals("mlist add")) {
    		MeasureOnAdded(con);
    	}else if (e.getPropertyName().equals("mlist rmv")) {
			//todo: wie Punkte aus diagram entfernen?
			chartMonitoring.getData().clear();
			measurements.getData().clear();
    		int size = con.getMListSize() - 1;
			for(int i = 0; i <= size; i++) {
				Measurement mTemp = con.getMeasure(i);
				measurements.getData().add(new XYChart.Data(mTemp.getTimeString(), mTemp.getConf()));
			}
			chartMonitoring.getData().clear();
			chartMonitoring.getData().add(measurements);
    	}else if (e.getPropertyName().equals("updated Phase to Log")) {
    		//PredictionOnUpdatedPhase(e,con);
    	}else if (e.getPropertyName().equals("start Predictions")) {
    		PredictionStart(con);
    	}
	}
    
    /**
     * displays the last measurement in con to the chart
     * @param con the container the measurement was added to
     */
    public void MeasureOnAdded(GrowthContainer con) {
		if(allowMeasurements) {
			System.out.println("Measurement added");
			int size = con.getMListSize() - 1;
			Measurement x = con.getMeasure(size);
			measurements.getData().add(new XYChart.Data(x.getTimeString(), x.getConf()));
			System.out.println(measurements.getData());
			chartMonitoring.getData().removeAll(measurements);
			chartMonitoring.getData().add(measurements);
		}
    }
    
    
    
    /** generates Predictions based on the second to last measurement in con and in future versions adds them to the screen
     * the last generated prediction will always be at the time of the latest measurement in con
     * @param con to read the last measurement from
     * currently it will generate 5 evenly spaced predictions
     * @throws IllegalArgumentException if con has less than three elements or something went wrong in pipelining
     */
    public void PredictionOnUpdatedPhase(PropertyChangeEvent e, GrowthContainer con) throws IllegalArgumentException{
    	try {
			allowMeasurements = false;
    		Prediction pred = new Prediction(0);
    		ArrayList<Prediction> list = pred.createPred(5, con); // I wanted to make createPred static but then it doesnt work for some reason
			XYChart.Series predictions = new XYChart.Series();
        	predictions.setName("Prediction");
        	for(Prediction x : list) {
        		predictions.getData().add(new XYChart.Data(x.getTimeString(), x.getConf()));
			}
			//todo: replace this by the correct function
			XYChart.Series deviation = new XYChart.Series();
			deviation.getData().add(new XYChart.Data("2000-05-06 04:20", 65));

			chartMonitoring.getData().remove(measurements);
        	chartMonitoring.getData().addAll(predictions, deviation);
			GrowthContainer container = GrowthContainer.instance();

			
    	} catch (IllegalArgumentException i) {
    		if(i.getMessage().contains("the specified container has not enough elements")){
    			System.out.println("\n in PREDICTION_ON_UPDATED_PHASE: " + i.getMessage() + "\n");
    		} else {
    			throw new IllegalArgumentException("in PREDICTION_ON_UPDATED_PHASE: Sth went wrong here: " + i.getMessage());
    		}
    	}
    }	
    
    /** after start is pressed, predictions are created & (hopefully) displayed based on the penultimate measurement.
     *  The last prediction is at the same time as the last (i.e. most recent) measurement.
     *  These two are then compared in evaluate().
     *
     * @param con to read the base and compare measurement from
     * @throws IllegalStateException if something went wrong in calculating the prediction
     * The base measurement is the one to be used for generating the prediction, while the comp measurement is used for evaluation
     */
    public void PredictionStart(GrowthContainer con) throws IllegalStateException{
            
            Measurement base = con.getMeasure(con.getMListSize()-2);
            Measurement comp = con.getMeasure(con.getMListSize()-1);
            
            Prediction prediction = new Prediction(0);
            //ArrayList<Prediction> list = prediction.createPred(5, base, con.getRate(), comp.getTime());  // I wanted to make createPred static but then it doesnt work for some reason
            prediction = prediction.createPred(base, con.getRate(), comp.getTime());
            System.out.println("Checkpoint MonitoringController->PredictionStart->Prediction ist:" + prediction.getTime().toString() +"      "+ prediction.getConf());
            if(!prediction.getTime().isEqual(comp.getTime())) {
                    throw new IllegalStateException("Berechnung haut net hin");
            }
            
            this.predictions = new XYChart.Series();
            this.predictions.setName("Prediction");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            predictions.getData().add(new XYChart.Data(prediction.getTime().format(formatter).toString(),prediction.getConf()));
            System.out.println("Checkpoint MonitoringController->PredictionStart->prediction wurde hinzugefügt");
            chartMonitoring.getData().clear();
            chartMonitoring.getData().add(measurements);
            chartMonitoring.getData().add(predictions);
            evaluate(comp,prediction,con.getRate());
            
            // Set Values of output Field
         	//todo: set the other text fields
         	//todo: set temeperature tab
         	finishedText.setText("Your cells will be ready at: " + con.calcFinalTime().format(formatter));
            
            /*
            for(Prediction x : list) {
                predictions.getData().add(new XYChart.Data(x.getTime().toString(),x.getConf()));
                //System.out.println(x.getConf()) -> nicht null müsste also gehen
            }
            System.out.println("Checkpoint MonitoringController->PredictionStart->predictions wurden erstellt");
            System.out.println(predictions.getData());
                chartMonitoring.getData().clear();
                chartMonitoring.getData().add(measurements);
            chartMonitoring.getData().add(predictions);
            Comparator<XYChart.Data> locComp = (d1, d2) -> ((String) d1.getXValue()).compareTo((String) d2.getXValue()) ;
            
            //chartMonitoring.getData().sort(???);
            */
            
    }
    
    /**
     * 
     * @param comp updated measurement
     * @param prediction to compare with the updated measurement
     * @param threshold determines the toleranz, within wich the prediction and measurement are interpreted as in agreement
     * @throws IllegalArgumentException if the times of prediction an comp dont match
     */
    private void evaluate(Measurement comp, Prediction prediction, double threshold) throws IllegalArgumentException{
        if (!comp.getTime().isEqual(prediction.getTime())){
                throw new IllegalArgumentException("in MonitoringController -> EVALUATE: Zeiten sind nicht gleich");
        } else{
			double dev = comp.getConf()-prediction.getConf();
			devText.setText("Current Deviation: " + df.format(dev));
			if(dev < threshold) {
				System.out.println("You have to make the cells grow faster!");
				temperatureController.setTemperature(1);
				tempText.setText("Adjust temperature by: 1 °C");
			}else if (comp.getConf()-prediction.getConf() > threshold) {
				System.out.println("You have to make the cells grow slower!");
				tempText.setText("Adjust temperature by: -1 °C");
			}else {
				System.out.println("Prediction and Measurement are in agreement!");
				tempText.setText("Adjust temperature by: 0 °C");
			}
		}
	}
}


    
