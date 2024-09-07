package com.fuzzy.tasks;

import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.PatchFile;
import com.fuzzy.utils.FXUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;

public class LockUnlockPatchFileTask extends Task<Void> {

    private final PatchFile patchFile;
    private final Button lockButton;
    private final AppController controller;

    public LockUnlockPatchFileTask(PatchFile patchFile, Button lockButton, AppController controller) {
        this.patchFile = patchFile;
        this.lockButton = lockButton;
        this.controller = controller;
    }

    @Override
    protected Void call() {
        if (patchFile != null){
            if (patchFile.isImmutable()) {
                patchFile.unlock();
            } else {
                patchFile.lock(controller.getProject());
            }
        }
        return null;
    }



    @Override
    protected void succeeded() {
        controller.getProject().saveProject();
        Platform.runLater(() -> FXUtil.setBackground(lockButton, patchFile.isImmutable() ? "lock_button.png" : "unlock_button.png"));
    }
}
