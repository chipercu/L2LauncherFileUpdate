package com.fuzzy.fx_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ProjectController {

    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;

    @FXML
    private TextField gitUrl;

    @FXML
    private TextField projectName;

    @FXML
    private TextField projectPath;

    @FXML
    private Button saveButton;

    @FXML
    private Button selectPathButton;


    public AnchorPane getMAIN_ANCHOR_PANE() {
        return MAIN_ANCHOR_PANE;
    }

    public TextField getProjectName() {
        return projectName;
    }

    public TextField getProjectPath() {
        return projectPath;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getSelectPathButton() {
        return selectPathButton;
    }

    public TextField getGitUrl() {
        return gitUrl;
    }
}
