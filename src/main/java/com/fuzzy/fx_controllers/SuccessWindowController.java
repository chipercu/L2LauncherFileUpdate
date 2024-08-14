package com.fuzzy.fx_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

public class SuccessWindowController {

    @FXML
    private Button CONFIRM_BUTTON;

    @FXML
    private Label TEXT;


    public Button getCONFIRM_BUTTON() {
        return CONFIRM_BUTTON;
    }

    public Label getTEXT() {
        return TEXT;
    }
}
