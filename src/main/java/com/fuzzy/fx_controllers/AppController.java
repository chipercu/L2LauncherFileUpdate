package com.fuzzy.fx_controllers;

import com.fuzzy.config.AppConfig;
import com.fuzzy.config.configsystem.ConfigBuilder;
import com.fuzzy.crypt.L2CryptUtil;
import com.fuzzy.model.file_edit.dat.DatFile;
import com.fuzzy.services.PathFilesManager;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.StringUtils;
import com.fuzzy.windows.DatEditor;
import com.fuzzy.windows.FileManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class AppController {

    private String[] extensions = {"", "dat", "ini", "int", "u", "dll"};

    @FXML
    private TextField CLIENT_PATH;

    @FXML
    private Button CLIENT_PATH_BUTTON;

    @FXML
    private ListView<Label> FILES_LIST;

    @FXML
    private TextField FILE_NAME_FILTER;

    @FXML
    private ComboBox<String> FILE_EXTENSION_FILTER;

    @FXML
    private Button FILE_MANAGER_BUTTON;

    @FXML
    private Button FULL_ANALIZE_BUTTON;

    @FXML
    private AnchorPane MAIN_ANCHOR_PANE;

    @FXML
    private MenuBar MENU_BAR;

    @FXML
    private ProgressBar PROGRESS_BAR;

    @FXML
    private Button TEST;

    @FXML
    private TreeView<AnchorPane> TREE_VIEW;

    @FXML
    void test() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\FuzzY\\IdeaProjects\\L2LauncherFileUpdate\\src\\main\\resources\\com\\fuzzy\\background"));
        File selectedDirectory = fileChooser.showOpenDialog(CLIENT_PATH.getScene().getWindow());
        if (selectedDirectory != null) {
            FXUtil.setBackground(MAIN_ANCHOR_PANE, selectedDirectory.getName());
        }

    }


    @FXML
    void openL2smr() throws Exception {
        if (!AppConfig.CLIENT_DIRECTORY.isEmpty()){
//            new L2smr();
        }
    }

    @FXML
    void openL2Tool() throws Exception {
//        new L2Tool();
    }

    @FXML
    void fullAnalyzeClient() {
        PathFilesManager.getInstance().fullInspectClient(PROGRESS_BAR);
    }

    @FXML
    void openFileManager() throws IOException {
        new FileManager();
    }

    @FXML
    void startGame(){
        File batchFile = new File(AppConfig.CLIENT_DIRECTORY + "\\startGame.bat");

        if (!batchFile.exists()){
            try {
                // Запись текста в файл
                String bat = " @echo off \n" +
                        "start \"\" \"" + AppConfig.CLIENT_DIRECTORY + "\\system\\l2.exe\"";
                Files.write(Path.of(batchFile.getAbsolutePath()), bat.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File clientExe = new File(AppConfig.CLIENT_DIRECTORY + "\\system\\l2.exe");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(batchFile.getAbsolutePath());
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClickGameClientPathFindButton() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File configPath = new File(AppConfig.CLIENT_DIRECTORY);
        if (!AppConfig.CONFIG_DIRECTORY.isEmpty() && configPath.exists()) {
            directoryChooser.setInitialDirectory(configPath);
        }
        // Открываем диалог выбора директории
        File selectedDirectory = directoryChooser.showDialog(CLIENT_PATH.getScene().getWindow());
        if (selectedDirectory != null) {
            // Заполняем поле пути
            String absolutePath = selectedDirectory.getAbsolutePath().replace("\\", "/");
            AppConfig.CLIENT_DIRECTORY = absolutePath;
            ConfigBuilder.update(AppConfig.class, false);
            CLIENT_PATH.setText(absolutePath);
            updateClientFolderTree();
        }
    }

    @FXML
    void initialize() {
        CLIENT_PATH.setText(AppConfig.CLIENT_DIRECTORY);
        FILE_NAME_FILTER.setText(AppConfig.LAST_FILE_NAME_FILTER);
        FILE_EXTENSION_FILTER.getItems().addAll(extensions);
        FXUtil.setBackground(MAIN_ANCHOR_PANE, "img_5.png");
        updateClientFolderTree();
        FILE_NAME_FILTER.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            ExtTreeItem value = (ExtTreeItem) TREE_VIEW.getFocusModel().getFocusedItem().getValue();
            updateFilesListViewer(value.getFolder());
        });

        FILE_EXTENSION_FILTER.setOnAction(actionEvent -> {
            ExtTreeItem value = (ExtTreeItem) TREE_VIEW.getFocusModel().getFocusedItem().getValue();
            updateFilesListViewer(value.getFolder());
        });
    }

    private void updateClientFolderTree() {
        if (AppConfig.CLIENT_DIRECTORY != null || !AppConfig.CLIENT_DIRECTORY.isEmpty()) {
            File rootDir = new File(AppConfig.CLIENT_DIRECTORY);
            TreeItem<AnchorPane> rootItem = new TreeItem<>(new ExtTreeItem(rootDir));

            PROGRESS_BAR.progressProperty().unbind();
            Task<Void> task = updateClientFolderTreeTask(rootItem, rootDir);
            PROGRESS_BAR.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
            TREE_VIEW.setRoot(rootItem);
            rootItem.setExpanded(true);
        }
    }

    private void buildTree(TreeItem<AnchorPane> treeItem, File file) {
        if (file.isDirectory()) {
            File[] files = Objects.requireNonNull(file.listFiles());
            for (File child : files) {
                if (child.isDirectory()) {
                    TreeItem<AnchorPane> childItem = new TreeItem<>(new ExtTreeItem(child));
                    treeItem.getChildren().add(childItem);
                    buildTree(childItem, child);
                }
            }
        }
    }

    public class ExtTreeItem extends AnchorPane {

        private final File folder;

        public ExtTreeItem(File folder) {
            this.folder = folder;
            Pane pane = new Pane();
            pane.setPrefHeight(20);
            pane.setPrefWidth(20);
            FXUtil.setBackground(pane, "folder.png");
            Label label = new Label();
            label.setPrefWidth(200);
            label.setPrefHeight(20);
            label.setLayoutX(20);
            label.setText(folder.getName());
            getChildren().addAll(pane, label);
            addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                FILE_NAME_FILTER.setText("");
                FILE_EXTENSION_FILTER.setValue(null);
                updateFilesListViewer(folder);
            });

        }

        public File getFolder() {
            return folder;
        }
    }

    public void updateFilesListViewer(File folder) {
        FILES_LIST.getItems().clear();
        File[] files = Objects.requireNonNull(folder.listFiles());
        Arrays.stream(files)
                .filter(file -> !file.isDirectory())
                .filter(file -> StringUtils.containsSubstring(file.getName(), FILE_NAME_FILTER.getText()))
                .filter(file -> file.getName().endsWith(FILE_EXTENSION_FILTER.getValue() == null ? "" : FILE_EXTENSION_FILTER.getValue()))
                .forEach(file -> {
                    Label label = new Label(file.getName());
                    label.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                        if (mouseEvent.getClickCount() == 2){
                            if (file.getName().endsWith("dat12")){
                                new Thread(datFileTask(file)).start();
                            } else if (file.getName().endsWith("unr")) {
//                                new L2smr(file.getName());
                            } else if (file.getName().endsWith("htm") || file.getName().endsWith("dat")) {
                                try {
                                    L2CryptUtil.decryptDat(file);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

//                                new TextEditor(decryptFile);
                            } else if (file.getName().endsWith(".dec")) {
//                                // Регулярное выражение для поиска версии в скобках
//                                Pattern pattern = Pattern.compile("\\(\\d+\\)");
//                                Matcher matcher = pattern.matcher(file.getName());
//                                int version = 0;
//                                if (matcher.find()) {
//                                    version = Integer.parseInt(matcher.group(0).substring(1, matcher.group(0).length() - 1));
//                                }
//
//                                File encrypt = new File(file.getAbsolutePath().replace(".dec", ".enc"));
//                                try (InputStream is = new FileInputStream(file);
//                                     OutputStream os = L2Crypt.getOutputStream(encrypt, version)) {
//
//                                    byte[] buffer = new byte[0x1000]; // Буфер на 4096 байт
//                                    int bytesRead;
//                                    // Чтение из файла и запись в новый зашифрованный файл
//                                    while ((bytesRead = is.read(buffer)) != -1) {
//                                        os.write(buffer, 0, bytesRead);
//                                    }
//                                    // Сообщение об успешном завершении
//                                    System.out.println("Шифрование завершено: " + encrypt.getAbsolutePath());
//
//                                } catch (IOException e) {
//                                    // Обработка исключений
//                                    System.err.println("Ошибка при обработке файла: " + e.getMessage());
//                                    e.printStackTrace(); // Логирование для отладки
//                                }
                            }
                        }
                    });
                    FILES_LIST.getItems().add(label);
                });
    }


