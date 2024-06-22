package com.orb.battambang.util;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;

public class CenteredListCell extends ListCell<Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            Text text = new Text(item.toString());
            HBox hbox = new HBox(text);
            hbox.setAlignment(Pos.CENTER); // Center the text in the HBox
            hbox.setPrefWidth(Double.MAX_VALUE); // Make HBox take up full width
            setGraphic(hbox);
        }
    }
}
