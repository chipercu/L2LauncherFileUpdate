package com.fuzzy.windows;

import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.crypt.L2CryptUtil;
import com.fuzzy.fx_controllers.DatEditorController;
import com.fuzzy.model.CsvFile;
import com.fuzzy.model.file_edit.dat.DatFile;
import com.fuzzy.utils.FXUtil;
import com.fuzzy.utils.FileUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatEditor extends Stage {

    private final File file;
    private final CsvFile csv;
    private final DatEditorController controller;

    public DatEditor(File file, CsvFile csv)  {
        this.file = file;
        this.csv = csv;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("DatEditor.fxml"));
        try {
            setScene(new Scene(fxmlLoader.load(), 800, 600));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller = fxmlLoader.getController();

        setTitle(csv.getCsvFile().getName());
        initAllDataElements(csv);


        TextField textFilter = controller.getTEXT_FILTER();
        TextField idFilter = controller.getID_FILTER();

        Button duplicateButton = (Button) getScene().lookup("#DUPLICATE_BUTTON");
//        duplicateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
//            if (selectedData != null) {
//                DatFile.Data clone = selectedData.clone();
//                if (datFile.getIdColumn() != null) {
//                    String datum = clone.getData()[datFile.getIdColumn().getIndex()];
//                    clone.getData()[datFile.getIdColumn().getIndex()] = datum + "-clone";
//                }
//                if (datFile.getNameColumn() != null) {
//                    String datum = clone.getData()[datFile.getNameColumn().getIndex()];
//                    clone.getData()[datFile.getNameColumn().getIndex()] = datum + "-clone";
//                }
//                datFile.getData().add(clone);
//                setDataFromViewer(clone);
//                refreshTotalData();
//                initAllDataElements(datFile.filterDataByName(textFilter.getText()));
//            }
//        });

        Button deleteButton = (Button) getScene().lookup("#DELETE_BUTTON");
//        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
//            if (selectedData != null) {
//                datFile.getData().remove(selectedData);
//
//                ScrollPane scrollPane = (ScrollPane) getScene().lookup("#ELEMENT_DATA_SCROLLPANE");
//                ListView listView = (ListView) scrollPane.getContent();
//                listView.getItems().clear();
//
//                refreshTotalData();
//                initAllDataElements(datFile.filterDataByName(textFilter.getText()));
//            }
//        });

        Button saveButton = (Button) getScene().lookup("#SAVE_BUTTON");
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            csv.save();
            L2CryptUtil.asmDat(file);
            L2CryptUtil.encryptDat(file);
        });
        refreshTotalData();
    }





    private void refreshTotalData() {
        Label totalData = (Label) getScene().lookup("#TOTAL_DATA");
        totalData.setText("count:" + csv.getObjects().size());
    }

    private double calculateMaxWidthFromText(double fontSize, String[] elements) {
        String longest = "";
        for (String str : elements) {
            if (str.length() > longest.length()) {
                longest = str;
            }
        }
        return longest.length() * fontSize;
    }


    private void setDataFromViewer(String[] object) {
        ListView<AnchorPane> elementDataViewer = controller.getELEMENT_DATA_VIEWER();
        elementDataViewer.getItems().clear();

        for (int i = 0; i < csv.getHeaders().length; i++) {

            int index = i;

            String currentData = object[index];
            String columnName = csv.getHeaders()[index];

            AnchorPane anchorPane = new AnchorPane();

            Label label = new Label();
            double preferredLabelWidth = calculateMaxWidthFromText(label.getFont().getSize() / 2, csv.getHeaders());
            label.setPrefWidth(preferredLabelWidth);
            label.setPrefHeight(20);
            label.setText(columnName);

            TextField textField = new TextField();
            double textFieldWidth = calculateMaxWidthFromText(textField.getFont().getSize() / 1.8, object);
            textField.setPrefWidth(textFieldWidth < 200 ? 200 : textFieldWidth);
            textField.setPrefHeight(20);
            textField.setLayoutX(preferredLabelWidth);
            textField.setText(currentData);

            textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> object[index] = textField.getText());

            anchorPane.getChildren().add(label);
            anchorPane.getChildren().add(textField);
            elementDataViewer.getItems().add(anchorPane);
        }
    }

    private void initAllDataElements(CsvFile csv) {

        ListView<Label> allElementsViewer = controller.getALL_ELEMENTS_VIEWER();
        allElementsViewer.getItems().clear();

        csv.getObjects().forEach(strings -> {
            Label label = new Label();
            String labelText = strings[0];
            label.setText(labelText);
            label.setPrefHeight(20);
            label.setPrefWidth(240);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> setDataFromViewer(strings));
            allElementsViewer.getItems().add(label);
        });
    }


//    private Task<Void> saveTask(){
//        return new Task<>() {
//            @Override
//            protected Void call() {
//                File file = new File(AppConfig.TEMP_DIRECTORY + datFile.getFile().getName().replace(".dat", ".txt"));
//                datFile.saveFile(file);
//                FileUtils.copyFile(Path.of(AppConfig.TEMP_DIRECTORY + file.getName().replace(".txt", ".dat")),
//                        Path.of(AppConfig.CLIENT_DIRECTORY + "/" + file.getName().replace(".txt", ".dat")));
//                FXUtil.successWindow("File " + file.getName() + " saved!");
//                return null;
//            }
//        };
//    }


}
