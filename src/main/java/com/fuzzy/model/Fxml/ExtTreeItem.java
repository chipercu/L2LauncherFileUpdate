package com.fuzzy.model.Fxml;

import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.services.GitService;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.FileUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ExtTreeItem extends AnchorPane {

    private AppController controller;
    private File folder;

    public ExtTreeItem(AppController controller, File folder) {
        this.controller = controller;
        this.folder = folder;
        getChildren().addAll(buildPane(), buildLabel());

        addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            controller.getFILE_NAME_FILTER().setText("");
            controller.getFILE_EXTENSION_FILTER().setValue(null);
            controller.updateFilesListViewer();
        });
    }


    private Pane buildPane() {
        Pane pane = new Pane();
        pane.setPrefHeight(20);
        pane.setPrefWidth(20);
        FXUtil.setBackground(pane, "file_icon/folder2.png");
        return pane;
    }

    private Label buildLabel() {
        Label label = new Label();
        label.setPrefWidth(200);
        label.setPrefHeight(20);
        label.setLayoutX(20);
        label.setText(folder.getName());

        ContextMenu contextMenu = new ContextMenu();
        MenuItem openFolder = new MenuItem("Открыть папку в новом окне");
        openFolder.setOnAction(actionEvent -> {
            try {
                Runtime.getRuntime().exec("cmd /c start " + folder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem toPatch = new MenuItem("Добавить всю папку в git");
        toPatch.setOnAction(actionEvent -> {
            Arrays.stream(Objects.requireNonNull(getFolder().listFiles()))
                    .filter(file -> Files.isRegularFile(file.toPath()))
                    .map(file -> FileUtils.relativePath(controller.getProject(), file))
                    .map(File::new)
                    .forEach(file -> GitService.addFile(controller.getProject(), file));
            controller.updateFilesListViewer();
        });

        MenuItem lock = new MenuItem("Запретить изменения файлов");
        lock.setOnAction(actionEvent -> {
            List<File> list = Arrays.stream(Objects.requireNonNull(getFolder().listFiles()))
                    .filter(file -> Files.isRegularFile(file.toPath()))
                    .toList();
            list.forEach(file -> controller.getProject().addBockedFile(file));
            controller.updateFilesListViewer();
        });
        MenuItem unlock = new MenuItem("Разрешить изменения файлов");
        unlock.setOnAction(actionEvent -> {
            List<File> list = Arrays.stream(Objects.requireNonNull(getFolder().listFiles()))
                    .filter(file -> Files.isRegularFile(file.toPath()))
                    .toList();
            list.forEach(file -> controller.getProject().removeBlockedFile(file));
            controller.updateFilesListViewer();
        });
        contextMenu.getItems().addAll(openFolder, toPatch, lock, unlock);

        label.setContextMenu(contextMenu);
        return label;
    }

    public AppController getController() {
        return controller;
    }

    public File getFolder() {
        return folder;
    }


}
