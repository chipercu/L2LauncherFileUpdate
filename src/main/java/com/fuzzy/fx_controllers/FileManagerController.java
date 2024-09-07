package com.fuzzy.fx_controllers;

import com.fuzzy.utils.FXUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class FileManagerController {

    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;

    @FXML
    private ListView<AnchorPane> FILES_LIST;

    @FXML
    private ScrollPane FILES_SCROLL_PANE;

    @FXML
    private ListView<Label> FOLDER_LIST;

    @FXML
    private ScrollPane FOLDER_SCROLL_PANE;

    @FXML
    private TextField FILE_FILTER;

    @FXML
    private ProgressBar PROGRESS_BAR;

    @FXML
    private Label PROGRESS_LABLE;

    public Label getPROGRESS_LABLE() {
        return PROGRESS_LABLE;
    }

    public ProgressBar getPROGRESS_BAR() {
        return PROGRESS_BAR;
    }

    public ListView<AnchorPane> getFILES_LIST() {
        return FILES_LIST;
    }

    public ScrollPane getFILES_SCROLL_PANE() {
        return FILES_SCROLL_PANE;
    }

    public ListView<Label> getFOLDER_LIST() {
        return FOLDER_LIST;
    }

    public ScrollPane getFOLDER_SCROLL_PANE() {
        return FOLDER_SCROLL_PANE;
    }

    public AnchorPane getMAIN_ANCHOR_PANE() {
        return MAIN_ANCHOR_PANE;
    }

    public TextField getFILE_FILTER() {
        return FILE_FILTER;
    }

    @FXML
    void initialize(){
        FXUtil.setBackground(MAIN_ANCHOR_PANE, "img_6.png");
    }

}
