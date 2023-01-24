package org.example;

import java.io.IOException;

import calc.GrowthContainer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FrameController {
    /**
     * Controller for Side-Menu
     */
    private boolean menuVisible = true;

    @FXML
    private ToggleButton inputvalues;

    @FXML
    private VBox menu;

    @FXML
    private ToggleButton monitoring;

    @FXML
    private Pane sidepane;

    @FXML
    private ToggleButton temperature;

    private Parent inputValues;
    private Parent monitoringParent;
    private Parent temperatureParent;

    /**
     * Method used to shrink Menu
     */
    @FXML
    void shrinkMenu() {
        if(menuVisible) {
            inputvalues.setText("");
            monitoring.setText("");
            temperature.setText("");
            menu.setPrefSize(21, 355);
            menuVisible = false;
        } else {
            inputvalues.setText("Input Values");
            monitoring.setText("Monitoring");
            temperature.setText("Temperature");
            menu.setPrefSize(172, 355);
            menuVisible = true;
        }
    }

    /**
     * Redirect to InputValues Site
     */
    @FXML
    void toInputValues(){
        sidepane.getChildren().clear();
        sidepane.getChildren().add(inputValues);
        inputValues.getStyleClass().clear();

    }

    /**
     * Redirect to Monitoring Site
     */
    @FXML
    void toMonitoring(){
        sidepane.getChildren().clear();
        sidepane.getChildren().add(monitoringParent);
    }

    /**
     * Redirect to Temperature Site
     */
    @FXML
    void toTemperature() {
        sidepane.getChildren().clear();
        sidepane.getChildren().add(temperatureParent);
    }

    /**
     * Called when Frame is loaded
     * Initializes other Controllers, hands them over to each other
     * @throws IOException on loading error
     */
    public void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("inputValues.fxml"));
        inputValues = fxmlLoader.load();
        sidepane.getChildren().add(inputValues);
        fxmlLoader = new FXMLLoader(App.class.getResource("monitoring.fxml"));
        monitoringParent = fxmlLoader.load();
        MonitoringController mc = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(App.class.getResource("temperature.fxml"));
        temperatureParent = fxmlLoader.load();
        TemperatureController tc = fxmlLoader.getController();
        mc.setController(tc);
    }
    @FXML
    public void reset() throws IOException {
        //todo: auch die logik reseten
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("inputValues.fxml"));
        inputValues = fxmlLoader.load();
        sidepane.getChildren().add(inputValues);
        fxmlLoader = new FXMLLoader(App.class.getResource("monitoring.fxml"));
        monitoringParent = fxmlLoader.load();
        sidepane.getChildren().clear();
        sidepane.getChildren().add(inputValues);
        MonitoringController mc = fxmlLoader.getController();
        fxmlLoader = new FXMLLoader(App.class.getResource("temperature.fxml"));
        temperatureParent = fxmlLoader.load();
        TemperatureController tc = fxmlLoader.getController();
        mc.setController(tc);
        GrowthContainer con =GrowthContainer.instance();
        con.reset();
    }
}
