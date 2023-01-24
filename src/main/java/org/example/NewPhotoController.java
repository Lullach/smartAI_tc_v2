package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NewPhotoController {
    /**
     * Dialog is opened when user clicks add in the input values site
     * allows upload of photos
     */

    private File file;

    @FXML
    private ImageView fotodepictor;

    @FXML
    private TextField timeInput;

    @FXML
    private Label fileNamePanel;

    @FXML
    private InputValuesController ivc;

    /**
     * opens local storage
     * displays the foto and filename on screen
     */
    @FXML
    void selectFile() {
        Stage stage = new Stage();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(imageFilter);
        file = fc.showOpenDialog(stage);
        if (file != null) {
            fotodepictor.setImage(new Image(file.toURI().toString()));
            fileNamePanel.setText(file.getName());
            fileNamePanel.setTextFill(Color.color(0,0,0));
        }
    }

    /**
     * When user clicks the save button
     * checks if all fields are filled with value
     * if yes, closes stage
     */
    @FXML
    void save() {
        String time = timeInput.getText();
        if(time == null || fileNamePanel.getText().isEmpty() || file == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("File or Time Input Missing!");
            alert.show();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            try {
                LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                ivc.initializeTable(fileNamePanel.getText(), file, dateTime);
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Date must be in format dd-MM-yyyy HH:mm");
                alert.show();
            }
        }

    }

    /**
     * closes dialog without adding anything to table
     */
    @FXML
    void discard() {
        InputValuesController.discardNewPhotoDialog();
    }

    /**
     * When loaded inputvaluescontroller is set
     * can be used to call methods in the class
     * @param controller inputvaluescontroller class
     */
   public void setInputValuesController(InputValuesController controller) {
        ivc = controller;
    }
}
