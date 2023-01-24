package org.example;

import calc.GrowthContainer;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class TemperatureController {
    /**
     * Controller for the temperature site
     */
    private XYChart.Series<String, Number> measurements = new XYChart.Series<>();

    @FXML
    private LineChart<String, Number> lineChart;

    private int currentTemperature = 31;

    /**
     * Set temperature in the diagram
     * @param tempChange adjustment of temperature with this value
     */
    public void setTemperature(int tempChange) {

        GrowthContainer con = GrowthContainer.instance();
        int size = con.getMListSize();
        measurements.getData().clear();
        for(int i = 0; i < size; i++) {
            if(i < size -1) {
                measurements.getData().add(new XYChart.Data<>(con.getMeasure(i).getTimeString(), currentTemperature));
            } else {
                currentTemperature = currentTemperature + tempChange;
                measurements.getData().add(new XYChart.Data<>(con.getMeasure(i).getTimeString(), currentTemperature));
            }
        }
        lineChart.getData().clear();
        lineChart.getData().add(measurements);
    }

    /**
     * Called when loaded, sets initial values of table
     */
    public void initialize() {
        measurements = new XYChart.Series<>();
        measurements.setName("Temperatures");
        lineChart.setAnimated(false);
    }
}
