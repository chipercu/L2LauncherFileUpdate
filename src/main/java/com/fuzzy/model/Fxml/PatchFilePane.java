package com.fuzzy.model.Fxml;

import com.fuzzy.enums.GitStatus;
import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.Project;
import com.fuzzy.services.GitService;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;

public class PatchFilePane extends AnchorPane {

    private final File file;
    private final AppController controller;
    private final Project project;
    private final GitStatus gitStatus;
    private final BooleanProperty lockProperty = new SimpleBooleanProperty();

    public PatchFilePane(File file, AppController controller, GitStatus gitStatus) {
        this.file = file;
        this.controller = controller;
        this.project = controller.getProject();
        this.gitStatus = gitStatus;
        getStyleClass().add("patch-file-pane");
        HBox pane = new HBox();
        pane.getStyleClass().add("patch-file-pane-box");
//        long fileSizeInBytes = file.length();
//        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
//        if (fileSizeInMB < 99.9){
        pane.getChildren().add(buildLockButton());
//        }
        pane.getChildren().addAll(buildIcon(), buildLabel());
        getChildren().add(pane);
    }

    private Button buildLockButton() {
        Button button = new Button();

        // Обновление псевдокласса на основе lockProperty
        lockProperty.addListener((observable, oldValue, newValue) -> {
                    button.getStyleClass().clear();
                    button.getStyleClass().add(newValue ? "patch-file-lock-button-lock" : "patch-file-lock-button-unlock");
                }
        );

        // Начальное состояние псевдокласса
        boolean isLocked = project.containBockedFile(file);
        button.getStyleClass().add(isLocked ? "patch-file-lock-button-lock" : "patch-file-lock-button-unlock");
        setLockProperty(isLocked);

        button.setOnAction(actionEvent -> new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                if (project.containBockedFile(file)) {
                    project.removeBlockedFile(file);
                } else {
                    project.addBockedFile(file);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                // Обновление lockProperty после изменения blockedFiles
                boolean isLocked = project.containBockedFile(file);
                Platform.runLater(() -> setLockProperty(isLocked));
                project.saveProject();
            }
        }).start());
        return button;
    }

    private Pane buildIcon() {
        Pane pane = new Pane();
        String imgName = FileUtils.getFileExtension(file.getName());
        pane.getStyleClass().add("patch-file-icon");
        pane.getStyleClass().add("patch-file-icon_unk");
        pane.getStyleClass().add("patch-file-icon-" + imgName);
        FXUtil.setTooltip(pane, gitStatus.name());
        return pane;
    }

    private Label buildLabel() {
        Label label = new Label(file.getName());
        label.setPrefHeight(26);
        label.setLayoutX(24);
        label.setTextFill(gitStatus.getColor());
        label.setContextMenu(buildCOntextMenu());

        label.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                if (file.getName().endsWith(".zip") || file.getName().endsWith(".rar")) {
                    try {
                        Runtime.getRuntime().exec("cmd /c start " + file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return label;
    }

    private ContextMenu buildCOntextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        switch (gitStatus) {
            case added, changed, committed, modified -> {
                MenuItem removeFromGit = new MenuItem("Удалить файл из Git");
                removeFromGit.setOnAction(actionEvent -> {

                    GitService.removeFile(project, new File(file.getAbsolutePath().replace(controller.getProject().getPath() + File.separator, "")));
                    controller.updateFilesListViewer();
                });
                contextMenu.getItems().addAll(removeFromGit);
            }
            case untracked -> {
                MenuItem addToGit = new MenuItem("Добавить файл в Git");
                addToGit.setOnAction(actionEvent -> {
//                    long fileSizeInBytes = file.length();
//                    double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
//                    if (fileSizeInMB > 99.9){
//                        Platform.runLater(() -> FXUtil.showErrorDialog("Git", "Нельзя добавлять в git файлы более 100мб"));
//                        return;
//                    }
                    GitService.addFile(project, new File(file.getAbsolutePath().replace(controller.getProject().getPath() + File.separator, "")));
                    controller.updateFilesListViewer();
                });
                contextMenu.getItems().addAll(addToGit);
            }
        }
        return contextMenu;
    }

    public void setLockProperty(boolean lockProperty) {
        this.lockProperty.set(lockProperty);
    }
}
