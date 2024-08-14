package com.fuzzy.windows;

import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.fx_controllers.DatEditorController;
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

    private final DatFile datFile;
    private final DatEditorController controller;
    private DatFile.Data selectedData;


    public DatEditor(DatFile datFile)  {
        this.datFile = datFile;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("DatEditor.fxml"));
        try {
            setScene(new Scene(fxmlLoader.load(), 800, 600));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller = fxmlLoader.getController();

        setTitle(datFile.getFile().getName());
        initAllDataElements(datFile.getData());


        TextField textFilter = controller.getTEXT_FILTER();
        textFilter.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> initAllDataElements(datFile.filterDataByName(textFilter.getText())));

        TextField idFilter = controller.getID_FILTER();
        idFilter.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            initAllDataElements(datFile.filterDataById(idFilter.getText()));
        });

        Button duplicateButton = (Button) getScene().lookup("#DUPLICATE_BUTTON");
        duplicateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            if (selectedData != null) {
                DatFile.Data clone = selectedData.clone();
                if (datFile.getIdColumn() != null) {
                    String datum = clone.getData()[datFile.getIdColumn().getIndex()];
                    clone.getData()[datFile.getIdColumn().getIndex()] = datum + "-clone";
                }
                if (datFile.getNameColumn() != null) {
                    String datum = clone.getData()[datFile.getNameColumn().getIndex()];
                    clone.getData()[datFile.getNameColumn().getIndex()] = datum + "-clone";
                }
                datFile.getData().add(clone);
                setDataFromViewer(clone);
                refreshTotalData();
                initAllDataElements(datFile.filterDataByName(textFilter.getText()));
            }
        });

        Button deleteButton = (Button) getScene().lookup("#DELETE_BUTTON");
        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            if (selectedData != null) {
                datFile.getData().remove(selectedData);

                ScrollPane scrollPane = (ScrollPane) getScene().lookup("#ELEMENT_DATA_SCROLLPANE");
                ListView listView = (ListView) scrollPane.getContent();
                listView.getItems().clear();

                refreshTotalData();
                initAllDataElements(datFile.filterDataByName(textFilter.getText()));
            }
        });

        Button saveButton = (Button) getScene().lookup("#SAVE_BUTTON");
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> new Thread(saveTask()).start());
        refreshTotalData();
    }





    private void refreshTotalData() {
        Label totalData = (Label) getScene().lookup("#TOTAL_DATA");
        totalData.setText("count:" + datFile.getData().size());
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


    private void setDataFromViewer(DatFile.Data data) {
        ScrollPane scrollPane = (ScrollPane) getScene().lookup("#ELEMENT_DATA_SCROLLPANE");
        ListView listView = (ListView) scrollPane.getContent();
        listView.getItems().clear();

        for (int i = 0; i < datFile.getCols().length; i++) {

            int index = i;

            String currentData = data.getData()[index];
            String columnName = datFile.getCols()[index];

            AnchorPane anchorPane = new AnchorPane();

            Label label = new Label();
            double preferredLabelWidth = calculateMaxWidthFromText(label.getFont().getSize() / 2, datFile.getCols());
            label.setPrefWidth(preferredLabelWidth);
            label.setPrefHeight(20);
            label.setText(columnName);

            TextField textField = new TextField();
            double textFieldWidth = calculateMaxWidthFromText(textField.getFont().getSize() / 1.8, data.getData());
            textField.setPrefWidth(textFieldWidth < 200 ? 200 : textFieldWidth);
            textField.setPrefHeight(20);
            textField.setLayoutX(preferredLabelWidth);
            textField.setText(currentData);

            textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> data.getData()[index] = textField.getText());

            anchorPane.getChildren().add(label);
            anchorPane.getChildren().add(textField);
            listView.getItems().add(anchorPane);
        }
    }

    private void initAllDataElements(List<DatFile.Data> dataList) {
        ScrollPane scrollPane = (ScrollPane) getScene().lookup("#ALL_ELEMENTS_SCROLLPANE");
        ListView listView = (ListView) scrollPane.getContent();
        listView.getItems().clear();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Выбранный элемент: " + newValue);
            // Здесь можете добавить любое действие, которое хотите выполнить при выборе элемента
        });

        dataList.forEach(data -> {
            Label label = new Label();
            String labelText = "";
            if (datFile.getIdColumn() != null) {
                labelText += "ID: " + data.getData()[datFile.getIdColumn().getIndex()];
            }
            if (datFile.getNameColumn() != null) {
                labelText += " NAME: " + data.getData()[datFile.getNameColumn().getIndex()];
            }
            if (labelText.isEmpty()) {
                labelText += datFile.getCols()[0] + ": " + data.getData()[0];
            }
            label.setText(labelText);
            label.setPrefHeight(20);
            label.setPrefWidth(240);

            label.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                selectedData = data;
                setDataFromViewer(data);
            });

            listView.getItems().add(label);
        });
    }


    private Task<Void> saveTask(){
        return new Task<>() {
            @Override
            protected Void call() {
                File file = new File(AppConfig.TEMP_DIRECTORY + datFile.getFile().getName().replace(".dat", ".txt"));
                datFile.saveFile(file);
                FileUtils.copyFile(Path.of(AppConfig.TEMP_DIRECTORY + file.getName().replace(".txt", ".dat")),
                        Path.of(AppConfig.CLIENT_DIRECTORY + "/" + file.getName().replace(".txt", ".dat")));
                FXUtil.successWindow("File " + file.getName() + " saved!");
                return null;
            }
        };
    }


}
