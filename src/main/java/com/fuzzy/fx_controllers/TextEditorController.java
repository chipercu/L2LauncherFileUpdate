package com.fuzzy.fx_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;

import javax.swing.text.html.HTMLEditorKit;

public class TextEditorController {

    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;

    @FXML
    private Button SAVE_BUTTON;

    @FXML
    private TextArea TEXT_AREA;

    @FXML
    private WebView web;


    public AnchorPane getMAIN_ANCHOR_PANE() {
        return MAIN_ANCHOR_PANE;
    }

    public Button getSAVE_BUTTON() {
        return SAVE_BUTTON;
    }

    public TextArea getTEXT_AREA() {
        return TEXT_AREA;
    }

    public WebView getWeb() {
        return web;
    }
}
