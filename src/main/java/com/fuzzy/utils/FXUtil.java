package com.fuzzy.utils;

import com.fuzzy.App;
import com.fuzzy.config.AppConfig;
import com.fuzzy.fx_controllers.SuccessWindowController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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

    public static void setButtonBackground(Region region, String img, String over_img, String tooltipText){
        setBackground(region, img);
        if (tooltipText != null){
            setTooltip(region, tooltipText);
        }
        region.setOnMouseEntered(event -> setBackground(region, over_img));
        region.setOnMouseExited(event -> setBackground(region, img));
    }

    public static void setTooltip(Region region, String text){
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.seconds(0.5));
        tooltip.setHideDelay(Duration.seconds(0.1));

        // Установка обработчиков мыши для настройки позиции Tooltip
        region.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
            tooltip.setAnchorX(region.getLayoutX()); // Позиция ниже region
            tooltip.setAnchorY(region.getLayoutY() + region.getHeight() + 10); // Позиция ниже region
            Tooltip.install(region, tooltip);
        });
        region.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            Tooltip.uninstall(region, tooltip); // Убираем Tooltip при уходе мыши
        });
    }

    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Убираем заголовок
        alert.showAndWait(); // Ожидаем закрытия диалога
    }

    public static void showSuccessDialog(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Убираем заголовок
        alert.showAndWait(); // Ожидаем закрытия диалога
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
                stage.setOnCloseRequest(event -> stage.close());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
