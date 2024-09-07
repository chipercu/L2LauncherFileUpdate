package com.fuzzy.tasks;

import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.Fxml.ExtTreeItem;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class UpdateFolderTreeTask extends Task<Void> {

    private final TreeItem<AnchorPane> treeItem;
    private final File file;
    private final AppController controller;

    public UpdateFolderTreeTask(TreeItem<AnchorPane> treeItem, File file, AppController controller) {
        this.treeItem = treeItem;
        this.file = file;
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception {
        File[] files = Objects.requireNonNull(file.listFiles());
        long count = Arrays.stream(files).filter(File::isDirectory).count();
        int progress = 0;
        updateProgress(progress, count);
        for (File child : files) {
            if (child.getName().equalsIgnoreCase(".git")){
                continue;
            }
            if (child.isDirectory()) {
                updateProgress(++progress, count);
                TreeItem<AnchorPane> childItem = new TreeItem<>(new ExtTreeItem(controller, child));
                treeItem.getChildren().add(childItem);
                buildTree(childItem, child, controller);
            }
        }
        updateProgress(0, count);
        System.out.println("Finish scanning client folder");

        return null;
    }

    private void buildTree(TreeItem<AnchorPane> treeItem, File file, AppController controller) {
        if (file.isDirectory()) {
            File[] files = Objects.requireNonNull(file.listFiles());
            for (File child : files) {
                if (child.isDirectory()) {
                    TreeItem<AnchorPane> childItem = new TreeItem<>(new ExtTreeItem(controller, child));
                    treeItem.getChildren().add(childItem);
                    buildTree(childItem, child, controller);
                }
            }
        }
    }
}
