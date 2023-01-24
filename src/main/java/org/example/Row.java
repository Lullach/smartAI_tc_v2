package org.example;

import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;

import java.time.LocalDateTime;


public class Row {
    /**
     * Used as a model class for the display of da
     */
    private String filename;
    private LocalDateTime time;
    private ImageView picture;
    private CheckBox action;

    public Row(String filename, LocalDateTime time, ImageView picture, CheckBox action) {
        this.filename = filename;
        this.time = time;
        this.picture = picture;
        this.action = action;
    }
}
