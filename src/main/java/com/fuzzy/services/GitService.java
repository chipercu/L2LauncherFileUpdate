package com.fuzzy.services;

import com.fuzzy.model.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GitService {

    public static void initProject(Project project) {
        try (Git git = Git.init().setDirectory(new File(project.getPath())).call()) {
            git.remoteAdd()
                    .setName("main")
                    .setUri(new URIish(project.getGitUrl()))
                    .call();
            System.out.println("Репозиторий успешно инициализирован в " + project.getPath());
        } catch (GitAPIException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addFile(Project project, File file) {
        // Открытие существующего репозитория
        try (Git git = Git.open(new File(project.getPath()))) {
            String canonicalPath = file.getPath().replace("\\", "/");

            double fileSizeInMB = file.length() / (1024.0 * 1024.0);
            if (fileSizeInMB > 99.9){
                Runtime.getRuntime().exec("git lfs track " + canonicalPath);
            }

            git.add()
                    .addFilepattern(canonicalPath) // Указание файла для добавления
                    .call();
            System.out.println("Добавлен файл: " + canonicalPath);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFile(Project project, File file) {
        // Открытие существующего репозитория
        try (Git git = Git.open(new File(project.getPath()))) {
            String canonicalPath = file.getPath().replace("\\", "/");
            git.rm().setCached(true)
                    .addFilepattern(canonicalPath) // Указание файла для добавления
                    .call();
            System.out.println("Убран файл: " + canonicalPath);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Git git(Project project) throws IOException {
        Repository repository = new FileRepositoryBuilder().setGitDir(new File(project.getPath() + "/.git")).build();
        return new Git(repository);
    }

    public static Status getStatus(Project project){
        try (Git git = git(project)){
            return git.status().call();
        } catch (GitAPIException | IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
