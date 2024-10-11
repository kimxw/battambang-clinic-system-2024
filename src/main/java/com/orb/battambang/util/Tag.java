package com.orb.battambang.util;

import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Rectangle;

public class Tag {
    Label label;
    Rectangle rectangle;
    String colourLight;
    String colourDark;
    public Tag(Label label, Rectangle rectangle, String colourLight, String colourDark) {
        this.label = label;
        this.rectangle = rectangle;
        this.colourLight = colourLight;
        this.colourDark = colourDark;
    }

    public Tag(ToggleButton toggleButton, String colourLight, String colourDark) {
        //initialise toggle button reference
        this.colourLight = colourLight;
        this.colourDark = colourDark;
    }

//    public static String getTagString() {
//
//    }

    public void updateTag(String tagSequence) {
        if (this.label == null || this.rectangle == null) {
            return;
        }
        if (tagSequence.contains(label.getText().substring(0, 1))) {
            rectangle.setStyle("-fx-fill: " + colourLight + "; -fx-stroke: " + colourDark);
            label.setStyle("-fx-text-fill: " + colourDark);
        } else {
            rectangle.setStyle("-fx-fill: #e8e8e8; -fx-stroke: #c4c4c4");
            label.setStyle("-fx-fill: #c4c4c4; -fx-opacity: 0.45");
        }
    }
}
