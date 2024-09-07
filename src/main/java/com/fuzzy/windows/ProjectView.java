package com.fuzzy.windows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.fx_controllers.ProjectController;
import com.fuzzy.model.PatchFile;
import com.fuzzy.model.Project;
import com.fuzzy.services.GitService;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;


public class ProjectView extends Stage {

    private final ProjectController controller;

    public ProjectView(AppController mainController) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Project.fxml"));
        AnchorPane anchorPane;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setScene(new Scene(anchorPane, 350, 400));
        setTitle("Project settings");
        this.controller = fxmlLoader.getController();
        setResizable(false);

        controller.getSelectPathButton().setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(this);
            if (file != null) {
                controller.getProjectPath().setText(file.getAbsolutePath());
            }
        });

        controller.getSaveButton().setOnAction(actionEvent -> {
            Project project = new Project();
            project.setName(controller.getProjectName().getText());
            project.setPath(controller.getProjectPath().getText());
            project.setGitUrl(controller.getGitUrl().getText());

            ProgressBar progressBar = mainController.getPROGRESS_BAR();
            progressBar.progressProperty().unbind();
            Task<Void> task = clientAnalizeTask(project, mainController);
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });
        setOnCloseRequest(event -> close());
        show();
    }

    private Task<Void> clientAnalizeTask(Project project, AppController mainController) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                File path = new File(project.getPath());
                try (Stream<Path> paths = Files.walk(path.toPath())) {
                    List<Path> list = paths.filter(Files::isRegularFile).toList();// Фильтруем только файлы;
                    for (int i = 0; i < list.size(); i++) {
                        File file = list.get(i).toFile();
                        updateProgress(i, list.size());
                    }
                    updateProgress(0, list.size());
                } catch (IOException e) {
                    e.printStackTrace(); // Обработка исключений
                }
                return null;
            }
        };

        task.setOnSucceeded(workerStateEvent -> {
            project.saveProject();
            GitService.initProject(project);
            System.out.println("Объект успешно сохранен в файл.");
            mainController.updateProjectsMenu();
            this.close();
        });
        return task;
    }


}