//    private File decryptFile(File file){
//        File decrypt = new File(AppConfig.TEMP_DIRECTORY + file.getName() + ".dec");
//        try (InputStream is = L2Crypt.getInputStream(file); OutputStream os = new FileOutputStream(decrypt)){
//            byte[] buffer = new byte[0x1000];
//            int r;
//            while ((r = is.read(buffer)) != -1){
//                os.write(buffer, 0, r);
//            }
//            System.out.println("Дешифрование завершено: " + decrypt.getAbsolutePath());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        File versionDecrypt = new File(decrypt.getAbsolutePath().replace(".dec", "(" + L2Crypt.version + ").dec"));
//        decrypt.renameTo(versionDecrypt);
//        return versionDecrypt;
//    }

    private File encryptFile(File file){
        return file;
    }

    private Task<Void> datFileTask(File file){
        return new Task<>() {
            private DatFile datFile;
            private int progress = 0;
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    PROGRESS_BAR.progressProperty().bind(this.progressProperty());
                });
                updateProgressBar("Создание файла");
                datFile = new DatFile(file, PROGRESS_BAR);
                updateProgressBar("Декодирование файла");
                datFile.decodeL2DatFile();
                updateProgressBar("Парсинг файла");
                datFile.parseFile();
                updateProgressBar("Создание таблицы");
                datFile.parseColumns();
                updateProgressBar("Заполнение таблицы");
                datFile.parseData();
                updateProgressBar("Таблица создана");
                return null;
            }

            @Override
            protected void succeeded() {
                DatEditor datEditor = new DatEditor(datFile);
                updateProgressBar("Открытья редактора");
                Platform.runLater(datEditor::show);
                Platform.runLater(() -> {
                    PROGRESS_BAR.progressProperty().unbind();
                });
            }

            private void updateProgressBar(String progressInfo){
                progress++;
                updateProgress(progress, 7);
            }
        };
    }


    private Task<Void> updateClientFolderTreeTask(TreeItem<AnchorPane> treeItem, File file) {
        return new Task<>() {
            @Override
            protected Void call() {
                File[] files = Objects.requireNonNull(file.listFiles());
                long count = Arrays.stream(files).filter(File::isDirectory).count();
                int progress = 0;
                updateProgress(progress, count);
                for (File child : files) {
                    if (child.isDirectory()) {
                        updateProgress(++progress, count);
                        TreeItem<AnchorPane> childItem = new TreeItem<>(new ExtTreeItem(child));
                        treeItem.getChildren().add(childItem);
                        buildTree(childItem, child);
                    }
                }
                updateProgress(0, count);
                System.out.println("Finish scanning client folder");
                return null;
            }
        };
    }


}
