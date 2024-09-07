package com.fuzzy.fx_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.config.configsystem.ConfigBuilder;
import com.fuzzy.enums.GitStatus;
import com.fuzzy.model.Fxml.ExtTreeItem;
import com.fuzzy.model.Fxml.PatchFilePane;
import com.fuzzy.model.Project;
import com.fuzzy.services.GitService;
import com.fuzzy.tasks.BuildPatchTask;
import com.fuzzy.tasks.CommitProjectTask;
import com.fuzzy.tasks.PushProjectTask;
import com.fuzzy.tasks.UpdateFolderTreeTask;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.StringUtils;
import com.fuzzy.windows.ProjectView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jgit.api.Status;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class AppController {

    public Project project;
    private final ObjectProperty<String> clientPath = new SimpleObjectProperty<>();
    private final ObjectProperty<String> projectName = new SimpleObjectProperty<>();

    @FXML
    private TextField CLIENT_PATH;
    @FXML
    private Button CLIENT_PATH_BUTTON;
    @FXML
    private ListView<AnchorPane> FILES_LIST;
    @FXML
    private TextField FILE_NAME_FILTER;
    @FXML
    private ComboBox<String> FILE_EXTENSION_FILTER;
    @FXML
    private Button BUILD_PATCH_BUTTON;
    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;
    @FXML
    private MenuBar MENU_BAR;
    @FXML
    private ProgressBar PROGRESS_BAR;
    @FXML
    private TreeView<AnchorPane> TREE_VIEW;
    @FXML
    private Menu projectsMenu;
    @FXML
    private MenuItem newProjectMenu;
    @FXML
    private MenuItem pushProject;
    @FXML
    private Button GIT_COMMIT_BUTTON;
    @FXML
    private Button GIT_PUSH_BUTTON;
    @FXML
    private AnchorPane PatchActionsPane;

    public ProgressBar getPROGRESS_BAR() {
        return PROGRESS_BAR;
    }

    public Menu getProjectsMenu() {
        return projectsMenu;
    }

    public TextField getCLIENT_PATH() {
        return CLIENT_PATH;
    }

    public Button getCLIENT_PATH_BUTTON() {
        return CLIENT_PATH_BUTTON;
    }

    public ListView<AnchorPane> getFILES_LIST() {
        return FILES_LIST;
    }

    public TextField getFILE_NAME_FILTER() {
        return FILE_NAME_FILTER;
    }

    public ComboBox<String> getFILE_EXTENSION_FILTER() {
        return FILE_EXTENSION_FILTER;
    }

    public Button getBUILD_PATCH_BUTTON() {
        return BUILD_PATCH_BUTTON;
    }

    public AnchorPane getMAIN_ANCHOR_PANE() {
        return MAIN_ANCHOR_PANE;
    }

    public MenuBar getMENU_BAR() {
        return MENU_BAR;
    }

    public TreeView<AnchorPane> getTREE_VIEW() {
        return TREE_VIEW;
    }

    public Project getProject() {
        return project;
    }

    public MenuItem getPushProject() {
        return pushProject;
    }

    public Button getGIT_COMMIT_BUTTON() {
        return GIT_COMMIT_BUTTON;
    }

    public Button getGIT_PUSH_BUTTON() {
        return GIT_PUSH_BUTTON;
    }

    public ObjectProperty<String> projectNameProperty() {
        return projectName;
    }


    @FXML
    void openL2smr() {
        if (!AppConfig.CLIENT_DIRECTORY.isEmpty()) {
//            new L2smr();
        }
    }

    @FXML
    void openL2Tool() {
//        new L2Tool();
    }

    @FXML
    void startGame() {
        if (project != null) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(project.getPath() + "\\system\\l2.exe");
            try {
                Process process = processBuilder.start();
                System.out.println(process.pid());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FXUtil.successWindow("Не выбран проект!");
        }
    }


    private void initBackgrounds() {
        FXUtil.setBackground(MAIN_ANCHOR_PANE, "img_5.png");
        FXUtil.setButtonBackground(GIT_COMMIT_BUTTON, "git_commit.png", "git_commit_over.png", "Создать коммит");
        FXUtil.setButtonBackground(GIT_PUSH_BUTTON, "git_push.png", "git_push_over.png", "Push проекта на гит");
        FXUtil.setButtonBackground(BUILD_PATCH_BUTTON, "archive.png", "archive_over.png", "Собрать патч в zip");
//        FXUtil.addTooltip(GIT_PUSH_BUTTON, "Push проекта на гит");
//        FXUtil.addTooltip(GIT_COMMIT_BUTTON, "Создать коммит");
        FILES_LIST.getStyleClass().add("patch-file-viewer");

    }

    @FXML
    void initialize() {
        loadProject(new File(AppConfig.LAST_PROJECT));
        initBackgrounds();
        FILE_NAME_FILTER.setText(AppConfig.LAST_FILE_NAME_FILTER);
        FILE_EXTENSION_FILTER.getItems().addAll("", "dat", "ini", "int", "u", "dll");
        newProjectMenu.setOnAction(actionEvent -> new ProjectView(this));
        CLIENT_PATH.textProperty().bind(clientPath);
        BUILD_PATCH_BUTTON.setOnAction(actionEvent -> new Thread(new BuildPatchTask(this)).start());
        FILE_NAME_FILTER.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> updateFilesListViewer());
        FILE_EXTENSION_FILTER.setOnAction(actionEvent -> updateFilesListViewer());
        CLIENT_PATH_BUTTON.setOnAction(actionEvent -> {
            try {
                ExtTreeItem value = (ExtTreeItem) TREE_VIEW.getFocusModel().getFocusedItem().getValue();
                Runtime.getRuntime().exec("cmd /c start " + value.getFolder());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        GIT_PUSH_BUTTON.setOnAction(actionEvent -> new Thread(new PushProjectTask(project, this)).start());
        GIT_COMMIT_BUTTON.setOnAction(actionEvent -> new Thread(new CommitProjectTask(project, this)).start());
        pushProject.setOnAction(actionEvent -> new Thread(new PushProjectTask(project, this)).start());
        updateProjectsMenu();
        updateClientFolderTree();
    }

    private void reloadCss(Scene scene) {
        scene.getStylesheets().clear();
        // Загружаем CSS-файл снова
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("style.css")).toExternalForm());
        System.out.println("CSS перезагружен!"); // Для отладки
    }

    private void loadProject(File file) {
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                project = objectMapper.readValue(file, Project.class);
                projectNameProperty().setValue(project.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateProjectsMenu() {
        projectsMenu.getItems().clear();
        File[] files = new File(AppConfig.PROJECTS_DIRECTORY).listFiles(pathname -> !pathname.isDirectory());
        if (files != null) {
            for (File file : files) {
                MenuItem menuItem = new MenuItem();
                menuItem.setText(file.getName().replace(".json", ""));
                menuItem.setOnAction(actionEvent -> {
                    AppConfig.LAST_PROJECT = file.getPath().replace("\\", "/");
                    ConfigBuilder.update(AppConfig.class, false);
                    loadProject(file);
                    clientPath.setValue(project.getPath());
                    updateClientFolderTree();
                });
                projectsMenu.getItems().add(menuItem);
            }
        }
    }

    public void updateClientFolderTree() {
        if (project != null) {
            File rootDir = new File(project.getPath());
            TreeItem<AnchorPane> rootItem = new TreeItem<>(new ExtTreeItem(this, rootDir));
            PROGRESS_BAR.progressProperty().unbind();
            UpdateFolderTreeTask updateFolderTree = new UpdateFolderTreeTask(rootItem, rootDir, this);
            PROGRESS_BAR.progressProperty().bind(updateFolderTree.progressProperty());
            new Thread(updateFolderTree).start();
            TREE_VIEW.setRoot(rootItem);
            rootItem.setExpanded(true);
        }
    }

    public void updateFilesListViewer() {
        getFILES_LIST().getItems().clear();
        ExtTreeItem extTreeItem = (ExtTreeItem) getTREE_VIEW().getFocusModel().getFocusedItem().getValue();
        File[] files = Objects.requireNonNull(extTreeItem.getFolder().listFiles());
        Status status = GitService.getStatus(getProject());
        Arrays.stream(files)
                .filter(file -> !file.isDirectory())
                .filter(file -> StringUtils.containsSubstring(file.getName(), getFILE_NAME_FILTER().getText()))
                .filter(file -> file.getName().endsWith(getFILE_EXTENSION_FILTER().getValue() == null ? "" : getFILE_EXTENSION_FILTER().getValue()))
                .forEach(file -> {
                            GitStatus gitStatus;
                            String filename = file.getAbsolutePath()
                                    .replace(getProject().getPath() + File.separator, "")
                                    .replace("\\", "/");

                            if (status.getModified().contains(filename)) {
                                gitStatus = GitStatus.modified;
                            } else if (status.getRemoved().contains(filename)) {
                                gitStatus = GitStatus.removed;
                            } else if (status.getUntracked().contains(filename)) {
                                gitStatus = GitStatus.untracked;
                            } else if (status.getChanged().contains(filename)) {
                                gitStatus = GitStatus.changed;
                            } else if (status.getAdded().contains(filename)) {
                                gitStatus = GitStatus.added;
                            } else if (status.getMissing().contains(filename)) {
                                gitStatus = GitStatus.missing;
                            } else {
                                gitStatus = GitStatus.committed;
                            }
                            getFILES_LIST().getItems().add(new PatchFilePane(file, this, gitStatus));
                        }
                );
    }

}
