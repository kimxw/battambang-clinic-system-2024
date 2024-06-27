package com.orb.battambang.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class WrappedTextCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> param) {
        return new TableCell<T, String>() {
            private final Text text;

            {
                text = new Text();
                text.setWrappingWidth(param.getPrefWidth() - 10); // Adjust this value as needed
                text.wrappingWidthProperty().bind(param.widthProperty().subtract(10)); // Bind to column width
                setGraphic(text);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item);
                }
            }
        };
    }
}