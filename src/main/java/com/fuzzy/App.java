package com.fuzzy;

import com.fuzzy.config.AppConfig;
import com.fuzzy.config.configsystem.ConfigBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private TrayIcon trayIcon;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("App.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 550);
        stage.setTitle("LauncherFileUpdate");
        stage.getIcons().add(new Image(getClass().getResource("background/icons/mipmap-hdpi/ic_launcher.png").toString()));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        ConfigBuilder.load(AppConfig.class, false);
        launch();
    }

}
