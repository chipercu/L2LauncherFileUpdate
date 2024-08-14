package com.fuzzy.fx_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class DatEditorController {


    @FXML
    private ListView<Label> ALL_ELEMENTS_VIEWER;

    @FXML
    private ListView<Label> ELEMENT_DATA_VIEWER;

    @FXML
    private TextField ID_FILTER;

    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;

    @FXML
    private Button SAVE_BUTTON;

    @FXML
    private TextField TEXT_FILTER;


    public ListView<Label> getALL_ELEMENTS_VIEWER() {
        return ALL_ELEMENTS_VIEWER;
    }

    public ListView<Label> getELEMENT_DATA_VIEWER() {
        return ELEMENT_DATA_VIEWER;
    }

    public TextField getID_FILTER() {
        return ID_FILTER;
    }

    public AnchorPane getMAIN_ANCHOR_PANE() {
        return MAIN_ANCHOR_PANE;
    }

    public Button getSAVE_BUTTON() {
        return SAVE_BUTTON;
    }

    public TextField getTEXT_FILTER() {
        return TEXT_FILTER;
    }
}
