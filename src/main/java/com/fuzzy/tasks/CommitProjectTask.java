package com.fuzzy.tasks;

import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.Project;
import com.fuzzy.services.GitService;
import com.fuzzy.utils.FXUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

import java.util.ArrayList;
import java.util.List;

public class CommitProjectTask extends Task<Void> {

    private final Project project;
    private final AppController controller;

    public CommitProjectTask(Project project, AppController controller) {
        this.project = project;
        this.controller = controller;
    }

    @Override
    protected Void call() {
        Platform.runLater(() -> controller.getPROGRESS_BAR().progressProperty().bind(this.progressProperty()));
        try (Git git = GitService.git(project)) {
            // Создание коммита с сообщением
            Status status = git.status().call();
            List<String> commitList = new ArrayList<>();
            commitList.addAll(status.getAdded());
            commitList.addAll(status.getModified());
            // Добавление файлов в индекс
            for (String filePath : commitList) {
                git.add().addFilepattern(filePath).call();
            }
            for (String filePath : status.getRemoved()){
                git.rm().addFilepattern(filePath).setCached(true).call();
            }

            // Создание коммита с сообщением
            git.commit().setMessage("Your commit message").call();
            updateProgress(0, 0);

        } catch (Exception e) {
            Platform.runLater(() -> {
                        FXUtil.showErrorDialog("Коммит проекта", "Не удалось зафиксировать изменения в проекте");
                        controller.getPROGRESS_BAR().progressProperty().unbind();
                    }
            );
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void succeeded() {
        updateProgress(0, 0);
        Platform.runLater(() -> {
            controller.getPROGRESS_BAR().progressProperty().unbind();
            controller.updateFilesListViewer();
        });
    }

    @Override
    protected void failed() {
        updateProgress(0, 0);
        Platform.runLater(() -> {
            FXUtil.showErrorDialog("Коммит проекта", "Не удалось зафиксировать изменения в проекте");
                    controller.getPROGRESS_BAR().progressProperty().unbind();
                }
        );
        System.out.println("commit failed!");
    }
}
