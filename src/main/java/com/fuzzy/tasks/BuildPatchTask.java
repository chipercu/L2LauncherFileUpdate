package com.fuzzy.tasks;

import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.Fxml.ExtTreeItem;
import com.fuzzy.model.PatchFile;
import com.fuzzy.model.Project;
import com.fuzzy.utils.FXUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BuildPatchTask extends Task<Void> {

    private final AppController controller;

    public BuildPatchTask(AppController controller) {
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception {
        if (controller.getProject() == null){
            FXUtil.successWindow("Не выбран проект!");
            throw new NullPointerException("Not found project");
        }

        Platform.runLater(() -> {
            controller.getPROGRESS_BAR().progressProperty().unbind();
            controller.getPROGRESS_BAR().progressProperty().bind(this.progressProperty());
            controller.getBUILD_PATCH_BUTTON().setDisable(true);
        });
        Project project = controller.getProject();
        File zipFile = new File(project.getPath() + File.separator +
                "Patch_" + project.getName() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".zip");
        List<File> list = new ArrayList<>();
//                project.getPatchFilesMap()
//                .values()
//                .stream()
//                .filter(PatchFile::isPatch_include)
//                .map(patchFile -> new File(project.getPath() + File.separator + patchFile.getFolder() + patchFile.getFileName()))
//                .toList();

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            File root = new File(project.getPath());

            for (int i = 0; i < list.size(); i++) {
                File file = list.get(i);
                if (file.exists()) {
                    String zipEntryName = root.toURI().relativize(file.toURI()).getPath();
                    zipOut.putNextEntry(new ZipEntry(zipEntryName));
                    Files.copy(file.toPath(), zipOut);
                    zipOut.closeEntry();
                    updateProgress(i, list.size());
                }
            }
        }
        return null;
    }

    @Override
    protected void succeeded() {
        updateProgress(0, 0);
        controller.updateFilesListViewer();
        Platform.runLater(() -> controller.getBUILD_PATCH_BUTTON().setDisable(false));
    }
}
