package com.fuzzy.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzzy.config.AppConfig;
import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.ClientFiles;
import com.fuzzy.model.Project;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.FileUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class PushProjectTask extends Task<Void> {

    private final Project project;
    private final AppController controller;

    public PushProjectTask(Project project, AppController controller) {
        this.project = project;
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception {
        try (Git git = Git.open(new File(project.getPath()))) {
            Status status = git.status().call();
            List<String> commitList = new ArrayList<>();
            commitList.addAll(status.getAdded());
            commitList.addAll(status.getModified());
            commitList.addAll(status.getRemoved());
            if (!commitList.isEmpty()){
                Platform.runLater(() ->
                        FXUtil.showErrorDialog(
                                "Push проекта",
                                "В проекте есть незафиксированные изменения, проверить файлы и зафиксируйте все изменения")
                );
                return null;
            }

            Platform.runLater(() -> controller.getPROGRESS_BAR().progressProperty().bind(this.progressProperty()));


            List<String> clientFileList = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(Paths.get(project.getPath()))) {
                clientFileList = paths
                        .filter(path -> !path.toString().contains(".git")) // Исключаем папку
                        .filter(path -> !path.toString().contains(".idea")) // Исключаем папку
                        .filter(Files::isRegularFile) // фильтруем только файлы
                        .map(Path::toFile)
                        .map(file -> FileUtils.relativePath(project, file))
                        .toList();// выводим пути к файлам
            } catch (IOException e) {
                e.printStackTrace();
            }




            File file = new File(project.getPath() + File.separator + project.getName() + ".json");
            ClientFiles clientFiles = new ClientFiles();
            clientFiles.setClientFileList(clientFileList);


            HashMap<String, String> blockedFilesMap = new HashMap<>();
            project.getBlockedFiles().forEach(blockedFile -> {
                String sha = FileUtils.getSHA(new File(project.getPath() + File.separator + blockedFile));
                blockedFilesMap.put(blockedFile, sha);
            });

            clientFiles.setBlockedFiles(blockedFilesMap);
            try {
                new ObjectMapper().writeValue(file, clientFiles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String canonicalPath = new File(project.getName() + ".json").getPath();
            git.add().addFilepattern(canonicalPath).call();
            updateProgress(1, 5);
            project.setVersion(project.getVersion() + 1);
            updateProgress(2, 5);
            project.saveProject();
            updateProgress(3, 5);
            git.commit().setMessage("Ver-" + project.getVersion()).call();
            updateProgress(4, 5);
            File sshDir = new File(FS.DETECTED.userHome(), "/.ssh");
            SshdSessionFactory sshSessionFactory = new SshdSessionFactoryBuilder()
                    .setPreferredAuthentications("publickey")
                    .setHomeDirectory(FS.DETECTED.userHome())
                    .setSshDirectory(sshDir)
                    .build(null);

            SshSessionFactory.setInstance(sshSessionFactory);
            try {
                git.push().setRemote(project.getGitUrl()).call();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            updateProgress(5, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void succeeded() {
        Platform.runLater(() ->
                        FXUtil.showSuccessDialog(
                                "Push проекта",
                                "Изменения успешно отправлены!"));
        updateProgress(0, 0);
        Platform.runLater(() -> controller.getPROGRESS_BAR().progressProperty().unbind());
    }
}
