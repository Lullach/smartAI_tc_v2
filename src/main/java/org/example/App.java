package org.example;
/*
 * meist getestet
cells_10	bei 	20-01-2023 17:47
cells_30	bei 	23-01-2023 17:47
cells_50	bei		24-01-2023 02:47

Immer 1h zwischendurch
cells_10	bei 	20-01-2023 17:47
cells_30	bei 	20-01-2023 18:47
cells_50	bei		20-01-2023 19:47

Immer 1h zwischendurch und okayes ergebnis
cells_10	bei 	20-01-2023 17:47
cells_30	bei 	20-01-2023 18:47
cells_50	bei		20-01-2023 19:47
*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

	// once you start the app this process should also start

    /**
     *
     * @param stage primary stage to load in components
     * @throws IOException exception thrown if loadFXML() fals
     */
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFrame());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @return loaded fxml file
     * @throws IOException throws exception in case of error on load
     */
    private static Parent loadFrame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("frame.fxml"));
        return fxmlLoader.load();
    }
    /**
    * Main Programm
    **/
    public static void main(String[] args) {
		launch();
	}
	

}