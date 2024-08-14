package com.fuzzy.model.Fxml;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class FX_ImmutableFile extends AnchorPane {

    private final String fileName;
    private final Label label;
    private final Button deleteButton;

    public FX_ImmutableFile(String fileName) {
        setPrefWidth(200);
        setPrefHeight(30);
        setStyle("-fx-background-color: rgba(147,147,147,0.58);");
        this.fileName = fileName;
        this.label = initLabel(200, 30);
        this.deleteButton = initDeleteButton(200, 30);
        addEventHandler(MouseEvent.MOUSE_ENTERED,mouseEvent -> label.setStyle("-fx-background-color: rgba(138,81,81,0.58);"));
        addEventHandler(MouseEvent.MOUSE_EXITED,mouseEvent -> label.setStyle("-fx-background-color: rgba(114,93,93,0.58);"));
        getChildren().addAll(label, deleteButton);
    }

    public String getFileName() {
        return fileName;
    }


    private Label initLabel(double width, double height){
        Label label = new Label();
        label.setPrefWidth(width - height - 4);
        label.setPrefHeight(height - 4);
        label.setLayoutX(2);
        label.setLayoutY(2);
        label.setText(" " + fileName);
        label.setStyle("-fx-background-color: rgba(114,93,93,0.58);");
        return label;
    }

    private Button initDeleteButton(double width, double height){
        Button button = new Button();
        button.setText("-");
        button.setPrefWidth(height);
        button.setPrefHeight(height - 4);
        button.setLayoutX(width - height);
        button.setLayoutY(2);
        return button;
    }

    public void setDeleteHandler(EventHandler<MouseEvent> handler){
        this.deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
    }

}
