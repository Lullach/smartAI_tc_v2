package org.example;

import ImageAnalysis.ImageJClass;
import calc.GrowthContainer;
import calc.*;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InputValuesController{
    /**
     * Controller for the Input Values Site
     */
    private static Stage stage;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;
    @FXML
    private TableColumn<Row, String> action;

    @FXML
    private TableColumn<Row, String> filename;

    @FXML
    private TableColumn<Row, String> picture;

    @FXML
    private TableView<Row> table;

    @FXML
    private TableColumn<Row, String> time;

    private final ObservableList<Row> listRows = FXCollections.observableArrayList();
    private final ObservableList<Row> selectedRows = FXCollections.observableArrayList();
    private final Map<Row, Measurement> rowMeasurementMap = new HashMap<>();

    /**
     * Closes the Dialog to upload a new photo
     */
    public static void discardNewPhotoDialog() {
        stage.close();
    }

    /**
     * When start Button is clicked, the Method checks if 3 pictures uploaded
     * If yes, starts prediction
     * Otherwise, show alert
     */
    @FXML
    void startMeasurement() {
        // Todo: starte prediction hier!
        // Property Change support changes
        if(listRows.size() < 3) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Add at least 3 pictures");
            alert.show();
        } else {
            addButton.setDisable(true);
            deleteButton.setDisable(true);
            GrowthContainer con = GrowthContainer.instance();
            con.startPredictions();
        }

    }

    /**
     * When the add button is clicked, a new dialog to upload the picture is loaded
     * @throws IOException on loading the fxml
     */
    @FXML
    void openDialogPane() throws IOException {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( "newPhoto.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        NewPhotoController npc = fxmlLoader.getController();
        npc.setInputValuesController(this);
        stage.setTitle("Upload Picture");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * If the delete button is pressed, removes all pictures in table,
     * that are contained in selected rows list
     */
    @FXML
    void deletePictures() {
        GrowthContainer container = GrowthContainer.instance();
        Measurement measurement;
        for(Row row : selectedRows) {
            listRows.remove(row);
            measurement = rowMeasurementMap.get(row);
            container.removeMeasure(measurement);
        }
        if (listRows.size() < 4) {
            addButton.setDisable(false);
        }
        table.getItems().clear();
        table.getItems().addAll(listRows);
    }

    /**
     * Called from the NewPhotoController to add picture to the table
     * @param filename  of photo
     * @param file photofile
     * @param time of the photo
     */
    public void initializeTable(String filename, File file, LocalDateTime time) {
        System.out.println("Inside initialize Table");
        CheckBox cb = new CheckBox();
        ImageView iv = new ImageView();
        Image image = new Image(file.toURI().toString(), 50, 50, false, false);
        iv.setImage(image);
        Row row = new Row(filename, time, iv, cb);
        cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> selectedRow(row));
        listRows.add(row);
        if(listRows.size() > 2) {
            addButton.setDisable(true);
        }
        table.getItems().clear();
        table.getItems().addAll(listRows);
        ImageJClass ij = new ImageJClass();
        Measurement measure = ij.analyze(file.getPath(), time);
        rowMeasurementMap.put(row, measure);
        GrowthContainer container = GrowthContainer.instance();
        container.addMeasure(measure,true);
        stage.close();
    }

    /**
     * Called when Controller is loaded
     * Initializes the Table
     */
    public void initialize(){
        action.setCellValueFactory(new PropertyValueFactory<>("action"));
        filename.setCellValueFactory(new PropertyValueFactory<>("filename"));
        picture.setCellValueFactory(new PropertyValueFactory<>("picture"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        table.getItems().addAll(listRows);
        table.setPlaceholder(new Label("No content added"));
    }

    /**
     * When user selects or unselects a checkbox in the table
     * Adds or delets respective row from selectedRows Variable
     * @param row where checkbox is used
     */
    private void selectedRow(Row row) {
        if(selectedRows.contains(row)) {
            selectedRows.remove(row);
        }else {
            selectedRows.add(row);
        }
        System.out.println(selectedRows);
    }
}
