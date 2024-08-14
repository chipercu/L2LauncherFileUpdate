package com.fuzzy.windows;

import com.fuzzy.App;
import com.fuzzy.fx_controllers.FileManagerController;
import com.fuzzy.model.PatchFile;
import com.fuzzy.model.file_edit.dat.DatFile;
import com.fuzzy.services.PathFilesManager;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.FileUtils;
import com.fuzzy.utils.StringUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileManager extends Stage {

    private final FileManagerController controller;

    private static final List<String> editFileTypes = new ArrayList<>(){{
        add("dat");add("ini");
        add("txt");add("log");
    }};

    public FileManager() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("FileManager.fxml"));
        setScene(new Scene(fxmlLoader.load(), 800, 600));
        setTitle("File Manager");

        this.controller = fxmlLoader.getController();

        ListView<Label> folderList = controller.getFOLDER_LIST();

        Map<String, List<PatchFile>> groupByFolder = PathFilesManager.getInstance().groupByFolder();
        groupByFolder.keySet().forEach(s -> {
            Label label = new Label(s);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                updateListFile(controller, groupByFolder.get(s));
            });
            folderList.getItems().add(label);
        });

        controller.getFILE_FILTER().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            List<PatchFile> list = PathFilesManager.getInstance().getPatchFiles().stream()
                    .filter(patchFile -> StringUtils.containsSubstring(patchFile.getFileName(), controller.getFILE_FILTER().getText()))
                    .toList();
            updateListFile(controller, list);
        });

        show();
    }


    private void updateListFile(FileManagerController controller, List<PatchFile> patchFiles){
        controller.getFILES_LIST().getItems().clear();
        for (PatchFile pathFile: patchFiles) {

            EventHandler<MouseEvent> lockHandler = mouseEvent -> {
                pathFile.setImmutable(!pathFile.isImmutable());
                updateListFile(controller, PathFilesManager.getInstance().getPatchFiles());
            };
            AnchorPane anchorPane = buildPathFilePane(pathFile, lockHandler);
            controller.getFILES_LIST().getItems().add(anchorPane);
        }
    }

    /**
     *       <AnchorPane prefHeight="28.0" prefWidth="385.0">
     *          <children>
     *             <Label layoutX="3.0" prefHeight="26.0" prefWidth="274.0" text="File Name" AnchorPane.bottomAnchor="1.0" AnchorPane.topAnchor="1.0" />
     *             <Button mnemonicParsing="false" prefHeight="28.0" text="Button" AnchorPane.bottomAnchor="1.0" AnchorPane.leftAnchor="277.0" AnchorPane.topAnchor="1.0" />
     *             <Button mnemonicParsing="false" prefHeight="28.0" prefWidth="56.0" text="redact" AnchorPane.bottomAnchor="1.0" AnchorPane.leftAnchor="329.0" AnchorPane.topAnchor="1.0" />
     *          </children>
     *       </AnchorPane>
     * @param patchFile
     * @return AnchorPane
     */
    private AnchorPane buildPathFilePane(PatchFile patchFile, EventHandler<MouseEvent> lockHandler){
        // Создаем AnchorPane
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(28.0);
        anchorPane.setPrefWidth(375.0);

        // Создаем Label
        Label label = new Label(patchFile.getFileName());
        label.setPrefHeight(26.0);
        label.setPrefWidth(270.0);
        AnchorPane.setTopAnchor(label, 1.0);
        AnchorPane.setBottomAnchor(label, 1.0);
        AnchorPane.setLeftAnchor(label, 3.0);

        // Создаем первую кнопку
        Button lockButton = new Button();

        if (patchFile.isImmutable()) {
            FXUtil.setBackground(lockButton, "lock_button.png");
        } else {
            FXUtil.setBackground(lockButton, "unlock_button.png");
        }
        lockButton.setPrefHeight(28.0);
        lockButton.setPrefWidth(28.0);
        AnchorPane.setTopAnchor(lockButton, 1.0);
        AnchorPane.setBottomAnchor(lockButton, 1.0);
        AnchorPane.setLeftAnchor(lockButton, 275.0);
        lockButton.addEventHandler(MouseEvent.MOUSE_CLICKED, lockHandler);

        // Добавляем элементы в AnchorPane
        anchorPane.getChildren().addAll(label, lockButton);

        String fileExtension = FileUtils.getFileExtension(patchFile.getFileName());
        if (editFileTypes.contains(fileExtension)){
            // Создаем вторую кнопку
            Button redactButton = new Button();

            switch (fileExtension){
                case "ogg":
                case "wav" : FXUtil.setBackground(redactButton, "play_sound_button.png");
                    break;
                default: FXUtil.setBackground(redactButton, "redact_button.png");
            }

            redactButton.setPrefHeight(28.0);
            redactButton.setPrefWidth(28.0);
            AnchorPane.setTopAnchor(redactButton, 1.0);
            AnchorPane.setBottomAnchor(redactButton, 1.0);
            AnchorPane.setLeftAnchor(redactButton, 327.0);

            redactButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                switch (fileExtension) {
                    case "log", "txt" -> new TextEditor(patchFile.getFile());
                    case "dat" -> new Thread(datFileTask(patchFile)).start();
                }
            });


            redactButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> Platform.runLater(() -> FXUtil.setBackground(redactButton, "redact_button_press.png")));
            redactButton.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> Platform.runLater(() ->FXUtil.setBackground(redactButton, "redact_button.png")));


            anchorPane.getChildren().add(redactButton);
        }

        return anchorPane;
    }


    private Task<Void> datFileTask(PatchFile patchFile){
        return new Task<>() {
            private DatFile datFile;
            private ProgressBar progressBar;
            private int progress = 0;
            @Override
            protected Void call() {
                progressBar = controller.getPROGRESS_BAR();
                Platform.runLater(() -> {
                    progressBar.setVisible(true);
                    progressBar.progressProperty().bind(this.progressProperty());
                });
                updateProgressBar("Создание файла");
                datFile = new DatFile(patchFile.getFile(), controller.getPROGRESS_BAR());
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
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    controller.getPROGRESS_LABLE().setText("");
                });
            }

            private void updateProgressBar(String progressInfo){
                progress++;
                updateProgress(progress, 7);
                Platform.runLater(() -> controller.getPROGRESS_LABLE().setText(progressInfo));
            }
        };
    }


}
