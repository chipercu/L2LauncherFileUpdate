package com.fuzzy.utils;

import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.fx_controllers.SuccessWindowController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class FXUtil {

    public static void setBackground(Region region, String img) {
        final URL resource = App.class.getResource("background/" + img);
        if (resource == null) {
            return;
        }
        BackgroundImage mainBackground = new BackgroundImage(
                new Image(resource.toString()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(0, 0, false, false, false, true));
        region.setBackground(new Background(mainBackground));
    }

    public static void addEventHandler(){

    }

    public static void treeView(){
        File rootDir = new File(AppConfig.CLIENT_DIRECTORY);
        TreeItem<AnchorPane> rootItem = new TreeItem<>(buildTreeItem(rootDir));

        // Заполнение дерева
        buildTree(rootItem, rootDir);

        // Создаем TreeView
        TreeView<AnchorPane> treeView = new TreeView<>(rootItem);
        rootItem.setExpanded(true); // Раскрываем корень

        // Создаем сцену и добавляем TreeView
        StackPane root = new StackPane();
        root.getChildren().add(treeView);
        Scene scene = new Scene(root, 400, 300);

        // Настраиваем и показываем Stage
        Stage stage = new Stage();
        stage.setTitle("Структура папок");
        stage.setScene(scene);
        stage.show();
    }

    private static void buildTree(TreeItem<AnchorPane> treeItem, File file) {
        // Проверяем, является ли file директория
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                if (child.isDirectory()){
                    TreeItem<AnchorPane> childItem = new TreeItem<>(buildTreeItem(child));
                    treeItem.getChildren().add(childItem);
                    // Рекурсивно добавляем дочерние элементы
                    buildTree(childItem, child);
                }
            }
        }
    }

    private static AnchorPane buildTreeItem(File file){
        AnchorPane anchorPane = new AnchorPane();
//        setBackground(anchorPane, "account_exit.png");
        Label label = new Label();
        label.setPrefWidth(200);
        label.setPrefHeight(20);
        label.setText(file.getName());
        anchorPane.getChildren().add(label);
        return anchorPane;
    }


    public static void successWindow(String text){
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Success.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 200, 100);
                stage.setScene(scene);
                stage.initStyle(StageStyle.UNDECORATED);
                SuccessWindowController controller = fxmlLoader.getController();
                controller.getTEXT().setText(text);
                controller.getCONFIRM_BUTTON().setOnAction(actionEvent -> stage.close());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
